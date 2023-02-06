package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.BetCondition;
import com.gen.GeneralModule.entities.MatchesLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BetConditionRepository extends JpaRepository<BetCondition, Integer> {

    @Modifying
    @Query(
            value = "DROP TABLE IF EXISTS bet_condition;\n" +
                    "CREATE TABLE IF NOT EXISTS bet_condition\n" +
                    "(\n" +
                    "    \"match_id\" int8 not null,\n" +
                    "    \"already_bet\" int8 not null,\n" +
                    "    \"bet_limit\" int8 not null,\n" +
                    "    \"dont_show\" boolean,\n" +
                    "    \"it_was_won\" boolean\n" +
                    ");",
            nativeQuery = true
    )
    @Transactional
    void createBetConditionTable();

}
