package com.planetholt.itunes

import scopt.OptionParser

case class Config(show: String,
                  season: Int,
                  network: Option[String] = None,
                  hdVideo: Option[Boolean] = None,
                  picture: Option[String] = None
                 )

object Config {
  def parse(args: Array[String], parser: OptionParser[Config] = new ConfigParser): Option[Config] = parser.parse(args, Config("", -1))

  class ConfigParser extends OptionParser[Config]("itunes-metadata") {
    head("itunes-metadata", "1.0")

    opt[String]('n', "show-name").required().action( (x, c) ⇒ c.copy(show = x) ).text("Show name")
    opt[Int]('s', "season").required().action( (x, c) ⇒ c.copy(season = x) ).text("Season number")
    opt[String]('N', "network").action( (x, c) ⇒ c.copy(network = Option(x))).text("Broadcast network")
    opt[Boolean]('H', "hd-video").action( (x, c) ⇒ c.copy(hdVideo = Option(x))).text("Flag indicating whether the video files contain HD video (with false indicating SD video). If unset, the quality option will not be set.")
    opt[String]('P', "picture").action( (x, c) ⇒ c.copy(picture = Option(x))).text("Path to artwork in PNG format")
  }
}
