resolvers ++= DefaultOptions.resolvers(snapshot = true)

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("2.0.4"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.7.0-RC8"))

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")
