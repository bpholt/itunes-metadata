package com.planetholt.itunes

import scopt.OptionParser

case class Config(show: String, season: Int)

object Config {
  def parse(args: Array[String], parser: OptionParser[Config] = new ConfigParser): Option[Config] = parser.parse(args, Config("", -1))

  class ConfigParser extends OptionParser[Config]("itunes-metadata") {
    head("itunes-metadata", "1.0")

    opt[String]('n', "show-name").required().action( (x, c) ⇒ c.copy(show = x) ).text("Show name")
    opt[Int]('s', "season").required().action( (x, c) ⇒ c.copy(season = x) ).text("Season number")
  }
}
