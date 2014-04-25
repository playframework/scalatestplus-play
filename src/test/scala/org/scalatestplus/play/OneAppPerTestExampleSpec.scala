
import org.scalatest._
import org.scalatestplus.play._

@DoNotDiscover class OneSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class TwoSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class RedSpec extends PlaySpec with ConfiguredApp

@DoNotDiscover class BlueSpec extends PlaySpec with ConfiguredApp

class OneAppPerSuiteExampleSpec extends Suites(
  new OneSpec,
  new TwoSpec,
  new RedSpec,
  new BlueSpec
) with OneAppPerSuite

