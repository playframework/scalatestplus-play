resolvers ++= DefaultOptions.resolvers(snapshot = true)

resolvers += Resolver.typesafeRepo("releases")

addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("1.3.19"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.6.25"))

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")
