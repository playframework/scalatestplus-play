
name := "ScalaTestPlus-Play"

version := "0.9.0"

scalaVersion := "2.10.3"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.2.2" % "provided",
  "org.scalatest" %% "scalatest" % "2.1.0",
  "com.typesafe.play" %% "play-test" % "2.2.2",
  "org.seleniumhq.selenium" % "selenium-java" % "2.38.0"
)

