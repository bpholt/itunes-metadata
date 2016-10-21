package com.planetholt.itunes

import java.nio.file.{Files, Paths}

import com.planetholt.itunes.model.{Episode, Season}
import java.time.Instant
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import skinny.http.{HTTP, Request, Response}

import scala.concurrent.{ExecutionContext, Future}

class EpisodeRetrieverSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  val episodesByCollectionIdResponse = Files.readAllBytes(Paths.get(getClass.getResource("/fake-responses/episodes-by-collection-id.json").toURI))
  val responseIncludingQuotes = Files.readAllBytes(Paths.get(getClass.getResource("/fake-responses/episode-with-quotes.json").toURI))

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

      private val season = Season("artistId", "collectionId", "artistName", "collectionName", "copyright", Instant.now, "genre", 4, None)
      private val hdVideo = Option(true)
      private val artwork = Option("file.png")
      val output = classToTest.getEpisodes(season, hdVideo, artwork)

      output must haveLength[List[Episode]](22).await

      private val expected = Episode(
        artistId = 328355264,
        collectionId = 909080561,
        trackId = 923833431,
        trackName = "Trust Issues",
        artistName = "The Good Wife",
        collectionName = "The Good Wife, Season 6",
        releaseDate = Instant.parse("2014-09-28T07:00:00Z"),
        genre = "Drama",
        contentAdvisoryRating = "TV-14",
        shortDescription = "Alicia scrambles to hold onto Florrick/Agos’ biggest client as the firm continues to struggle with",
        longDescription = "Alicia scrambles to hold onto Florrick/Agos’ biggest client as the firm continues to struggle with an internal legal crisis. Meanwhile, Diane continues to plot her move from Lockhart/Gardner and looks for partners to bring with her to the new firm.  Taye Diggs guest stars.",
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

    "change 's in response strings to ’" in new Setup {
      http.asyncGet(any[Request])(any[ExecutionContext]) returns Future.successful(Response(200, body = responseIncludingQuotes))

      private val showId = "episode-id"
      private val season = Season("artistId", "collectionId", "artistName", "collectionName", "copyright", Instant.now, "genre", 4, None)
      private val hdVideo = Option(true)
      private val artwork = Option("file.png")
      val output = classToTest.getEpisodes(season, hdVideo, artwork)

      output must contain(beLike[Episode] {
        case e: Episode ⇒
          e.artistName must_== "test’s"
      }).await
    }

    """change ""s in response strings to “”""" in new Setup {
      http.asyncGet(any[Request])(any[ExecutionContext]) returns Future.successful(Response(200, body = responseIncludingQuotes))

      private val showId = "episode-id"
      private val season = Season("artistId", "collectionId", "artistName", "collectionName", "copyright", Instant.now, "genre", 4, None)
      private val hdVideo = Option(true)
      private val artwork = Option("file.png")
      val output = classToTest.getEpisodes(season, hdVideo, artwork)

      output must contain(beLike[Episode] {
        case e: Episode ⇒
          e.shortDescription must_== "’“”“"
          e.longDescription must_== "’“”“”"
      }).await
    }
  }

}
