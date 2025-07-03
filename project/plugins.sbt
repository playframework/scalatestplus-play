resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers += Resolver.sonatypeCentralSnapshots // used by deploy nightlies, which publish here & use -Dplay.version

addSbtPlugin("org.playframework" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "3.0.8"))

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.5.5")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.1.4")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.10.0")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.1")
