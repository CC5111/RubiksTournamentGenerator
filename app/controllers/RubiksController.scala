package controllers

import javax.inject._
import akka.actor.ActorSystem
import models.daos._
import models.entities.Supplier
import models.persistence.SlickTables.SuppliersTable
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}

import models.entities._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}
import play.api.data._
import play.api.data.Forms._


import play.api.i18n.Messages.Implicits._
import play.api.Play.current

/**
  * Created by milenkotomic on 29-07-16.
  */

@Singleton
class RubiksController @Inject()(tournamentDAO: TournamentDAO)
                                (implicit ec: ExecutionContext, system: ActorSystem, mat: akka.stream.Materializer) extends Controller {

  def index = Action{implicit request =>
    Ok(views.html.index())
  }

  def indexTournament = Action{implicit request =>
    Ok(views.html.tournament())
  }

  val tournamentForm = Form(
    mapping(
      "id" -> longNumber,
      "place" -> nonEmptyText,
      "organizer_id" -> longNumber,
      "delegated_id" -> longNumber,
      "fecha inicial" -> sqlDate,
      "fecha final" -> sqlDate
    )(Tournament.apply)(Tournament.unapply)
  )

  def createTournament = Action{implicit request =>
    Ok(views.html.tournament_creation(tournamentForm))
  }

  def addTournament = Action { implicit request =>
    Ok(views.html.tournament_creation(tournamentForm))
  }
  /*def viewTournament = Action.async { implicit request =>
    for {events <- tournamentDAO.all} yield Ok(views.html.indexRecords(events))
  }*/
}
