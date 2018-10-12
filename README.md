[![Gitter](https://img.shields.io/gitter/room/gitterHQ/gitter.svg)](https://gitter.im/playframework/playframework?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [<img src="https://img.shields.io/travis/playframework/playframework.svg"/>](https://travis-ci.org/playframework/scalatestplus-play) [![Maven](https://img.shields.io/maven-central/v/org.scalatestplus.play/scalatestplus-play_2.12.svg)](http://mvnrepository.com/artifact/org.scalatestplus.play/scalatestplus-play_2.12)

# ScalaTest _Plus_ Play

ScalaTest + Play provides integration support between [ScalaTest](http://www.scalatest.org/) and [Play Framework](http://www.playframework.com).

## Installation

To use it, please add the following dependency to your project's `build.sbt` or `project/Build.scala` file:

```scala
"org.scalatestplus.play" %% "scalatestplus-play" % "{VERSION}" % "test"
```

Where version is one of the listed below, according to your needs.

## Releases

| Release | Play  | Scalatest | Documentation                                                                       |
|:--------|:------|:----------|:------------------------------------------------------------------------------------|
| 4.0.x   | 2.7.x | 3.0.x     | [docs](https://www.playframework.com/documentation/2.7.0-M4/ScalaTestingWithScalaTest) |
| 3.1.x   | 2.6.x | 3.0.x     | [docs](https://www.playframework.com/documentation/2.6.x/ScalaTestingWithScalaTest) |
| 3.0.x   | 2.6.x | 3.0.x     | [docs](https://www.playframework.com/documentation/2.6.x/ScalaTestingWithScalaTest) |
| 2.0.x   | 2.5.x | 3.0.x     | [docs](https://www.playframework.com/documentation/2.5.x/ScalaTestingWithScalaTest) |
| 1.5.x   | 2.5.x | 2.2.x     | [docs](https://www.playframework.com/documentation/2.5.x/ScalaTestingWithScalaTest) |
| 1.4.x   | 2.4.x | 2.2.x     | [docs](https://www.playframework.com/documentation/2.4.x/ScalaTestingWithScalaTest) |
| 1.3.x   | 2.2.x | 2.2.x     | [docs](https://www.playframework.com/documentation/2.2.x/ScalaTestingWithScalaTest) |

## Contributions

The following tips may be useful for all welcome contributions:

* Raise a Pull Request against the relevant branch
* The Pull Request will be validated & tested in Travis-CI
* Builds which don't meet the style guidelines will fail, we recommend running `sbt validateCode` before pushing
* Please use descriptive commit messages

See more details [here](https://playframework.com/contributing).

## License

ScalaTest _Plus_ Play is licensed under the Apache license, version 2. See the LICENSE.txt file for more information.
