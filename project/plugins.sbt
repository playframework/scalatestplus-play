resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"), // used by deploy nightlies, which publish here & use -Dplay.version
)

addSbtPlugin("com.typesafe.play" % "interplay"            % sys.props.getOrElse("interplay.version", "3.1.5"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin"           % sys.props.getOrElse("play.version", "2.9.0-M7"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.9.0-M7"))

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.5.2")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.1.3")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.10.0")
