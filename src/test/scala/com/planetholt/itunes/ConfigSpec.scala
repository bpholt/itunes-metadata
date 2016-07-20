package com.planetholt.itunes

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.collection.mutable

class ConfigSpec extends Specification with Mockito {

  trait Setup extends Scope {
    val errors = mutable.Buffer[String]()
    val warnings = mutable.Buffer[String]()

    val errorCapturingConfigParser = new Config.ConfigParser {
      override def reportError(msg: String): Unit = errors += msg
      override def reportWarning(msg: String): Unit = warnings += msg

    }
  }

  "Config" should {

    "accept a show name and a season using the long names" in new Setup {
      val args: Array[String] = Array("--show-name", "name", "--season", "42")

      val output = Config.parse(args, errorCapturingConfigParser)

      output must beAnInstanceOf[Option[Config]]
      output must beSome(Config("name", 42))
      errors must beEmpty
      warnings must beEmpty
    }

    "accept a show name and a season using the short names" in new Setup {
      val args: Array[String] = Array("-n", "name", "-s", "42")

      val output = Config.parse(args, errorCapturingConfigParser)

      output must beAnInstanceOf[Option[Config]]
      output must beSome(Config("name", 42))
      errors must beEmpty
      warnings must beEmpty
    }

    "return None when season is omitted" in new Setup {
      val args: Array[String] = Array("-n", "name")

      val output = Config.parse(args, errorCapturingConfigParser)
      output must beNone

      errors must contain("Missing option --season")
      warnings must beEmpty
    }

    "return None when show name is omitted" in new Setup {
      val args: Array[String] = Array("-s", "42")

      val output = Config.parse(args, errorCapturingConfigParser)
      output must beNone
      errors must contain("Missing option --show-name")
      warnings must beEmpty
    }

  }

}
