# --- !Ups

create table "tournament" (
  "id" 	 BIGSERIAL NOT NULL PRIMARY KEY,
	"place"	      VARCHAR(256),
	"city"        VARCHAR(256),
	"address"     VARCHAR(256),
	"details"     VARCHAR(256),
	"website"     VARCHAR(256),
	"organizer"	  VARCHAR(256),
	"delegated"	  VARCHAR(256),
  "start_date"	DATE,
  "end_date"    DATE
  );

create table "category" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"title"	        VARCHAR(256),
	"tournament_id"	BIGINT,
  "time_limit"    BIGINT,
  "format_id"      BIGINT
  );

create table "event" (
	"id" 	        BIGSERIAL NOT NULL PRIMARY KEY,
  "start_date"	TIMESTAMP,
  "round"       BIGINT,
  "category_id"  BIGINT
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
	"event_id"	      BIGINT,
  "participant_id" BIGINT
 );

create table "result" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"participant_id"	BIGINT,
	"event_id"	      BIGINT,
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