/*
 * Copyright 2001-2016 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.typesafe.tools.mima.core._
import sbt.util._

import scala.sys.process._
import sbt.io.Path._
import interplay.ScalaVersions._

import play.core.PlayVersion

val SeleniumVersion          = "3.141.59"
val HtmlUnitVersion          = "2.48.0"
val PhantomJsDriverVersion   = "1.4.4"
val MockitoVersion           = "3.2.4"
val CssParserVersion         = "1.6.0"
val ScalatestVersion         = "3.1.2"
val ScalatestSeleniumVersion = "3.1.2.0"
val ScalatestMockitoVersion  = "3.1.1.0"

playBuildRepoName in ThisBuild := "scalatestplus-play"
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
dynverVTagPrefix in ThisBuild := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  val v = version.value
  if (dynverGitDescribeOutput.value.hasNoTags)
    throw new MessageOnlyException(
      s"Failed to derive version from git tags. Maybe run `git fetch --unshallow`? Version: $v"
    )
  s
}

val previousVersion: Option[String] = Some("5.0.0")

lazy val mimaSettings = Seq(
  mimaBinaryIssueFilters ++= Seq(
    // Add mima filters here
    ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedPlaySpec"),
    ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedPlaySpec.*"),
    ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedPlaySpec.*"),
    ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.PlaySpec"),
    ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.PlaySpec.*"),
    ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.PlaySpec.*"),
    ProblemFilters.exclude[DirectMissingMethodProblem]("org.scalatestplus.play.MixedPlaySpec.*"),
  ),
  mimaPreviousArtifacts := previousVersion.map(organization.value %% name.value % _).toSet
)

lazy val commonSettings = Seq(
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala212, scala213),
  parallelExecution in Test := false,
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oTK")
)

lazy val `scalatestplus-play-root` = project
  .in(file("."))
  .enablePlugins(PlayRootProject)
  .aggregate(`scalatestplus-play`)
  .settings(commonSettings)
  .settings(
    sonatypeProfileName := "org.scalatestplus.play",
    mimaPreviousArtifacts := Set.empty
  )
  .settings(
    Seq(
      // this overrides releaseProcess to make it work with sbt-dynver
      releaseProcess := {
        import ReleaseTransformations._
        Seq[ReleaseStep](
          checkSnapshotDependencies,
          runClean,
          releaseStepCommandAndRemaining("+test"),
          releaseStepCommandAndRemaining("+publishSigned"),
          releaseStepCommand("sonatypeBundleRelease"),
          pushChanges // <- this needs to be removed when releasing from tag
        )
      }
    )
  )

lazy val `scalatestplus-play` = project
  .in(file("module"))
  .enablePlugins(Playdoc, PlayLibrary)
  .configs(Docs)
  .settings(
    commonSettings,
    mimaSettings,
    organization := "org.scalatestplus.play",
    libraryDependencies ++= Seq(
      ws,
      akkaHttpServer             % Test,
      "com.typesafe.play"        %% "play-test"         % PlayVersion.current,
      "org.scalatest"            %% "scalatest"         % ScalatestVersion,
      "org.scalatestplus"        %% "mockito-3-2"       % ScalatestMockitoVersion,
      "org.scalatestplus"        %% "selenium-3-141"    % ScalatestSeleniumVersion,
      "org.seleniumhq.selenium"  % "selenium-java"      % SeleniumVersion,
      "org.seleniumhq.selenium"  % "htmlunit-driver"    % HtmlUnitVersion,
      "net.sourceforge.htmlunit" % "htmlunit-cssparser" % CssParserVersion,
      "com.codeborne"            % "phantomjsdriver"    % PhantomJsDriverVersion
    ),
    scalacOptions in (Compile, doc) := Seq("-doc-title", "ScalaTest + Play, " + releaseVersion),
    pomExtra := PomExtra
  )

lazy val docs = project
  .in(file("docs"))
  .enablePlugins(PlayDocsPlugin, PlayNoPublish)
  .configs(Docs)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % MockitoVersion % Test,
    ),
    PlayDocsKeys.scalaManualSourceDirectories := (baseDirectory.value / "manual" / "working" / "scalaGuide" ** "code").get,
    PlayDocsKeys.resources += {
      val apiDocs = (doc in (`scalatestplus-play`, Compile)).value
      // Copy the docs to a place so they have the correct api/scala prefix
      val apiDocsStage = target.value / "api-docs-stage"
      val cacheFile    = streams.value.cacheDirectory / "api-docs-stage"
      val mappings = apiDocs.allPaths.filter(!_.isDirectory).get.pair(relativeTo(apiDocs)).map {
        case (file, path) => file -> apiDocsStage / "api" / "scala" / path
      }
      Sync.sync(CacheStore(cacheFile))(mappings)
      PlayDocsDirectoryResource(apiDocsStage)
    },
    SettingKey[Seq[File]]("migrationManualSources") := Nil
  )
  .dependsOn(`scalatestplus-play`)

lazy val PomExtra = {
  <scm>
    <url>https://github.com/playframework/scalatestplus-play</url>
    <connection>scm:git:git@github.com:playframework/scalatestplus-play.git</connection>
    <developerConnection>
      scm:git:git@github.com:playframework/scalatestplus-play.git
    </developerConnection>
  </scm>
    <developers>
      <developer>
        <id>bvenners</id>
        <name>Bill Venners</name>
      </developer>
      <developer>
        <id>gcberger</id>
        <name>George Berger</name>
      </developer>
      <developer>
        <id>cheeseng</id>
        <name>Chua Chee Seng</name>
      </developer>
    </developers>
}
