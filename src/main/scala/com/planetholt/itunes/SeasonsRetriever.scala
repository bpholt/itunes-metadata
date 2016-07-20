package com.planetholt.itunes

import com.planetholt.itunes.exceptions.SeasonNotFoundException
import com.planetholt.itunes.model.{Season, SeasonDTO, ServiceException}
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import skinny.http._

import scala.concurrent.{ExecutionContext, Future}

class SeasonsRetriever(http: HTTP = HTTP) {
  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def getSeasonOfShow(showName: String, season: Int, network: Option[String] = None)(implicit ec: ExecutionContext): Future[Season] = {
    val req = Request("https://itunes.apple.com/search").queryParams(
      ("media", "tvShow"),
      ("entity", "tvSeason"),
      ("term", showName),
      ("attribute", "showTerm")
    )
    val eventualResponse = http.asyncGet(req)

    eventualResponse.map { (res: Response) ⇒
      if (!(200 to 299).contains(res.status)) {
        throw ServiceException(s"Received unexpected status ${res.status} : ${res.asString}")
      }
      (parse(res.asString) \ "results")
        .extract[List[SeasonDTO]]
        .find(_.collectionName.endsWith(s" Season $season"))
        .map(_.toSeason(season, network))
        .getOrElse(throw SeasonNotFoundException(showName, season))
    }
  }
}
