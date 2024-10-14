resolvers ++= DefaultOptions.resolvers(snapshot = true)
resolvers ++= Resolver
  .sonatypeOssRepos("snapshots") // used by deploy nightlies, which publish here & use -Dplay.version

addSbtPlugin("com.typesafe.play" % "play-docs-sbt-plugin" % sys.props.getOrElse("play.version", "2.9.5"))

addSbtPlugin("org.scalameta"     % "sbt-scalafmt"    % "2.5.2")
addSbtPlugin("com.typesafe"      % "sbt-mima-plugin" % "1.1.4")
addSbtPlugin("de.heikoseeberger" % "sbt-header"      % "5.10.0")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.8.0")
