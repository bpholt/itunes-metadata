package com.planetholt.itunes.model

import org.joda.time.DateTime

case class Season(artistId: String,
                  collectionId: String,
                  artistName: String,
                  collectionName: String,
                  copyright: String,
                  releaseDate: DateTime,
                  primaryGenreName: String,
                  seasonNumber: Int,
                  network: Option[String]
                 )

case class SeasonDTO(artistId: String,
                  collectionId: String,
                  artistName: String,
                  collectionName: String,
                  copyright: String,
                  releaseDate: DateTime,
                  primaryGenreName: String
                 ) {
  def toSeason(seasonNumber: Int, network: Option[String] = None) = Season(
    artistId = artistId,
    collectionId = collectionId,
    artistName = artistName,
    collectionName = collectionName,
    copyright = copyright,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    seasonNumber = seasonNumber,
    network = network
  )
}
