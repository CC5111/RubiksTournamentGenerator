package controllers

import javax.inject._

import akka.actor.ActorSystem
import akka.actor.FSM.->
import models.entities.Supplier
import models.daos._
import models.persistence.SlickTables.SuppliersTable
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import models.entities._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
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
case class NewEvents(events: Seq[TournamentsEventF])

case class ParticipantEventF(id: Long,
                             checked: Boolean,
                             name: String,
                             eventId: Long)
case class NewParticipantEvents(events: Seq[ParticipantEventF])

@Singleton
class RubiksController @Inject()(tournamentDAO: TournamentDAO, eventDAO: EventDAO, tournamentEventsDAO: TournamentEventsDAO, participantDAO: ParticipantDAO, eventParticipantDAO: EventParticipantDAO)
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
      "name" -> nonEmptyText,
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
    )(NewEvents.apply)(NewEvents.unapply)
  )


  def addTournamentEvent(tournamentId: Long) = Action.async{ implicit request =>

    tournamentDAO.exists(tournamentId).flatMap(s =>
      if (s) {
         eventDAO.allNames.map { case names =>


          val existingEvents = for {
            name <- names
          } yield TournamentsEventF(1, false, name, 0, 0)

          Ok(views.html.tournament_addevents(tournamentId, tournamentEventForm.fill(NewEvents(existingEvents))))
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
          BadRequest(views.html.tournament_addevents(tournamentId, formWithErrors))
        }
      },
      events => {
        val tournament_event = for{
          form <- events.events if (form.checked)
        } yield TournamentEvents(0, form.name, tournamentId, form.limit_time, 1, form.rounds)

        tournament_event.map{ form =>
          tournamentEventsDAO.insert(form)
        }

        Future{
          Ok(views.html.index())
        }
      }
    )

  }

  def seeTournaments() = Action.async{implicit request =>
    tournamentDAO.all.map{ tournaments =>
    Ok(views.html.tournament_see(tournaments.toList))}
  }

  def tournamentIndex(tournamentId: Long) = Action.async{implicit request =>

    tournamentDAO.byId(tournamentId).flatMap{
      case Some(tournament) => Future(Ok(views.html.tournament_index(tournament)))
      case _ => Future(NotFound)

    }

  }

  def tournamentEvents(tournamentId: Long) = Action.async{implicit request =>

    tournamentEventsDAO.all.map{
      case events =>
        val tournamentEvents = for{
          event <- events if (event.tournamentId == tournamentId)
        } yield event

        Ok(views.html.tournament_events(tournamentEvents.toList,tournamentId))

    }

  }

  val participantForm = Form(
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "email" -> nonEmptyText,
      "WCAID" -> nonEmptyText,
      "gender" -> nonEmptyText,
      "tournament_id" -> longNumber,
      "birth_date" -> sqlDate
    )(Participant.apply)(Participant.unapply)
  )

  /* POST */
  def tournamentCreateParticipant(tournamentId: Long) = Action.async{implicit request =>
    participantForm.bindFromRequest.fold(
      formWithErrors => {
        Future{
          BadRequest(views.html.tournament_addparticipant(formWithErrors, tournamentId))
        }
      },
      participant => {
        participantDAO.insert(participant).map{id =>
          Redirect(routes.RubiksController.addParticipantEvent(tournamentId, id))
        }
      }
    )
  }

  /*GET*/
  def tournamentAddParticipant(tournamentId: Long) = Action{implicit request =>
    Ok(views.html.tournament_addparticipant(participantForm, tournamentId))
  }

  val participantEventForm = Form(
    mapping(
      "events" -> seq(
        mapping(
          "id" -> longNumber,
          "checked" -> boolean,
          "name" -> nonEmptyText,
          "event_id" -> longNumber
        )(ParticipantEventF.apply)(ParticipantEventF.unapply)
      )
    )(NewParticipantEvents.apply)(NewParticipantEvents.unapply)
  )

  def addParticipantEvent(tournamentId: Long, participantId: Long) = Action.async{ implicit request =>

    participantDAO.exists(participantId).flatMap(s =>
      if (s) {
        tournamentEventsDAO.all.map { case tevents =>


          val existingEvents = for {
            event <- tevents if (event.tournamentId == tournamentId)
          } yield ParticipantEventF(1, false, event.title, event.id)

          Ok(views.html.tournament_participantevents(participantEventForm.fill(NewParticipantEvents(existingEvents)),tournamentId,participantId))
        }
      }
      else
        Future{ Redirect(routes.RubiksController.index())}
    )
  }


  def createParticipantEvent(tournamentId: Long, participantId: Long) = Action.async{implicit request =>
    participantEventForm.bindFromRequest.fold(
      formWithErrors => {
        Future{
          println(formWithErrors)
          BadRequest(views.html.tournament_participantevents(formWithErrors, tournamentId,participantId))
        }
      },
      events => {
        val participant_event = for{
          form <- events.events if (form.checked)
        } yield EventParticipant(0, form.eventId, participantId)
        println(participant_event)

        participant_event.map{ form =>
          eventParticipantDAO.insert(form)
        }

        Future{
          Redirect(routes.RubiksController.tournamentParticipants(tournamentId))
        }
      }
    )

  }


  def tournamentParticipants(tournamentId: Long) = Action.async{implicit request =>

    participantDAO.all.map{
      case participants =>
        val tournamentParticipants = for{
          participant <- participants if (participant.tournamentId == tournamentId)
        } yield participant

        Ok(views.html.tournament_participants(tournamentParticipants.toList,tournamentId))

    }

  }
}
