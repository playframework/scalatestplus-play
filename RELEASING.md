# Releasing

## Prerequisites

Access to `vegemite.lightbend.com`

## Process

* Add the release to README.md, PR and merge to master
* Log in to `vegemite.lightbend.com`
* `cd deploy`
* `./release --project scalatestplus-play --branch master --tag 5.1.0` (notice: the tag is not prefixed with a 'v')

## Future

We'd like to improve this process so releasing is just a matter
of tagging the release in github, and having a service like Travis
actually do the release.

This should be fairly close since we're already using sbt-dynver
and sbt-release.

Help welcome!

## Background

More background on how Play artifacts are generally released can be
found at https://github.com/playframework/play-meta/blob/master/releasing/play.md
