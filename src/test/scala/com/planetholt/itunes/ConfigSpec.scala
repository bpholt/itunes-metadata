package com.planetholt.itunes

import com.planetholt.itunes.Config.FilenameFormats
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

    val requiredArguments = Array("--show-name", "name", "--season", "42")

    val parseWithCurriedConfigParser: (Array[String]) ⇒ Option[Config] = Config.parse(_, errorCapturingConfigParser)
  }

  "Config" should {

    "deal with required arguments" >> {
      "accept a show name and a season using the long names" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beAnInstanceOf[Option[Config]]
        output must beSome(Config("name", 42))
        errors must beEmpty
        warnings must beEmpty
      }

      "accept a show name and a season using the short names" in new Setup {
        val args: Array[String] = Array("-n", "name", "-s", "42")

        val output = parseWithCurriedConfigParser(args)

        output must beAnInstanceOf[Option[Config]]
        output must beSome(Config("name", 42))
        errors must beEmpty
        warnings must beEmpty
      }

      "return None when season is omitted" in new Setup {
        val args: Array[String] = Array("-n", "name")

        val output = parseWithCurriedConfigParser(args)
        output must beNone

        errors must contain("Missing option --season")
        warnings must beEmpty
      }

      "return None when show name is omitted" in new Setup {
        val args: Array[String] = Array("-s", "42")

        val output = parseWithCurriedConfigParser(args)
        output must beNone
        errors must contain("Missing option --show-name")
        warnings must beEmpty
      }
    }

    "accept an optional Network" >> {
      "not require an optional Network" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.network must beNone }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow network to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--network", "ESPN")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.network must beSome("ESPN") }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow network to be set with short argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("-N", "ESPN")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.network must beSome("ESPN") }
        errors must beEmpty
        warnings must beEmpty
      }
    }

    "accept an optional HD Video flag" >> {
      "not require the optional flag" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.hdVideo must beNone }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow hdVideo to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--hd-video", "true")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.hdVideo must beSome(true) }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow network to be set with short argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("-H", "false")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.hdVideo must beSome(false) }
        errors must beEmpty
        warnings must beEmpty
      }
    }

    "accept an optional picture flag" >> {
      "not require the optional flag" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.hdVideo must beNone }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow picture to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--picture", "file.png")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.picture must beSome("file.png") }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow picture to be set with short argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("-P", "file.png")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.picture must beSome("file.png") }
        errors must beEmpty
        warnings must beEmpty
      }
    }

    "accept an optional multiline output flag" >> {
      "not require the optional flag" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.multiLine must beTrue }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow single-line output to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--single-line-output")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.multiLine must beFalse }
        errors must beEmpty
        warnings must beEmpty
      }
    }

    "accept an optional filename format" >> {
      "not require the optional flag" in new Setup {
        val args: Array[String] = requiredArguments

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.filenameFormat must_== FilenameFormats.S00E00 }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow iTunes filename format to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--filename-format", "iTunes")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.filenameFormat must_== FilenameFormats.iTunes }
        errors must beEmpty
        warnings must beEmpty
      }

      "allow S00E00 filename format to be set with long argument name" in new Setup {
        val args: Array[String] = requiredArguments ++ Array("--filename-format", "S00E00")

        val output = parseWithCurriedConfigParser(args)

        output must beSome[Config].like { case c ⇒ c.filenameFormat must_== FilenameFormats.S00E00 }
        errors must beEmpty
        warnings must beEmpty
      }
    }
  }

}
