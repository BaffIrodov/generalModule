package com.gen.GeneralModule.repositories;


import com.gen.GeneralModule.entities.ResultsLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResultsLinkRepository extends JpaRepository<ResultsLink, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS results_link;\n" +
                    "CREATE TABLE IF NOT EXISTS results_link\n" +
                    "(\n" +
                    "    \"result_id\" int8 not null,\n" +
                    "    \"result_url\" VARCHAR(200) not null,\n" +
                    "    \"processed\" BOOLEAN,\n" +
                    "    \"archive\" BOOLEAN\n" +
                    "    );\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_results_link_id\";\n" +
                    "CREATE SEQUENCE \"sq_results_link_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createResultsLinkTable();

}
