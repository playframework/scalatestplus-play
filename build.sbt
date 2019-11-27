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

val SeleniumVersion        = "3.141.59"
val HtmlUnitVersion        = "2.36.0"
val PhantomJsDriverVersion = "1.4.4"
val MockitoVersion         = "2.18.3"
val CssParserVersion       = "1.5.0"
val ScalatestVersion       = "3.0.8"

playBuildRepoName in ThisBuild := "scalatestplus-play"
resolvers in ThisBuild += Resolver.sonatypeRepo("releases")

lazy val mimaSettings = Seq(
  mimaPreviousArtifacts := {
    if (scalaVersion.value.equals(scala213)) Set.empty // TODO: update to 5.0.0 once released
    else Set(organization.value %% name.value % "4.0.0")
  }
)

lazy val commonSettings = mimaSettings ++ Seq(
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

lazy val `scalatestplus-play` = project
  .in(file("module"))
  .enablePlugins(Playdoc, PlayLibrary, PlayReleaseBase)
  .configs(Docs)
  .settings(
    organization := "org.scalatestplus.play",
    libraryDependencies ++= Seq(
      ws,
      akkaHttpServer             % Test,
      "com.typesafe.play"        %% "play-test" % PlayVersion.current,
      "org.scalatest"            %% "scalatest" % ScalatestVersion,
      "org.seleniumhq.selenium"  % "selenium-java" % SeleniumVersion,
      "org.seleniumhq.selenium"  % "htmlunit-driver" % HtmlUnitVersion,
      "net.sourceforge.htmlunit" % "htmlunit-cssparser" % CssParserVersion,
      "com.codeborne"            % "phantomjsdriver" % PhantomJsDriverVersion
    ),
    scalacOptions in (Compile, doc) := Seq("-doc-title", "ScalaTest + Play, " + releaseVersion),
    pomExtra := PomExtra,
    mimaBinaryIssueFilters ++= Seq(
      ProblemFilters.exclude[ReversedMissingMethodProblem](
        "org.scalatestplus.play.BaseOneServerPerTest.org$scalatestplus$play$BaseOneServerPerTest$_setter_$org$scalatestplus$play$BaseOneServerPerTest$$lock_="
      ),
      ProblemFilters.exclude[ReversedMissingMethodProblem](
        "org.scalatestplus.play.BaseOneServerPerTest.org$scalatestplus$play$BaseOneServerPerTest$$lock"
      ),
      // Using org.scalatestplus.selenium.WebBrowser instead of deprecated org.scalatest.selenium.WebBrowser
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.checkbox"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.clickOn"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.colorField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateTimeLocalField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.emailField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.find"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.findAll"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.frame"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.frame$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.goTo"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.monthField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.multiSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.numberField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.pwdField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.radioButton"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.rangeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.searchField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.singleSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.switchTo"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.switchTo$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.telField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.textArea"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.textField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.timeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.urlField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.weekField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.checkbox"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.clickOn"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.colorField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateTimeLocalField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.emailField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.find"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.findAll"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.frame"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.frame$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.goTo"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.monthField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.multiSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.numberField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.pwdField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.radioButton"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.rangeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.searchField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.singleSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.switchTo"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.switchTo$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.telField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.textArea"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.textField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.timeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.urlField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.weekField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.checkbox"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.clickOn"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.colorField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateTimeLocalField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.emailField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.find"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.findAll"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.frame"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.frame$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.goTo"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.monthField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.multiSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.numberField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.pwdField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.radioButton"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.rangeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.searchField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.singleSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.switchTo"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.switchTo$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.telField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.textArea"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.textField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.timeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.urlField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.weekField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.checkbox"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.clickOn"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.colorField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.dateField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.dateTimeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.dateTimeLocalField"
      ),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.emailField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.find"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.findAll"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.frame"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.frame$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.goTo"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.monthField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.multiSel"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.numberField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.pwdField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.radioButton"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.rangeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.searchField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.singleSel"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.switchTo"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.switchTo$default$3"
      ),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.telField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.textArea"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.textField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.timeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.urlField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.weekField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.checkbox"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.clickOn"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.colorField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateTimeLocalField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.emailField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.find"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.findAll"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.frame"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.frame$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.goTo"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.monthField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.multiSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.numberField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.pwdField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.radioButton"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.rangeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.searchField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.singleSel"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.switchTo"),
      ProblemFilters
        .exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.switchTo$default$3"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.telField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.textArea"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.textField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.timeField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.urlField"),
      ProblemFilters.exclude[IncompatibleMethTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.weekField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.ClassNameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.CssSelectorQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.Dimension"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.IdQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.LinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.NameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.PartialLinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.Point"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.TagNameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.XPathQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.activeElement"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.add"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.alertBox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.capture"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.checkbox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.className"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.click"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.colorField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.cookie"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.cookies"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.cssSelector"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.dateTimeLocalField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.defaultContent"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.delete"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.emailField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.frame"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.go"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.id"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.linkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.monthField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.multiSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.name"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.numberField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.partialLinkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.pwdField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.radioButton"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.radioButtonGroup"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.rangeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.searchField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.singleSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.switch"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.tagName"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.telField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.textArea"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.textField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.timeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.urlField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.weekField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.window"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Chrome.xpath"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.ClassNameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.CssSelectorQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.Dimension"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.IdQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.LinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.NameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.PartialLinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.Point"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.TagNameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.XPathQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.activeElement"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.add"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.alertBox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.capture"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.checkbox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.className"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.click"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.colorField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.cookie"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.cookies"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.cssSelector"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.dateTimeLocalField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.defaultContent"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.delete"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.emailField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.frame"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.go"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.id"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.linkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.monthField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.multiSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.name"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.numberField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.partialLinkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.pwdField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.radioButton"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.radioButtonGroup"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.rangeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.searchField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.singleSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.switch"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.tagName"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.telField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.textArea"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.textField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.timeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.urlField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.weekField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.window"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Firefox.xpath"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.ClassNameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.CssSelectorQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.Dimension"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.IdQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.LinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.NameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.PartialLinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.Point"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.TagNameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.XPathQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.activeElement"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.add"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.alertBox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.capture"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.checkbox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.className"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.click"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.colorField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.cookie"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.cookies"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.cssSelector"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.dateTimeLocalField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.defaultContent"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.delete"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.emailField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.frame"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.go"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.id"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.linkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.monthField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.multiSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.name"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.numberField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.partialLinkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.pwdField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.radioButton"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.radioButtonGroup"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.rangeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.searchField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.singleSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.switch"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.tagName"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.telField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.textArea"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.textField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.timeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.urlField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.weekField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.window"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.xpath"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.ClassNameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.CssSelectorQuery"
      ),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.Dimension"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.IdQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.LinkTextQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.NameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.PartialLinkTextQuery"
      ),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.Point"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.TagNameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.XPathQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.activeElement"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.add"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.alertBox"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.capture"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.checkbox"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.className"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.click"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.colorField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.cookie"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.cookies"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.cssSelector"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.dateField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.dateTimeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.dateTimeLocalField"
      ),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.defaultContent"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.delete"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.emailField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.frame"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.go"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.id"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.linkText"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.monthField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.multiSel"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.name"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.numberField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.partialLinkText"
      ),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.pwdField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.radioButton"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem](
        "org.scalatestplus.play.MixedFixtures#InternetExplorer.radioButtonGroup"
      ),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.rangeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.searchField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.singleSel"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.switch"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.tagName"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.telField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.textArea"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.textField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.timeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.urlField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.weekField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.window"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.xpath"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.ClassNameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.CssSelectorQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.Dimension"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.IdQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.LinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.NameQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.PartialLinkTextQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.Point"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.TagNameQuery"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.XPathQuery"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.activeElement"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.add"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.alertBox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.capture"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.checkbox"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.className"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.click"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.colorField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.cookie"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.cookies"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.cssSelector"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateTimeField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.dateTimeLocalField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.defaultContent"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.delete"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.emailField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.frame"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.go"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.id"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.linkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.monthField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.multiSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.name"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.numberField"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.partialLinkText"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.pwdField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.radioButton"),
      ProblemFilters
        .exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.radioButtonGroup"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.rangeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.searchField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.singleSel"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.switch"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.tagName"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.telField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.textArea"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.textField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.timeField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.urlField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.weekField"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.window"),
      ProblemFilters.exclude[IncompatibleResultTypeProblem]("org.scalatestplus.play.MixedFixtures#Safari.xpath"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Chrome.find"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Chrome.findAll"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Firefox.find"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Firefox.findAll"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.find"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#HtmlUnit.findAll"),
      ProblemFilters
        .exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.find"),
      ProblemFilters
        .exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#InternetExplorer.findAll"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Safari.find"),
      ProblemFilters.exclude[IncompatibleSignatureProblem]("org.scalatestplus.play.MixedFixtures#Safari.findAll"),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$$TagMeta"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$$targetDir"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$$targetDir_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$activeElement_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$alertBox_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$cookies_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$defaultContent_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$$TagMeta"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$$targetDir"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$$targetDir_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$activeElement_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$alertBox_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$cookies_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.AllBrowsersPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$defaultContent_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$$TagMeta"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$$targetDir"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$$targetDir_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$_setter_$activeElement_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$_setter_$alertBox_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$_setter_$cookies_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.ConfiguredBrowser.org$scalatestplus$selenium$WebBrowser$_setter_$defaultContent_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$$TagMeta"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$$targetDir"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$$targetDir_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$activeElement_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$alertBox_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$cookies_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerSuite.org$scalatestplus$selenium$WebBrowser$_setter_$defaultContent_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$$TagMeta"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$$targetDir"
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$$targetDir_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$activeElement_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$alertBox_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$cookies_="
      ),
      ProblemFilters.exclude[InheritedNewAbstractMethodProblem](
        "org.scalatestplus.play.OneBrowserPerTest.org$scalatestplus$selenium$WebBrowser$_setter_$defaultContent_="
      ),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.AllBrowsersPerSuite"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.AllBrowsersPerTest"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.ConfiguredBrowser"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedFixtures$Chrome"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedFixtures$Firefox"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedFixtures$HtmlUnit"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedFixtures$InternetExplorer"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.MixedFixtures$Safari"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.OneBrowserPerSuite"),
      ProblemFilters.exclude[MissingTypesProblem]("org.scalatestplus.play.OneBrowserPerTest")
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
  .settings(commonSettings: _*)
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

addCommandAlias("validateCode", ";scalafmtCheckAll;scalafmtSbtCheck")
