# --- !Ups

create table "tournament" (
  "id" 	 BIGSERIAL NOT NULL PRIMARY KEY,
  "name"        VARCHAR(256),
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

create table "tournamentEvent" (
	"id" 	          BIGSERIAL NOT NULL PRIMARY KEY,
	"title"	        VARCHAR(256),
	"tournament_id"	BIGINT,
  "time_limit"    BIGINT,
  "format_id"     BIGINT,
	"rounds"      	BIGINT
  );

create table "round" (
	"id" 	        BIGSERIAL NOT NULL PRIMARY KEY,
  "start_date"	TIMESTAMP,
  "round"       BIGINT,
  "tournamentEvent_id"  BIGINT
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


create table "event"(
  "id"    BIGINT NOT NULL PRIMARY KEY,
  "name"  VARCHAR(256)
  );

INSERT INTO "event" ("id","name") VALUES (1,'3x3');
INSERT INTO "event" ("id","name") VALUES (2,'2x2');
INSERT INTO "event" ("id","name") VALUES (3,'4x4');
INSERT INTO "event" ("id","name") VALUES (4,'5x5');
INSERT INTO "event" ("id","name") VALUES (5,'3x3 Blindfolded');
INSERT INTO "event" ("id","name") VALUES (6,'3x3 One-Hand');
INSERT INTO "event" ("id","name") VALUES (7,'3x3 Fewest Move');
INSERT INTO "event" ("id","name") VALUES (8,'3x3 With Feet');
INSERT INTO "event" ("id","name") VALUES (9,'Megaminx');
INSERT INTO "event" ("id","name") VALUES (10,'Pyraminx');
INSERT INTO "event" ("id","name") VALUES (11,'Square-1');
INSERT INTO "event" ("id","name") VALUES (12,'Clock');
INSERT INTO "event" ("id","name") VALUES (13,'Skewb');
INSERT INTO "event" ("id","name") VALUES (14,'6x6');
INSERT INTO "event" ("id","name") VALUES (15,'7x7');
INSERT INTO "event" ("id","name") VALUES (16,'4x4 Blindfolded');
INSERT INTO "event" ("id","name") VALUES (17,'5x5 Blindfolded');
INSERT INTO "event" ("id","name") VALUES (18,'3x3 Multi-Blindfolded');


# --- !Downs
;
drop table "tournament";
drop table "tournamentEvent";
drop table "event";
drop table "round";
drop table "participant";
drop table "eventParticipant";
drop table "result";