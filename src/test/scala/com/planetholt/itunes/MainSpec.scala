package com.planetholt.itunes

import com.planetholt.itunes.model.mp4.Options
import com.planetholt.itunes.model.{Episode, Season}
import org.joda.time.DateTime
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.concurrent.Future

class MainSpec(implicit ee: ExecutionEnv) extends Specification with Mockito {

  trait Setup extends Scope {
    val hdVideo = Option(true)
    val picture = Option("file.png")
    val network = Option("network")
    val config = Config("show", 2, network, hdVideo, picture)
    val seasonsRetriever = mock[SeasonsRetriever]
    val episodesRetriever = mock[EpisodesRetriever]

    private val expectedSeason = Season(
      artistId = "328355264",
      collectionId = "390453780",
      artistName = "The Good Wife",
      collectionName = "The Good Wife, Season 2",
      copyright = "© CBS Corp 2010",
      releaseDate = DateTime.parse("2010-09-28T07:00:00Z"),
      primaryGenreName = "Drama",
      seasonNumber = 2,
      network = Option("CBS")
    )
    val expectedEpisode = Episode(
      artistId = 328355264,
      collectionId = 909080561,
      trackId = 923833431,
      artistName = "The Good Wife",
      collectionName = "The Good Wife, Season 6",
      releaseDate = DateTime.parse("2014-09-28T07:00:00Z"),
      genre = "Drama",
      contentAdvisoryRating = "TV-14",
      shortDescription = "Alicia scrambles to hold onto Florrick/Agos’ biggest client as the firm continues to struggle with",
      longDescription = "Alicia scrambles to hold onto Florrick/Agos’ biggest client as the firm continues to struggle with an internal legal crisis. Meanwhile, Diane continues to plot her move from Lockhart/Gardner and looks for partners to bring with her to the new firm.  Taye Diggs guest stars.",
      explicit = None,
      discCount = 1,
      discNumber = 1,
      trackCount = 23,
      trackNumber = 2,
      season = expectedSeason,
      hdVideo = hdVideo,
      picture = picture
    )

    seasonsRetriever.getSeasonOfShow(config.show, config.season, network) returns Future.successful(expectedSeason)

    episodesRetriever.getEpisodes(expectedSeason, hdVideo, picture) returns Future.successful(List(expectedEpisode))
    val classToTest = new Main(config, seasonsRetriever, episodesRetriever)

  }

  "Main" should {

    "retrieve the list of seasons" in new Setup {

      val output: Future[List[Episode]] = classToTest.lookupEpisodes

      output must contain(expectedEpisode).await
    }

    "map the results to Options" in new Setup {
      val output: Future[List[Options]] = classToTest.lookupEpisodesAsOptions

      output must contain(Options(expectedEpisode)).await
    }

  }
}
