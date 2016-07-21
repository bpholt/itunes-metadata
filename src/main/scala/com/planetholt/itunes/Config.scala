package com.planetholt.itunes

import com.planetholt.itunes.Config.FilenameFormats
import com.planetholt.itunes.Config.FilenameFormats.FilenameFormats
import scopt.OptionParser

import scala.language.postfixOps

case class Config(show: String,
                  season: Int,
                  network: Option[String] = None,
                  hdVideo: Option[Boolean] = None,
                  picture: Option[String] = None,
                  multiLine: Boolean = true,
                  filenameFormat: FilenameFormats = FilenameFormats.S00E00
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
    opt[Unit]("single-line-output").action( (_, c) ⇒ c.copy(multiLine = false)).text("Output each episode’s command line on a single line (defaults to multi-line output)")
    opt[FilenameFormats]("filename-format").action( (x, c) ⇒ c.copy(filenameFormat = x)).text("Select the expected filename format. «iTunes» matches the format in a managed iTunes library. «S00E00» is the default.")
  }

  object FilenameFormats extends Enumeration {
    type FilenameFormats = Value
    val S00E00, iTunes = Value
  }

  implicit val filenameFormatsRead: scopt.Read[FilenameFormats.Value] =
    scopt.Read.reads(FilenameFormats withName)
}
