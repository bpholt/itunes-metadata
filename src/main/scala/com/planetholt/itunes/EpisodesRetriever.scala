package com.planetholt.itunes

import com.planetholt.itunes.model._
import org.json4s._
import org.json4s.native.JsonMethods._
import skinny.http.{HTTP, _}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class EpisodesRetriever(http: HTTP = HTTP) {
  implicit val formats = DefaultFormats + ZonedDateTimeSerializer

  def getEpisodes(season: Season,
                  hdVideo: Option[Boolean],
                  picture: Option[String])(implicit ec: ExecutionContext): Future[List[Episode]] = {
    val req = Request("https://itunes.apple.com/lookup").queryParams(
      ("id", season.collectionId),
      ("entity", "tvEpisode")
    )
    val eventualResponse = http.asyncGet(req)

    eventualResponse.map { (res: Response) ⇒
      if (!(200 to 299).contains(res.status)) {
        throw ServiceException(s"Received unexpected status ${res.status} : ${res.asString}")
      }
      (parse(res.asString) \ "results")
        .extract[List[CollectionItemDTO]]
        .filter(dto ⇒ dto.isEpisode && !dto.isSupplementalContent)
        .map(_.toEpisodeOfSeason(season, hdVideo, picture))
        .map(EpisodesRetriever.smartQuotes)
    }
  }

}

object EpisodesRetriever {
  import sh.echo.Mappable
  import Mappable._
  val mappable = materializeMappable[Episode]
  val quotesRegex = new Regex(""""([^"]*)("?)""", "content", "optionalTrailingQuote")

  def smartQuotes(episode: Episode): Episode = mappable.fromMap(mappable.toMap(episode).map {
    case (key: String, value: String) ⇒ (key, quotesRegex.replaceAllIn(value.replace("'", "’"), replaceQuotesInMatch))
    case other ⇒ other
  })

  val replaceQuotesInMatch: (Match) ⇒ String = m ⇒ {
    val trailer = if (m.group("optionalTrailingQuote").equals("\"")) "”" else ""
    s"“${m.group("content")}$trailer"
  }
}
