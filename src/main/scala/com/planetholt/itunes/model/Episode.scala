package com.planetholt.itunes.model

import org.joda.time.DateTime

case class Episode(artistId: Int,
                   collectionId: Int,
                   trackId: Int,
                   trackName: String,
                   artistName: String,
                   collectionName: String,
                   releaseDate: DateTime,
                   genre: String,
                   contentAdvisoryRating: String,
                   shortDescription: String,
                   longDescription: String,
                   explicit: Option[Boolean],
                   discCount: Int,
                   discNumber: Int,
                   trackCount: Int,
                   trackNumber: Int,
                   season: Season,
                   hdVideo: Option[Boolean],
                   picture: Option[String]
                  )


case class CollectionItemDTO(wrapperType: String,
                             collectionType: Option[String],
                             artistId: Int,
                             collectionId: Int,
                             trackId: Option[Int],
                             trackName: Option[String],
                             artistName: String,
                             collectionName: String,
                             releaseDate: DateTime,
                             primaryGenreName: String,
                             contentAdvisoryRating: String,
                             shortDescription: Option[String],
                             longDescription: String,
                             trackExplicitness: Option[String],
                             discCount: Option[Int],
                             discNumber: Option[Int],
                             trackCount: Int,
                             trackNumber: Option[Int]
                            ) {
  def toEpisodeOfSeason(season: Season, hdVideo: Option[Boolean], picture: Option[String]): Episode = Episode(
    artistId = artistId,
    collectionId = collectionId,
    trackId = trackId.get,
    trackName = trackName.get,
    artistName = artistName,
    collectionName = collectionName,
    releaseDate = releaseDate,
    genre = primaryGenreName,
    contentAdvisoryRating = contentAdvisoryRating,
    shortDescription = shortDescription.get,
    longDescription = longDescription,
    explicit = trackExplicitness match {
      case Some(x) if x.equalsIgnoreCase("explicit") ⇒ Some(true)
      case Some(x) if x.equalsIgnoreCase("cleaned") ⇒ Some(false)
      case _ ⇒ None
    },
    discCount = discCount.get,
    discNumber = discNumber.get,
    trackCount = trackCount,
    trackNumber = trackNumber.get,
    season = season,
    hdVideo = hdVideo,
    picture = picture
  )

  def isEpisode: Boolean = "track" == wrapperType
  def isSupplementalContent: Boolean = trackNumber.exists(_ > 100)
}
