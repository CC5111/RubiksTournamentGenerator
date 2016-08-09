package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import models.daos.RoundDao
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

/**
  * Created by hans on 04-08-16.
  */
class RecordsController @Inject()(roundDAO: RoundDao)
                                 (implicit ec: ExecutionContext, system: ActorSystem, mat: akka.stream.Materializer) extends Controller {

  def indexRecords = Action.async { implicit request =>
    for {
      rounds <- roundDAO.all
    } yield
      Ok(views.html.index_records(rounds))
  }

}
