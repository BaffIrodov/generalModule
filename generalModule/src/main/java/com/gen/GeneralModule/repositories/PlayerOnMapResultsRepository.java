package com.gen.GeneralModule.repositories;


import com.gen.GeneralModule.entities.PlayerOnMapResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlayerOnMapResultsRepository extends JpaRepository<PlayerOnMapResults, Integer> {
    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS player_on_map_results;\n" +
                    "CREATE TABLE IF NOT EXISTS player_on_map_results\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"player_id\" int8 not null,\n" +
                    "    \"id_stats_map\" int8 not null,\n" +
                    "    \"url\" VARCHAR(200),\n" +
                    "    \"player_name\" VARCHAR(200),\n" +
                    "    \"date_of_match\" date,\n" +
                    "    \"played_map\" VARCHAR(200),\n" +
                    "    \"played_map_string\" VARCHAR(200),\n" +
                    "    \"team\" VARCHAR(200),\n" +
                    "    \"team_winner\" VARCHAR(200),\n" +
                    "    \"kills\" int8,\n" +
                    "    \"assists\" int8,\n" +
                    "    \"deaths\" int8,\n" +
                    "    \"kd\" float8,\n" +
                    "    \"headshots\" int8,\n" +
                    "    \"adr\" float8,\n" +
                    "    \"rating20\" float8,\n" +
                    "    \"cast20\" float8\n" +
                    "    );\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_player_on_map_results_id\";\n" +
                    "CREATE SEQUENCE \"sq_player_on_map_results_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createPlayerOnMapResultsTable();
}
