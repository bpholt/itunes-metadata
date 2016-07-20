package com.planetholt.itunes
import com.planetholt.itunes.model._
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import skinny.http.{HTTP, _}

import scala.concurrent.{ExecutionContext, Future}

class EpisodesRetriever(http: HTTP = HTTP) {
  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def getEpisodes(showId: String,
                  season: Season,
                  hdVideo: Option[Boolean],
                  picture: Option[String])(implicit ec: ExecutionContext): Future[List[Episode]] = {
    val req = Request("https://itunes.apple.com/lookup").queryParams(
      ("id", showId),
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
//        .filter(_.trackNumber == 1)
    }
  }
}
