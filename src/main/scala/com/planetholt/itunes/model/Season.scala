package com.planetholt.itunes.model

import org.joda.time.DateTime

//media=tvShow&entity=tvSeason&term=the%2Bgood%2Bwife&attribute=showTerm
case class Season(artistId: String,
                  collectionId: String,
                  artistName: String,
                  collectionName: String,
                  copyright: String,
                  releaseDate: DateTime,
                  primaryGenreName: String
                 )

case class SeasonsResponse(resultCount: Int, results: List[Season])
