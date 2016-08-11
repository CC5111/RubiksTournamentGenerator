package controllers

import javax.inject._
import akka.actor.ActorSystem
import akka.actor.FSM.->
import models.entities.Supplier
import models.daos.{EventDAO, TournamentDAO}
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


case class TournamentsEventF(id: Long,
                             checked: Boolean,
                             name: String,
                             limit_time: Int,
                             rounds: Int)
case class Tournaments(events: Seq[TournamentsEventF])

@Singleton
class RubiksController @Inject()(tournamentDAO: TournamentDAO, eventDAO: EventDAO)
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
    mapping(
      "events" -> seq(
        mapping(
          "id" -> longNumber,
          "checked" -> boolean,
          "name" -> nonEmptyText,
          "limit_time" -> number,
          "rounds" -> number

        )(TournamentsEventF.apply)(TournamentsEventF.unapply)
      )
    )(Tournaments.apply)(Tournaments.unapply)
  )


  def addTournamentEvent(tournamentId: Long) = Action.async{ implicit request =>

    tournamentDAO.exists(tournamentId).flatMap(s =>
      if (s) {
         eventDAO.allNames.map { case names =>


          val existingEvents = for {
            name <- names
          } yield TournamentsEventF(1, false, name, 0, 0)

          Ok(views.html.tournament_events(tournamentId, tournamentEventForm.fill(Tournaments(existingEvents))))
         //Redirect(routes.RubiksController.index())
        }
      }
      else
        Future{ Redirect(routes.RubiksController.index())}
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
