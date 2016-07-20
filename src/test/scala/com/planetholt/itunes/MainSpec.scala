package com.planetholt.itunes

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class MainSpec extends Specification with Mockito {

  trait Setup extends Scope {
    val config = Config("show", 2)
  }

  "Main" should {

    "retrieve the list of seasons" in new Setup {
    }

  }
}
