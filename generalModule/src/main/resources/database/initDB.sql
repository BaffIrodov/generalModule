DROP TABLE IF EXISTS player;
CREATE TABLE IF NOT EXISTS player
(
    "id" int8 not null,
    "id_stats_map" VARCHAR(200) not null,
    "url" VARCHAR(200),
    "player_name" VARCHAR(200),
    "date_of_match" date,
    "played_map" VARCHAR(200),
    "team" VARCHAR(200),
    "kills" int8,
    "assists" int8,
    "deaths" int8,
    "kd" float8,
    "headshots" int8,
    "adr" float8,
    "rating20" float8,
    "cast20" float8
    );
DROP TABLE IF EXISTS player_on_map_results;
CREATE TABLE IF NOT EXISTS player_on_map_results
(
    "id" int8 not null,
    "id_stats_map" VARCHAR(200) not null,
    "url" VARCHAR(200),
    "player_name" VARCHAR(200),
    "date_of_match" date,
    "played_map" VARCHAR(200),
    "team" VARCHAR(200),
    "kills" int8,
    "assists" int8,
    "deaths" int8,
    "kd" float8,
    "headshots" int8,
    "adr" float8,
    "rating20" float8,
    "cast20" float8
    );
DROP TABLE IF EXISTS results_link;
CREATE TABLE IF NOT EXISTS results_link
(
    "id" int8 not null,
    "match_url" VARCHAR(200) not null,
    "processed" BOOLEAN,
    "archive" BOOLEAN
    );

DROP SEQUENCE IF EXISTS "sq_results_link_id";
CREATE SEQUENCE "sq_results_link_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

DROP TABLE IF EXISTS matches_link;
CREATE TABLE IF NOT EXISTS matches_link
(
    "id" int8 not null,
    "match_url" VARCHAR(200) not null,
    "left_team" VARCHAR(200) not null,
    "right_team" VARCHAR(200) not null
    );