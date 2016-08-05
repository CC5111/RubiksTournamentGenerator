# --- !Ups

create table "tournament" (
  "id" 	 BIGSERIAL NOT NULL PRIMARY KEY,
	"place"	      VARCHAR(256),
	"organizer"	  BIGINT,
	"delegated"	  BIGINT,
  "start_date"	TIMESTAMP,
  "end_date"    TIMESTAMP
  );

create table "category" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"title"	        VARCHAR(256),
	"tournamentId"	BIGINT,
  "time_limit"    BIGINT,
  "formatId"      BIGINT
  );

create table "event" (
	"id" 	        BIGSERIAL NOT NULL PRIMARY KEY,
  "start_date"	TIMESTAMP,
  "round"       BIGINT,
  "categoryId"  BIGINT
  );

create table "participant" (
	"id" 	       BIGSERIAL NOT NULL PRIMARY KEY,
	"name"	     VARCHAR(256),
	"rut"	       BIGINT,
	"email"	     VARCHAR(256),
	"WCAID"	     VARCHAR(256),
	"gender"	   VARCHAR(256),
  "birth_date" TIMESTAMP
  );

create table "eventParticipant" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"eventId"	      BIGINT,
  "participantId" BIGINT
 );

create table "result" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"participantId"	BIGINT,
	"eventId"	      BIGINT,
  "time"          DECIMAL(10,6)
  );

# --- !Downs
;
drop table "tournament";
drop table "category";
drop table "event";
drop table "participant";
drop table "eventParticipant";
drop table "result";