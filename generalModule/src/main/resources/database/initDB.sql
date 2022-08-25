DROP TABLE IF EXISTS player_on_map_results;
CREATE TABLE IF NOT EXISTS player_on_map_results
(
    "id" int8 not null,
    "player_id" int8 not null,
    "id_stats_map" int8 not null,
    "url" VARCHAR(200),
    "player_name" VARCHAR(200),
    "date_of_match" date,
    "played_map" VARCHAR(200),
    "played_map_string" VARCHAR(200),
    "team" VARCHAR(200),
    "team_winner" VARCHAR(200),
    "kills" int8,
    "assists" int8,
    "deaths" int8,
    "kd" float8,
    "headshots" int8,
    "adr" float8,
    "rating20" float8,
    "cast20" float8
    );

DROP SEQUENCE IF EXISTS "sq_player_on_map_results_id";
CREATE SEQUENCE "sq_player_on_map_results_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

DROP TABLE IF EXISTS results_link;
CREATE TABLE IF NOT EXISTS results_link
(
    "result_id" int8 not null,
    "result_url" VARCHAR(200) not null,
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
    "match_id" int8 not null,
    "match_url" VARCHAR(200) not null,
    "left_team" VARCHAR(200) not null,
    "right_team" VARCHAR(200) not null,
    "match_format" VARCHAR(200) not null,
    "match_maps_names" VARCHAR(200),
    "left_team_odds" VARCHAR(200),
    "right_team_odds" VARCHAR(200),
    "match_time" int8
    );

DROP TABLE IF EXISTS round_history;
CREATE TABLE IF NOT EXISTS round_history
(
    "id" int8 not null,
    "id_stats_map" int8 not null,
    "date_of_match" timestamp not null,
    "round_sequence" VARCHAR(200) not null,
    "left_team_is_terrorists_in_first_half" BOOLEAN
    );

DROP SEQUENCE IF EXISTS "sq_round_history_id";
CREATE SEQUENCE "sq_round_history_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

DROP TABLE IF EXISTS stats_response;
CREATE TABLE IF NOT EXISTS stats_response
(
    "id" int8 not null,
    "batch_size" int8,
    "batch_time" int8,
    "request_date" timestamp
    );

DROP SEQUENCE IF EXISTS "sq_stats_response_id";
CREATE SEQUENCE "sq_stats_response_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

DROP TABLE IF EXISTS errors;
CREATE TABLE IF NOT EXISTS errors
(
    "id" int8 not null,
    "class_and_line" VARCHAR(200) not null,
    "description_error" VARCHAR(200) not null,
    "verification_error" BOOLEAN,
    "payload" VARCHAR(200),
    "date_time" timestamp
);

DROP SEQUENCE IF EXISTS "sq_errors_id";
CREATE SEQUENCE "sq_errors_id"
    INCREMENT 1
    MINVALUE  1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;