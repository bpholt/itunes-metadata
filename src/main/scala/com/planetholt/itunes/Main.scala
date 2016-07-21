package com.planetholt.itunes

import com.planetholt.itunes.model.Episode
import com.planetholt.itunes.model.mp4.Options

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  val config = Config.parse(args)

  config match {
    case None ⇒ sys.exit(1)
    case Some(c) ⇒
      Await.result(new Main(c).lookupEpisodesAsOptions.map(_.foreach(println(_))), 30 seconds)
  }
}

class Main(config: Config,
           seasonsRetriever: SeasonsRetriever = new SeasonsRetriever(),
           episodesRetriever: EpisodesRetriever = new EpisodesRetriever())(implicit ec: ExecutionContext) {
  def lookupEpisodesAsOptions: Future[List[Options]] = lookupEpisodes.map(_.map(Options(_, config.filenameFormat)))

  def lookupEpisodes: Future[List[Episode]] = {
    val eventualSeason = seasonsRetriever.getSeasonOfShow(config.show, config.season, config.network)

    eventualSeason.flatMap { season ⇒
      episodesRetriever.getEpisodes(season, config.hdVideo, config.picture)
    }
  }
}
