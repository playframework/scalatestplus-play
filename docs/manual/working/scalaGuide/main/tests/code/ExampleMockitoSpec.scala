/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */
package scalaguide.tests.scalatest

// #scalatest-mockitosugar
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.*

import org.mockito.Mockito.*

class ExampleMockitoSpec extends PlaySpec with MockitoSugar {

  "MyService#isDailyData" should {
    "return true if the data is from today" in {
      val mockDataService = mock[DataService]
      when(mockDataService.findData).thenReturn(Data(new java.util.Date()))

      val myService = new MyService() {
        override def dataService = mockDataService
      }

      val actual = myService.isDailyData
      actual mustBe true
    }
  }
}
// #scalatest-mockitosugar

// #scalatest-mockito-dataservice
case class Data(retrievalDate: java.util.Date)

trait DataService {
  def findData: Data
}
// #scalatest-mockito-dataservice

class MyService {
  import java.util.*

  def dataService: DataService = null // implementation reference...

  def isDailyData: Boolean = {
    val retrievalDate = Calendar.getInstance
    retrievalDate.setTime(dataService.findData.retrievalDate)

    val today = Calendar.getInstance()

    (retrievalDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    && retrievalDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
  }
}
