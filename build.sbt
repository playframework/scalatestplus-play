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
import sbt.util.{ Level => _, _ }

import sbt.io.Path._
import interplay.ScalaVersions._

val SeleniumVersion          = "4.12.1"
val SeleniumHtmlunitVersion  = "4.12.0"
val MockitoVersion           = "4.6.1"
val CssParserVersion         = "1.14.0"
val ScalatestVersion         = "3.2.17"
val ScalatestSeleniumVersion = ScalatestVersion + ".0"
val ScalatestMockitoVersion  = ScalatestVersion + ".0"

ThisBuild / playBuildRepoName := "scalatestplus-play"
ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("releases")

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}

val previousVersion: Option[String] = Some("6.0.0-RC2")

lazy val mimaSettings = Seq(
  mimaBinaryIssueFilters ++= Seq(
  ),
  mimaPreviousArtifacts := previousVersion.map(organization.value %% name.value % _).toSet
)

lazy val commonSettings = Seq(
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala213, scala3),
  Test / parallelExecution := false,
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oTK"),
  headerLicense := Some(
    HeaderLicense.Custom(
      """|Copyright 2001-2022 Artima, Inc.
         |
         |Licensed under the Apache License, Version 2.0 (the "License");
         |you may not use this file except in compliance with the License.
         |You may obtain a copy of the License at
         |
         |     http://www.apache.org/licenses/LICENSE-2.0
         |
         |Unless required by applicable law or agreed to in writing, software
         |distributed under the License is distributed on an "AS IS" BASIS,
         |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         |See the License for the specific language governing permissions and
         |limitations under the License.
         |""".stripMargin
    )
  )
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
  .enablePlugins(Playdoc, PlayLibrary)
  .configs(Docs)
  .settings(
    commonSettings,
    mimaSettings,
    organization := "org.scalatestplus.play",
    libraryDependencies ++= Seq(
      ws,
      nettyServer % Test, // Using netty for now, we can switch back to akkaHttpServer when it has Scala 3 artifacts
      playTest,
      "org.scalatest"            %% "scalatest"         % ScalatestVersion,
      "org.scalatestplus"        %% "mockito-4-11"      % ScalatestMockitoVersion,
      "org.scalatestplus"        %% "selenium-4-12"     % ScalatestSeleniumVersion,
      "org.seleniumhq.selenium"  % "selenium-java"      % SeleniumVersion,
      "org.seleniumhq.selenium"  % "htmlunit-driver"    % SeleniumHtmlunitVersion,
      "net.sourceforge.htmlunit" % "htmlunit-cssparser" % CssParserVersion
    ),
    Compile / doc / scalacOptions := Seq("-doc-title", "ScalaTest + Play, " + version.value),
    Test / fork := true,
    Test / javaOptions ++= List(
      "-Dwebdriver.firefox.logfile=/dev/null", // disable GeckoDriver logs polluting the CI logs
    ),
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

addCommandAlias(
  "validateCode",
  List(
    "headerCheckAll",
    "scalafmtSbtCheck",
    "scalafmtCheckAll",
  ).mkString(";")
)
