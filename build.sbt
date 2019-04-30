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
import interplay.ScalaVersions.scala212

// TODO remove when adopting Scala 2.13.0-RC* or GA version.
val scala213 = "2.13.0-M5"
val PlayVersion = playVersion("2.7.2")

val SeleniumVersion = "3.141.59"
val HtmlUnitVersion = "2.35.1"
val PhantomJsDriverVersion = "1.4.4"
val MockitoVersion = "2.18.3"
val CssParserVersion = "1.4.0"

def previousVersion(scalaVer: String): String  = if(scalaVer.equals(scala213))  "4.0.1" else  "4.0.0"

lazy val mimaSettings = Seq(
  mimaPreviousArtifacts := Set(organization.value %% name.value % previousVersion(scalaVersion.value))
)

def ScalatestVersion(scalaVer: String): String = if (scalaVer.equals(scala213)) "3.0.6-SNAP6" else "3.0.7"

resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers += Resolver.sonatypeRepo("snapshots")

lazy val commonSettings = mimaSettings ++ Seq(
  scalaVersion := scala212,
  crossScalaVersions := Seq(scala212, scala213),
  fork in Test := false,
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


lazy val `scalatestplus-play` = project
  .in(file("module"))
  .enablePlugins(Playdoc, PlayLibrary, PlayReleaseBase)
  .configs(Docs)
  .settings(
    organization := "org.scalatestplus.play",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion(scalaVersion.value),
      "org.seleniumhq.selenium" % "selenium-java" % SeleniumVersion exclude(org = "com.codeborne", name = "phantomjsdriver"),
      "org.seleniumhq.selenium" % "htmlunit-driver" % HtmlUnitVersion,
      "net.sourceforge.htmlunit" % "htmlunit-cssparser" % CssParserVersion,
      "com.codeborne" % "phantomjsdriver" % PhantomJsDriverVersion,
      "com.typesafe.play" %% "play-test" % PlayVersion,
      "com.typesafe.play" %% "play-ws" % PlayVersion,
      "com.typesafe.play" %% "play-ahc-ws" % PlayVersion
    ),
    scalacOptions in(Compile, doc) := Seq("-doc-title", "ScalaTest + Play, " + releaseVersion),

    pomExtra := PomExtra,

    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[ReversedMissingMethodProblem]("org.scalatestplus.play.BaseOneServerPerTest.org$scalatestplus$play$BaseOneServerPerTest$_setter_$org$scalatestplus$play$BaseOneServerPerTest$$lock_="),
      ProblemFilters.exclude[ReversedMissingMethodProblem]("org.scalatestplus.play.BaseOneServerPerTest.org$scalatestplus$play$BaseOneServerPerTest$$lock")
    )
  )
  .settings(commonSettings)

lazy val docs = project
  .in(file("docs"))
  .enablePlugins(PlayDocsPlugin, PlayNoPublish)
  .configs(Docs)
  .settings(
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % MockitoVersion % Test
    ),

    PlayDocsKeys.scalaManualSourceDirectories := (baseDirectory.value / "manual" / "working" / "scalaGuide" ** "code").get,
    PlayDocsKeys.resources += {
      val apiDocs = (doc in(`scalatestplus-play`, Compile)).value
      // Copy the docs to a place so they have the correct api/scala prefix
      val apiDocsStage = target.value / "api-docs-stage"
      val cacheFile = streams.value.cacheDirectory / "api-docs-stage"
      val mappings = (apiDocs.allPaths.filter(!_.isDirectory).get pair relativeTo(apiDocs)).map {
        case (file, path) => file -> apiDocsStage / "api" / "scala" / path
      }
      Sync.sync(CacheStore(cacheFile))(mappings)
      PlayDocsDirectoryResource(apiDocsStage)
    },
    SettingKey[Seq[File]]("migrationManualSources") := Nil
  )
  .settings(commonSettings)
  .settings(
    crossScalaVersions := Seq(scala212, scala213),
  )
  .dependsOn(`scalatestplus-play`)

playBuildRepoName in ThisBuild := "scalatestplus-play"

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

lazy val checkCodeFormat = taskKey[Unit]("Check that code format is following Scalariform rules")

checkCodeFormat := {
  val exitCode = "git diff --exit-code".!
  if (exitCode != 0) {
    sys.error(
      """
        |ERROR: Scalariform check failed, see differences above.
        |To fix, format your sources using sbt scalariformFormat test:scalariformFormat before submitting a pull request.
        |Additionally, please squash your commits (eg, use git commit --amend) if you're going to update this pull request.
        |""".stripMargin)
  }
}

addCommandAlias("validateCode",
  ";scalariformFormat;test:scalariformFormat;checkCodeFormat"
)
