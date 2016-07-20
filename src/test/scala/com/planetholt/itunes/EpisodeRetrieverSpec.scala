package com.planetholt.itunes

import java.nio.file.{Files, Paths}

import com.planetholt.itunes.model.Episode
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

      val output = classToTest.getEpisodes(showId)

      output must haveLength[List[Episode]](22).await
    }

  }

}
