package models.entities

/**
  * Created by milenkotomic on 28-07-16.
  */
case class Tournament(id: Long,
                      place: String,
                      organizer: Long,
                      delegated: Long,
                      start_date: java.sql.Timestamp,
                      end_date: java.sql.Timestamp) extends BaseEntity

case class Category(id: Long,
                    title: String,
                    tournamentId: Long,
                    time_limit: Int,
                    formatId: Long) extends BaseEntity

case class Event(id: Long,
                 start_date: java.sql.Timestamp,
                 round: Int,
                 categoryId: Long) extends BaseEntity

case class Participant(id: Long,
                       name: String,
                       rut: Long,
                       email: String,
                       WCAID: String,
                       gender: String,
                       birth_date: java.sql.Date) extends BaseEntity

case class EventParticipant(id: Long,
                            eventId: Long,
                            participantId: Long) extends BaseEntity

case class Result(id: Long,
                  participantId: Long,
                  eventId: Long,
                  time: Int) extends BaseEntity




