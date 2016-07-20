package com.planetholt.itunes

import java.nio.file.{Files, Paths}

import com.planetholt.itunes.model.{Episode, Season}
import org.joda.time.DateTime
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import skinny.http.{HTTP, Request, Response}

import scala.concurrent.Future

class EpisodeRetrieverSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  val episodesByCollectionIdResponse = Files.readAllBytes(Paths.get(getClass.getResource("/fake-responses/episodes-by-collection-id.json").toURI))

  trait Setup extends Scope {
    val http = mock[HTTP]
    val classToTest = new EpisodesRetriever(http)
  }

  "EpisodeRetriever" should {

    "fetch the episodes for the given id" in new Setup {
      private val showId = "episode-id"

      http.asyncGet(Request("https://itunes.apple.com/lookup").queryParams(
        ("id", showId),
        ("entity", "tvEpisode")
      ))(ee.ec) returns Future.successful(Response(200, body = episodesByCollectionIdResponse))

      private val season = Season("artistId", "collectionId", "artistName", "collectionName", "copyright", DateTime.now, "genre", 4, None)
      private val hdVideo = Option(true)
      private val artwork = Option("file.png")
      val output = classToTest.getEpisodes(showId, season, hdVideo, artwork)

      output must haveLength[List[Episode]](22).await

      private val expected = Episode(
        artistId = 328355264,
        collectionId = 909080561,
        trackId = 923833431,
        artistName = "The Good Wife",
        collectionName = "The Good Wife, Season 6",
        releaseDate = DateTime.parse("2014-09-28T07:00:00Z"),
        genre = "Drama",
        contentAdvisoryRating = "TV-14",
        shortDescription = "Alicia scrambles to hold onto Florrick/Agos' biggest client as the firm continues to struggle with",
        longDescription = "Alicia scrambles to hold onto Florrick/Agos' biggest client as the firm continues to struggle with an internal legal crisis. Meanwhile, Diane continues to plot her move from Lockhart/Gardner and looks for partners to bring with her to the new firm.  Taye Diggs guest stars.",
        explicit = None,
        discCount = 1,
        discNumber = 1,
        trackCount = 23,
        trackNumber = 2,
        season = season,
        hdVideo = hdVideo,
        picture = artwork
      )

      output must contain(expected).await
    }

  }

}

/*
    {
      "wrapperType": "track",
      "kind": "tv-episode",
      "artistId": 328355264,
      "collectionId": 909080561,
      "trackId": 923833431,
      "artistName": "The Good Wife",
      "collectionName": "The Good Wife, Season 6",
      "trackName": "Trust Issues",
      "collectionCensoredName": "The Good Wife, Season 6",
      "trackCensoredName": "Trust Issues",
      "artistViewUrl": "https://itunes.apple.com/us/tv-show/the-good-wife/id328355264?uo=4",
      "collectionViewUrl": "https://itunes.apple.com/us/tv-season/trust-issues/id909080561?i=923833431&uo=4",
      "trackViewUrl": "https://itunes.apple.com/us/tv-season/trust-issues/id909080561?i=923833431&uo=4",
      "previewUrl": "http://a1072.phobos.apple.com/us/r1000/050/Video5/v4/c7/05/cf/c705cf88-3492-b0ec-1da3-823745339404/mzvf_6878528952001767860.640x480.h264lc.D2.p.m4v",
      "artworkUrl30": "http://is5.mzstatic.com/image/thumb/Music6/v4/a0/f1/90/a0f19024-561b-4367-f0e9-72a76acbd9f3/source/30x30bb.jpg",
      "artworkUrl60": "http://is5.mzstatic.com/image/thumb/Music6/v4/a0/f1/90/a0f19024-561b-4367-f0e9-72a76acbd9f3/source/60x60bb.jpg",
      "artworkUrl100": "http://is5.mzstatic.com/image/thumb/Music6/v4/a0/f1/90/a0f19024-561b-4367-f0e9-72a76acbd9f3/source/100x100bb.jpg",
      "collectionPrice": 38.99,
      "trackPrice": 1.99,
      "collectionHdPrice": 44.99,
      "trackHdPrice": 2.99,
      "releaseDate": "2014-09-28T07:00:00Z",
      "collectionExplicitness": "notExplicit",
      "trackExplicitness": "notExplicit",
      "discCount": 1,
      "discNumber": 1,
      "trackCount": 23,
      "trackNumber": 2,
      "trackTimeMillis": 2618912,
      "country": "USA",
      "currency": "USD",
      "primaryGenreName": "Drama",
      "contentAdvisoryRating": "TV-14",
      "shortDescription": "Alicia scrambles to hold onto Florrick/Agos' biggest client as the firm continues to struggle with",
      "longDescription": "Alicia scrambles to hold onto Florrick/Agos' biggest client as the firm continues to struggle with an internal legal crisis. Meanwhile, Diane continues to plot her move from Lockhart/Gardner and looks for partners to bring with her to the new firm.  Taye Diggs guest stars."
    }
 */