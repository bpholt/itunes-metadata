package com.planetholt.itunes



import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {
  val config = Config.parse(args)

  config match {
    case None ⇒ sys.exit(1)
    case Some(c) ⇒ new Main(c)
  }
}

class Main(config: Config) {
  private val eventualSeason = new SeasonsRetriever().getSeasonOfShow(config.show, config.season)

  private val eventualEpisodes = eventualSeason.flatMap { season ⇒
    new EpisodesRetriever().getEpisodes(season.collectionId)
  }

  private val result = Await.result(eventualEpisodes, 10 seconds)

  import org.json4s._
  import org.json4s.native.JsonMethods._
  import org.json4s.native.Serialization
  import org.json4s.native.Serialization.write
  import org.json4s.ext.JodaTimeSerializers

  implicit val formats = Serialization.formats(NoTypeHints) ++ JodaTimeSerializers.all

  result.map(x ⇒ pretty(render(parse(write(x))))).foreach(println(_))
}
