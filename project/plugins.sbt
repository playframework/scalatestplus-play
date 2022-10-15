lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0, see https://github.com/sbt/sbt/pull/7021
)
resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"), // used by deploy nightlies, which publish here & use -Dplay.version
)

addSbtPlugin("com.typesafe.play" % "interplay"            % sys.props.getOrElse("interplay.version", "3.1.0-RC5"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin"           % sys.props.getOrElse("play.version", "2.9.0-M2"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.9.0-M2"))

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.4.6")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.1.1")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.7.0")
