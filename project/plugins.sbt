resolvers ++= DefaultOptions.resolvers(snapshot = true)

resolvers += Resolver.typesafeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")

addSbtPlugin("com.typesafe.play" % "interplay"            % sys.props.get("interplay.version").getOrElse("2.1.1"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin"           % sys.props.getOrElse("play.version", "2.8.0-RC1"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.8.0-RC1"))

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.2.1")

// remove this resolved when https://github.com/lightbend/mima/issues/422 is solved
resolvers += Resolver.url(
  "typesafe sbt-plugins",
  url("https://dl.bintray.com/typesafe/sbt-plugins")
)(Resolver.ivyStylePatterns)
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.6.1")
