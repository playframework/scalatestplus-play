/*
 * Copyright 2001-2014 Artima, Inc.
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

val PlayVersion = playVersion("2.5.0-RC1")

lazy val `scalatestplus-play-root` = project
  .in(file("."))
  .enablePlugins(PlayRootProject)
  .aggregate(`scalatestplus-play`)
  .settings(
    sonatypeProfileName := "org.scalatestplus.play"
  )

lazy val `scalatestplus-play` = project
  .in(file("module"))
  .enablePlugins(Playdoc, PlayLibrary, PlayReleaseBase)
  .settings(
    organization := "org.scalatestplus.play",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6",
      "com.typesafe.play" %% "play-test" % PlayVersion,
      "org.seleniumhq.selenium" % "selenium-java" % "2.48.2",
      "com.machinepublishers" % "jbrowserdriver" % "0.10.1",
      "com.typesafe.play" %% "play-ws" % PlayVersion,
      "com.typesafe.play" %% "play-cache" % PlayVersion % Test
    ),
    parallelExecution in Test := false,
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oTK"),

    scalacOptions in (Compile, doc) := Seq("-doc-title", "ScalaTest + Play, " + releaseVersion),
    
    pomExtra := PomExtra
  )

lazy val docs = project
  .in(file("docs"))
  .enablePlugins(PlayDocsPlugin, PlayNoPublish)
  .settings(
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-cache" % PlayVersion % Test,
      "org.mockito" % "mockito-core" % "1.9.5" % Test
    ),
    
    parallelExecution in Test := false,

    PlayDocsKeys.scalaManualSourceDirectories := (baseDirectory.value / "manual" / "working" / "scalaGuide" ** "code").get,
    PlayDocsKeys.resources += {
      val apiDocs = (doc in (`scalatestplus-play`, Compile)).value
      // Copy the docs to a place so they have the correct api/scala prefix
      val apiDocsStage = target.value / "api-docs-stage"
      val cacheFile = streams.value.cacheDirectory / "api-docs-stage"
      val mappings = (apiDocs.***.filter(!_.isDirectory).get pair relativeTo(apiDocs)).map {
        case (file, path) => file -> apiDocsStage / "api" / "scala" / path
      }
      Sync(cacheFile)(mappings)
      PlayDocsDirectoryResource(apiDocsStage)
    },
    SettingKey[Seq[File]]("migrationManualSources") := Nil
  )
  .dependsOn(`scalatestplus-play`)
  
playBuildRepoName in ThisBuild := "scalatestplus-play"

lazy val PomExtra = {
  <scm>
    <url>https://github.com/scalatest/scalatestplus-play</url>
    <connection>scm:git:git@github.com:scalatest/scalatest.git</connection>
    <developerConnection>
      scm:git:git@github.com:scalatest/scalatest.git
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

