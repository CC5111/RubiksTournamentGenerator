package models.persistence

import models.entities.Supplier
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import models.entities._

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

  case class SimpleSupplier(name: String, desc: String)

  class SuppliersTable(tag: Tag) extends BaseTable[Supplier](tag, "suppliers") {
    def name = column[String]("name")
    def desc = column[String]("desc")
    def * = (id, name, desc) <> (Supplier.tupled, Supplier.unapply)
  }

  val suppliersTableQ : TableQuery[SuppliersTable] = TableQuery[SuppliersTable]

  class TournamentTable(tag: Tag) extends BaseTable[Tournament](tag,"tournament"){
    def place = column[String]("place")
    def city = column[String]("city")
    def address = column[String]("address")
    def details = column[String]("details")
    def website = column[String]("website")
    def organizer = column[String]("organizer")
    def delegated = column[String]("delegated")
    def start_date = column[java.sql.Date]("start_date")
    def end_date = column[java.sql.Date]("end_date")

    def * = (id, place, city, address, details, website, organizer, delegated, start_date, end_date) <> (Tournament.tupled, Tournament.unapply _)
  }

  val tournamentQ = TableQuery[TournamentTable]

  class CategoryTable(tag: Tag) extends BaseTable[Category](tag,"category"){
    def title = column[String]("title")
    def tournament = column[Long]("tournament")
    def time_limit = column[Int]("time_limit")
    def format = column[Long]("format")

    def * = (id, title, tournament, time_limit, format) <> (Category.tupled, Category.unapply _)
  }

  val categoryQ = TableQuery[CategoryTable]

  class EventTable(tag: Tag) extends BaseTable[Event](tag,"event"){
    def start_date = column[java.sql.Timestamp]("start_date")
    def round = column[Int]("round")
    def categoryId = column[Long]("category_id")

    def * = (id, start_date, round, categoryId) <> (Event.tupled, Event.unapply _)
  }

  val eventQ = TableQuery[EventTable]

  class ParticipantTable(tag: Tag) extends BaseTable[Participant](tag,"participant"){
    def name = column[String]("name")
    def rut = column[Long]("rut")
    def email = column[String]("email")
    def WCAID = column[String]("WCAID")
    def gender = column[String]("gender")
    def birth_date = column[java.sql.Date]("birth_date")

    def * = (id, name, rut, email, WCAID, gender, birth_date) <> (Participant.tupled, Participant.unapply _)
  }

  val participantQ = TableQuery[ParticipantTable]

  class EventParticipantTable(tag: Tag) extends BaseTable[EventParticipant](tag, "event_participant"){
    def eventId = column[Long]("event_id")
    def participantId = column[Long]("participant_id")

    def * = (id, eventId, participantId) <> (EventParticipant.tupled, EventParticipant.unapply _)
  }

  val eventParticipantQ = TableQuery[EventParticipantTable]


  class ResultTable(tag: Tag) extends BaseTable[Result](tag, "result"){
    def participantId = column[Long]("participant_id")
    def eventId = column[Long]("event_id")
    def time = column[Int]("time")

    def * = (id, participantId, eventId, time) <> (Result.tupled, Result.unapply _)
  }

  val resultQ = TableQuery[ResultTable]
}

