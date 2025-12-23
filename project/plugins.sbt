resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers += Resolver.sonatypeCentralSnapshots // used by deploy nightlies, which publish here & use -Dplay.version

addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.9.10"))

addSbtPlugin("org.scalameta"  % "sbt-scalafmt"    % "2.5.6")
addSbtPlugin("com.typesafe"   % "sbt-mima-plugin" % "1.1.4")
addSbtPlugin("com.github.sbt" % "sbt-header"      % "5.11.0")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
