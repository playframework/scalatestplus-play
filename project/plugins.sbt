resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers ++= Seq(
  Resolver.typesafeRepo("releases"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"), // used by deploy nightlies, which publish here & use -Dplay.version
)

addSbtPlugin("com.typesafe.play" % "interplay"            % sys.props.getOrElse("interplay.version", "3.0.0"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin"           % sys.props.getOrElse("play.version", "2.8.1"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.8.1"))

addSbtPlugin("org.scalameta" % "sbt-scalafmt"    % "2.3.1")
addSbtPlugin("com.typesafe"  % "sbt-mima-plugin" % "0.7.0")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.0.0")
