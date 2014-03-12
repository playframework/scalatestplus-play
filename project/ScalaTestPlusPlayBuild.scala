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
import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtPgp._

object ScalaTestPlusPlayBuild extends Build {

  val releaseVersion = "0.9.0"
  val projectTitle = "ScalaTest + Play" // for scaladoc source urls

  def envVar(name: String): String =
    try {
      sys.env(name)
    }
    catch {
      case e: NoSuchElementException => "Environment variable '" + name + "' not specified."
    }


  val buildSettings = Defaults.defaultSettings ++ Seq(

    name := "plusplay",

    organization := "org.scalatest",

    version := releaseVersion,

    scalaVersion := "2.10.3",

    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",

    resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",

    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.1.0",
      "com.typesafe.play" %% "play-test" % "2.2.2",
      "org.seleniumhq.selenium" % "selenium-java" % "2.38.0"
    ),

    testOptions in Test += Tests.Argument("-oTK"),

    parallelExecution in Test := false,

    scalacOptions in (Compile, doc) := Seq("-doc-title", projectTitle + ", " + releaseVersion)
  )

  val sonatypeSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },

    credentials += Credentials(
      "Sonatype Nexus Repository Manager", "oss.sonatype.org", envVar("SCALATEST_NEXUS_LOGIN"), envVar("SCALATEST_NEXUS_PASSWORD")),
    pgpSecretRing := file(envVar("SCALATEST_GPG_FILE")),
    // pgpPassphrase := Some(envVar("SCALATEST_GPG_PASSPHASE").toCharArray),

    pomExtra := (
      <url>http://www.scalatest.org/plus/play</url>
      <licenses>
        <license>
          <name>The Apache License, ASL Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses> 
      <scm>
        <url>https://github.com/scalatest/scalatest</url>
        <connection>scm:git:git@github.com:scalatest/scalatest.git</connection>
        <developerConnection>
          scm:git:git@github.com:scalatest/scalatest.git
        </developerConnection>
      </scm>
      <developers>
        <developer>
          <id>bvenners</id>
          <name>Bill Venners</name>
          <email>bill@artima.com</email>
        </developer>
        <developer>
          <id>gcberger</id>
          <name>George Berger</name>
          <email>george.berger@gmail.com</email>
        </developer>
        <developer>
          <id>cheeseng</id>
          <name>Chua Chee Seng</name>
          <email>cheeseng@amaseng.com</email>
        </developer>
      </developers>
      <parent>
        <groupId>org.scalatest</groupId>
        <artifactId>plus-play</artifactId>
        <version>0.0.9</version>
      </parent>
      ))
  lazy val root = Project(
    "ScalaTestPlusPlay",
    file("."),
    settings = buildSettings ++ sonatypeSettings
  )
}
