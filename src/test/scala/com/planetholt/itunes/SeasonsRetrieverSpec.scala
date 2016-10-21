package com.planetholt.itunes

import java.nio.file.{Files, Paths}

import com.planetholt.itunes.exceptions.SeasonNotFoundException
import com.planetholt.itunes.model.{Season, ServiceException}
import java.time.ZonedDateTime
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import skinny.http.{HTTP, Request, Response}

import scala.concurrent.{ExecutionContext, Future}

class SeasonsRetrieverSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {
  val seasonsBySearchTermResponse = Files.readAllBytes(Paths.get(getClass.getResource("/fake-responses/seasons-by-search-term.json").toURI))

  trait Setup extends Scope {
    val http = mock[HTTP]
    val classToTest = new SeasonsRetriever(http)
  }

  "SeasonsRetriever" should {

    "make an HTTP request to retrieve the given show and filter it by season" in new Setup {
      val showName = "show"

      http.asyncGet(Request("https://itunes.apple.com/search").queryParams(
        ("media", "tvShow"),
        ("entity", "tvSeason"),
        ("term", showName),
        ("attribute", "showTerm")
      )) returns Future.successful(Response(200, body = seasonsBySearchTermResponse))

      val output = classToTest.getSeasonOfShow(showName, 2, Option("CBS"))

      val expected = Season(
        artistId = "328355264",
        collectionId = "390453780",
        artistName = "The Good Wife",
        collectionName = "The Good Wife, Season 2",
        copyright = "© CBS Corp 2010",
        releaseDate = ZonedDateTime.parse("2010-09-28T07:00:00Z"),
        primaryGenreName = "Drama",
        seasonNumber = 2,
        network = Option("CBS")
      )

      output must be_==(expected).await
    }

    "throw an exception if the show is found but the season is not" in new Setup {
      val showName = "show"

      http.asyncGet(Request("https://itunes.apple.com/search").queryParams(
        ("media", "tvShow"),
        ("entity", "tvSeason"),
        ("term", showName),
        ("attribute", "showTerm")
      )) returns Future.successful(Response(200, body = seasonsBySearchTermResponse))

      val output = classToTest.getSeasonOfShow(showName, 15, Option("CBS"))

      output must throwA[SeasonNotFoundException].like {
        case ex: SeasonNotFoundException ⇒
          ex.season must_== 15
          ex.show must_== showName
          ex.getMessage must_== s"Found $showName, but not season 15"
      }.await
    }

    "error out if the response code is not 2xx" in new Setup {
      http.asyncGet(any[Request])(any[ExecutionContext]) returns Future.successful(Response(500))

      classToTest.getSeasonOfShow("show", 5, None) must throwA[ServiceException].like { case ex ⇒ ex.getMessage must_== s"Received unexpected status 500 : "}.await
    }
  }

}



