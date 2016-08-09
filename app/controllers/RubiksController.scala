package controllers

import javax.inject._
import akka.actor.ActorSystem
import akka.actor.FSM.->
import models.entities.Supplier
import models.daos.TournamentDAO
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


case class tournamentsEventF(id: Long,
                             checked: Boolean,
                             name: String,
                             limit_time: Int,
                             rounds: Int)

@Singleton
class RubiksController @Inject()(tournamentDAO: TournamentDAO)
                                (implicit ec: ExecutionContext, system: ActorSystem, mat: akka.stream.Materializer) extends Controller {

  def index = Action{implicit request =>
    Ok(views.html.index())
  }

  def indexTournament = Action{implicit request =>
    Ok(views.html.tournament())
  }

  def addTournament = Action { implicit request =>
    Ok(views.html.tournament_creation(tournamentForm))
  }

  val tournamentForm = Form(
    mapping(
      "id" -> longNumber,
      "place" -> nonEmptyText,
      "city" -> nonEmptyText,
      "address" -> nonEmptyText,
      "details" -> nonEmptyText,
      "website" -> nonEmptyText,
      "organizer_id" -> nonEmptyText,
      "delegated_id" -> nonEmptyText,
      "start_date" -> sqlDate,
      "end_date" -> sqlDate
    )(Tournament.apply)(Tournament.unapply)
  )

  def createTournament = Action.async{ implicit request =>
    tournamentForm.bindFromRequest.fold(
      formWithErrors => {
        Future{
          BadRequest(views.html.tournament_creation(formWithErrors))
        }
      },
      tournament => {
        tournamentDAO.insert(tournament).map{id =>
          Redirect(routes.RubiksController.addTournamentEvent(id))
        }
      }
    )
  }

  /*def viewTournament = Action.async { implicit request =>
    for {events <- tournamentDAO.all} yield Ok(views.html.indexRecords(events))
  }*/




  val tournamentEventForm = Form(
    single(
      "events" -> seq(
        mapping(
          "id" -> longNumber,
          "checked" -> boolean,
          "name" -> nonEmptyText,
          "limit_time" -> number,
          "rounds" -> number

        )(tournamentsEventF.apply)(tournamentsEventF.unapply)
      )
    )
  )


  def addTournamentEvent(tournamentId: Long) = Action.async{ implicit request =>
    tournamentDAO.exists(tournamentId).map(s =>
      if (s) {
          val existingEvents = List(tournamentsEventF(1, false, "3x3", 0, 0),
                                    tournamentsEventF(2, false, "2x2", 0, 0))

          Ok(views.html.tournament_events(tournamentId, tournamentEventForm.fill(existingEvents)))
      }
      else
        Redirect(routes.RubiksController.index())
    )
  }


  def createTournamentEvent(tournamentId: Long) = Action.async{implicit request =>
    tournamentEventForm.bindFromRequest.fold(
      formWithErrors => {
        Future{
          println(formWithErrors)
          BadRequest(views.html.tournament_events(tournamentId, formWithErrors))
        }
      },
      events => {
        Future{
          Ok(views.html.index())
        }
      }
    )

  }
}
