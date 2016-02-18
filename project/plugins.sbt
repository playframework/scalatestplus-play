addSbtPlugin("com.typesafe.play" % "interplay" % sys.props.get("interplay.version").getOrElse("1.1.0"))
addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.5.0-RC1"))
