package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.StatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StatsResponseRepository extends JpaRepository<StatsResponse, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS stats_response;\n" +
                    "CREATE TABLE IF NOT EXISTS stats_response\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"batch_size\" int8,\n" +
                    "    \"batch_time\" int8,\n" +
                    "    \"request_date\" timestamp\n" +
                    "    );\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_stats_response_id\";\n" +
                    "CREATE SEQUENCE \"sq_stats_response_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createStatsResponseTable();

}
