package com.planetholt.itunes.model.mp4

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}

import com.planetholt.itunes.Config.FilenameFormats
import com.planetholt.itunes.Config.FilenameFormats.FilenameFormats

case class Options(input: String, options: List[Mp4Option[_]]) {
  override def toString: String = {
    val combinedOptions = options.mkString(" \\\n    ")
    s"""mp4tags \\
       |    $combinedOptions \\
       |    "$input"""".stripMargin
  }
}

object Options {
  val removeEncodedByAndToolMetadata = Remove("eE")

  def apply(episode: com.planetholt.itunes.model.Episode, filenameFormat: FilenameFormats = FilenameFormats.S00E00): Options = {
    val filename = filenameFormat match {
      case FilenameFormats.S00E00 ⇒ f"S${episode.season.seasonNumber}%02dE${episode.trackNumber}%02d.m4v"
      case FilenameFormats.iTunes ⇒ f"${episode.trackNumber}%02d ${episode.trackName} (HD).m4v".replaceAll("['’?]", "_")
    }

    val requiredOptions: List[Mp4Option[_]] = List(
      Type("tvshow"),
      ArtistId(episode.artistId),
      PlaylistId(episode.collectionId),
      ContentId(episode.trackId),
      Song(episode.trackName),
      Artist(episode.artistName),
      Album(episode.collectionName),
      Show(episode.artistName),
      Year(episode.releaseDate),
      Genre(episode.genre),
      Description(episode.shortDescription),
      LongDesc(episode.longDescription),
      Disks(episode.discCount),
      Disk(episode.discNumber),
      Tracks(episode.trackCount),
      Track(episode.trackNumber),
      Season(episode.season.seasonNumber),
      Episode(episode.trackNumber),
      episode.explicit match {
        case Some(true) ⇒ Explicit
        case Some(false) ⇒ Clean
        case _ ⇒ Remove("X")
      },
      removeEncodedByAndToolMetadata,
      Copyright(episode.season.copyright)
    )
    val optionalOptions: List[Mp4Option[_]] = List(
      episode.season.network.map(Network),
      episode.hdVideo.map(HdVideo),
      episode.picture.map(Picture)
    ).flatMap(_.toList)

    Options(
      filename,
      requiredOptions ++ optionalOptions
    )
  }
}

sealed trait Mp4Option[T] {
  def name: String
  def value: T

  override def toString = s"$name $value"
}

sealed trait StringMp4Option extends Mp4Option[String] {
  override def toString = s"""$name "$value""""
}

case class Album(value: String) extends StringMp4Option {
  override def name: String = "-album"
}

case class Artist(value: String) extends StringMp4Option {
  override def name: String = "-artist"
}

case class Tempo(value: Int) extends Mp4Option[Int] {
  override def name: String = "-tempo"
}

case class Comment(value: String) extends StringMp4Option {
  override def name: String = "-comment"
}

case class Copyright(value: String) extends StringMp4Option {
  override def name: String = "-copyright"
}

case class Disk(value: Int) extends Mp4Option[Int] {
  override def name: String = "-disk"
}

case class Disks(value: Int) extends Mp4Option[Int] {
  override def name: String = "-disks"
}

case class EncodedBy(value: String) extends StringMp4Option {
  override def name: String = "-encodedby"
}

case class Tool(value: String) extends StringMp4Option {
  override def name: String = "-tool"
}

case class Genre(value: String) extends StringMp4Option {
  override def name: String = "-genre"
}

case class Grouping(value: String) extends StringMp4Option {
  override def name: String = "-grouping"
}

case class HdVideo(value: Boolean) extends Mp4Option[Boolean] {
  override def name: String = "-hdvideo"

  override def toString: String = s"$name ${if (value) 1 else 0}"
}

case class Type(value: String) extends StringMp4Option {
  override def name: String = "-type"
}

case class ContentId(value: Int) extends Mp4Option[Int] {
  override def name: String = "-contentid"
}

case class GenreId(value: Int) extends Mp4Option[Int] {
  override def name: String = "-genreid"
}

case class LongDesc(value: String) extends StringMp4Option {
  override def name: String = "-longdesc"
}

case class Lyrics(value: Int) extends Mp4Option[Int] {
  override def name: String = "-lyrics"
}

case class Description(value: String) extends StringMp4Option {
  override def name: String = "-description"
}

case class Episode(value: Int) extends Mp4Option[Int] {
  override def name: String = "-episode"
}

case class Season(value: Int) extends Mp4Option[Int] {
  override def name: String = "-season"
}

case class Network(value: String) extends StringMp4Option {
  override def name: String = "-network"
}

case class EpisodeId(value: String) extends StringMp4Option {
  override def name: String = "-episodeid"
}

case class Category(value: String) extends StringMp4Option {
  override def name: String = "-category"
}

case class PlaylistId(value: Int) extends Mp4Option[Int] {
  override def name: String = "-playlistid"
}

case class Picture(value: String) extends StringMp4Option {
  override def name: String = "-picture"
}

case class Podcast(value: Int) extends Mp4Option[Int] {
  override def name: String = "-podcast"
}

case class AlbumArtist(value: String) extends StringMp4Option {
  override def name: String = "-albumartist"
}

case class Song(value: String) extends StringMp4Option {
  override def name: String = "-song"
}

case class Show(value: String) extends StringMp4Option {
  override def name: String = "-show"
}

case class Track(value: Int) extends Mp4Option[Int] {
  override def name: String = "-track"
}

case class Tracks(value: Int) extends Mp4Option[Int] {
  override def name: String = "-tracks"
}

case class Xid(value: String) extends StringMp4Option {
  override def name: String = "-xid"
}

case class Writer(value: String) extends StringMp4Option {
  override def name: String = "-writer"
}

case class Year(value: Instant) extends Mp4Option[Instant] {
  override def name: String = "-year"
  override def toString: String = s"$name ${Year.dateFormat.format(value)}"
}

object Year {
  val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC)
}

case class ArtistId(value: Int) extends Mp4Option[Int] {
  override def name: String = "-artistid"
}

case class ComposerId(value: Int) extends Mp4Option[Int] {
  override def name: String = "-composerid"
}

case class Remove(value: String) extends StringMp4Option {
  override def name: String = "-remove"
}

sealed trait Rating extends StringMp4Option {
  override def name: String = "-rating"
}

case object Explicit extends Rating {
  override def value: String = "explicit"
}

case object Clean extends Rating {
  override def value: String = "clean"
}
