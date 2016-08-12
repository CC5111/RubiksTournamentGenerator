package models.entities

/**
  * Created by milenkotomic on 28-07-16.
  */
case class Tournament(id: Long,
                      name: String,
                      place: String,
                      city: String,
                      address: String,
                      details: String,
                      website: String,
                      organizer: String,
                      delegated: String,
                      start_date: java.sql.Date,
                      end_date: java.sql.Date) extends BaseEntity

case class TournamentEvents(id: Long,
                            title: String,
                            tournamentId: Long,
                            time_limit: Int,
                            formatId: Long,
                            rounds: Int) extends BaseEntity

case class Round(id: Long,
                 start_date: java.sql.Timestamp,
                 round: Int,
                 tournamentEventId: Long) extends BaseEntity

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


case class Event(id: Long,
                  name: String
                 ) extends BaseEntity

