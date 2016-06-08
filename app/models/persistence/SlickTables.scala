package models.persistence

import models.entities.Supplier
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

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
    def organizer = column[Long]("organizer")
    def delegated = column[Long]("delegated")
    def start_date = column[java.sql.Timestamp]("start_date")
    def end_date = column[java.sql.Timestamp]("end_date")

    def * = (id, place, organizer, delegated, start_date, end_date) <> (Tournament.tuppled, Tournament.unapply)
  }

  class CategoryTable(tag: Tag) extends BaseTable[Category](tag,"category"){
    def title = column[String]("title")
    def tournament = column[Long]("tournament")
    time_limit: Integer,
    format: Integer
  }

  class EventTable(tag: Tag) extends BaseTable[Event](tag,"event"){

  }

  class ResultTable(tag: Tag) extends BaseTable[Result](tag,"result"){

  }

  class ParticipantTable(tag: Tag) extends BaseTable[Participant](tag,"participant"){

  }

  class EventParaticipantTable(tag: Tag) extends BaseTable[EventParaticipant](tag,"event_paraticipant"){

  }

  class UserAdminTable(tag: Tag) extends BaseTable[UserAdmin](tag,"user_admin"){

  }
}
