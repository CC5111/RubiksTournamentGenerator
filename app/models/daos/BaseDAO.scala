package models.daos

import javax.inject.{Singleton, Inject}

import models.entities.{Supplier, BaseEntity}
import models.persistence.SlickTables
import models.persistence.SlickTables.{SuppliersTable, BaseTable}
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.{CanBeQueryCondition}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.entities._

@Singleton
class TournamentDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  import dbConfig.db

  protected val tableQ = SlickTables.tournamentQ

  def all: Future[Seq[Tournament]] = {
    db.run(tableQ.result)
  }

  def insert(tournament: Tournament): Future[Long] ={
    db.run(tableQ returning tableQ.map(_.id) += tournament)
  }


  def byId(id: Long): Future[Option[Tournament]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def update(tournament: Tournament): Future[Int] = {
    if (tournament.isValid)
      db.run(tableQ.filter(_.id === tournament.id).update(tournament))
    else
      Future{0}
  }
}

@Singleton
class CategoryDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  import dbConfig.db

  protected val tableQ = SlickTables.categoryQ

  def all: Future[Seq[Category]] = {
    db.run(tableQ.result)
  }

  def insert(category: Category): Future[Long] ={
    db.run(tableQ returning tableQ.map(_.id) += category)
  }


  def byId(id: Long): Future[Option[Category]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def update(category: Category): Future[Int] = {
    if (category.isValid)
      db.run(tableQ.filter(_.id === category.id).update(category))
    else
      Future{0}
  }
}

@Singleton
class EventDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider){
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  import dbConfig.db

  protected val tableQ = SlickTables.eventQ

  def all: Future[Seq[Event]] = {
    db.run(tableQ.result)
  }

  def insert(event: Event): Future[Long] ={
    db.run(tableQ returning tableQ.map(_.id) += event)
  }

  def byId(id: Long): Future[Option[Event]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def update(event: Event): Future[Int] = {
    if (event.isValid)
      db.run(tableQ.filter(_.id === event.id).update(event))
    else
      Future{0}
  }

  def getAllParticipant = {
    for {
      e <- tableQ
      ep <- SlickTables.eventParticipantQ if e.id === ep.participantId
      p <- SlickTables.participantQ if ep.participantId === p.id
    } yield p
  }


}











trait AbstractBaseDAO[T,A] {
  def insert(row : A): Future[Long]
  def insert(rows : Seq[A]): Future[Seq[Long]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : Long): Future[Option[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id : Long): Future[Int]
  def deleteById(ids : Seq[Long]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
}


abstract class BaseDAO[T <: BaseTable[A], A <: BaseEntity]() extends AbstractBaseDAO[T,A] with HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  protected val tableQ: TableQuery[T]

  def insert(row : A): Future[Long] ={
    insert(Seq(row)).map(_.head)
  }

  def insert(rows : Seq[A]): Future[Seq[Long]] ={
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row : A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future{0}
  }

  def update(rows : Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def findById(id : Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def deleteById(id : Long): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids : Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }



}