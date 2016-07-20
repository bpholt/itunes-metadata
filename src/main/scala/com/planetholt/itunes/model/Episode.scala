package com.planetholt.itunes.model

import org.joda.time.DateTime

case class Episode(artistId: String,
                   collectionId: String,
                   trackId: String,
                   artistName: String,
                   collectionName: String,
                   releaseDate: DateTime,
                   genre: String,
                   contentAdvisoryRating: String,
                   shortDescription: String,
                   longDescription: String,
                   explicit: Boolean,
                   discCount: Int,
                   discNumber: Int,
                   trackCount: Int,
                   trackNumber: Int
                  )


case class CollectionItemDTO(wrapperType: String,
                             collectionType: Option[String],
                             artistId: String,
                             collectionId: String,
                             trackId: Option[String],
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
  def toEpisode: Episode = Episode(
    artistId,
    collectionId,
    trackId.get,
    artistName,
    collectionName,
    releaseDate,
    primaryGenreName,
    contentAdvisoryRating,
    shortDescription.get,
    longDescription,
    !"notExplicit".eq(trackExplicitness.getOrElse("notExplicit")),
    discCount.get,
    discNumber.get,
    trackCount,
    trackNumber.get
  )

  def isEpisode: Boolean = "track" == wrapperType
  def isSupplementalContent: Boolean = trackNumber.exists(_ > 100)
}
