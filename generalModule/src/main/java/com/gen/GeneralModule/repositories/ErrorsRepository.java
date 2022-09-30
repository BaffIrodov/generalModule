package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.Errors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ErrorsRepository extends JpaRepository<Errors, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS errors;\n" +
                    "CREATE TABLE IF NOT EXISTS errors\n" +
                    "(\n" +
                    "    \"id\" int8 not null,\n" +
                    "    \"class_and_line\" VARCHAR(200) not null,\n" +
                    "    \"description_error\" VARCHAR(200) not null,\n" +
                    "    \"verification_error\" BOOLEAN,\n" +
                    "    \"payload\" VARCHAR(200),\n" +
                    "    \"date_time\" timestamp\n" +
                    ");\n" +
                    "\n" +
                    "DROP SEQUENCE IF EXISTS \"sq_errors_id\";\n" +
                    "CREATE SEQUENCE \"sq_errors_id\"\n" +
                    "    INCREMENT 1\n" +
                    "    MINVALUE  1\n" +
                    "    MAXVALUE 9223372036854775807\n" +
                    "    START 1\n" +
                    "    CACHE 1;",
            nativeQuery = true
    )
    @Transactional
    void createErrorsTable();

}
