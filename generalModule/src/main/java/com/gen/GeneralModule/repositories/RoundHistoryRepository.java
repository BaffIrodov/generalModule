package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.RoundHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RoundHistoryRepository extends JpaRepository<RoundHistory, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS round_history;\n" +
                    "CREATE TABLE IF NOT EXISTS round_history\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"id_stats_map\" int8 not null,\n" +
                    "    \"date_of_match\" timestamp not null,\n" +
                    "    \"round_sequence\" VARCHAR(200) not null,\n" +
                    "    \"left_team_is_terrorists_in_first_half\" BOOLEAN\n" +
                    "    );\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_round_history_id\";\n" +
                    "CREATE SEQUENCE \"sq_round_history_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createRoundHistoryTable();

}
