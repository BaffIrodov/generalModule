package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.MatchesLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MatchesLinkRepository extends JpaRepository<MatchesLink, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS matches_link;\n" +
                    "CREATE TABLE IF NOT EXISTS matches_link\n" +
                    "(\n" +
                    "    \"match_id\" int8 not null,\n" +
                    "    \"match_url\" VARCHAR(200) not null,\n" +
                    "    \"left_team\" VARCHAR(200) not null,\n" +
                    "    \"right_team\" VARCHAR(200) not null,\n" +
                    "    \"match_format\" VARCHAR(200) not null,\n" +
                    "    \"match_maps_names\" VARCHAR(200),\n" +
                    "    \"left_team_odds\" VARCHAR(200),\n" +
                    "    \"right_team_odds\" VARCHAR(200),\n" +
                    "    \"match_time\" int8\n" +
                    "    );",
            nativeQuery = true
    )
    @Transactional
    void createMatchesLinkTable();

}
