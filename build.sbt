
val releaseVersion = "0.9.0"

val projectTitle = "ScalaTest + Play" // for scaladoc source urls

name := projectTitle

version := releaseVersion

scalaVersion := "2.10.3"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.0",
  "com.typesafe.play" %% "play-test" % "2.2.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.38.0"
)

testOptions in Test += Tests.Argument("-oTK")

parallelExecution in Test := false

scalacOptions in (Compile, doc) := Seq("-doc-title", projectTitle + ", " + releaseVersion)

