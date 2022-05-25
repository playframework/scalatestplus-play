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
import sbt.util.{Level => _, _}

import scala.sys.process._
import sbt.io.Path._
import interplay.ScalaVersions._

import play.core.PlayVersion

val SeleniumVersion          = "4.1.3"
val HtmlUnitVersion          = "3.61.0"
val MockitoVersion           = "4.5.0"  
val CssParserVersion         = "1.11.0"
val ScalatestVersion         = "3.2.12"
val ScalatestSeleniumVersion = ScalatestVersion + ".0"
val ScalatestMockitoVersion  = ScalatestVersion + ".0"

ThisBuild / playBuildRepoName := "scalatestplus-play"
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
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
  Test / parallelExecution := false,
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oTK")
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
      // Note: It seems like the only jackson version that works with all play, play-ws, play-json and selenium is 2.11.4.
      //       play will bring in older version of selenium as well, so need excluding and let scalatest+selenium brings in the newer version.4
      ws,
      akkaHttpServer             % Test,
      "com.typesafe.play"        %% "play-test"         % PlayVersion.current exclude ("com.fasterxml.jackson.core", "jackson-core") 
                                                                              exclude ("com.fasterxml.jackson.core", "jackson-databind")
                                                                              exclude ("org.seleniumhq.selenium", "htmlunit-driver")
                                                                              exclude ("org.seleniumhq.selenium", "selenium-api")
                                                                              exclude ("org.seleniumhq.selenium", "selenium-support")
                                                                              exclude ("org.seleniumhq.selenium", "selenium-firefox-driver")
                                                                              exclude ("org.seleniumhq.selenium", "selenium-remote-driver"),
      "org.scalatest"            %% "scalatest"         % ScalatestVersion,
      "org.scalatestplus"        %% "mockito-4-5"       % ScalatestMockitoVersion,
      "org.scalatestplus"        %% "selenium-4-1"      % ScalatestSeleniumVersion exclude ("com.fasterxml.jackson.core", "jackson-core") 
                                                                                   exclude ("com.fasterxml.jackson.core", "jackson-databind"),
      "org.seleniumhq.selenium"  % "selenium-java"      % SeleniumVersion,
      "org.seleniumhq.selenium"  % "htmlunit-driver"    % HtmlUnitVersion,
      "net.sourceforge.htmlunit" % "htmlunit-cssparser" % CssParserVersion
    ),
    evictionErrorLevel := Level.Info, 
    Compile / doc / scalacOptions := Seq("-doc-title", "ScalaTest + Play, " + releaseVersion),
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
      val apiDocs = (`scalatestplus-play` / Compile / doc).value
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
