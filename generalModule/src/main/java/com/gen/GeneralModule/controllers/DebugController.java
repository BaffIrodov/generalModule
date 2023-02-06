package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.entities.*;
import com.gen.GeneralModule.repositories.*;
import com.gen.GeneralModule.services.DebugService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.hibernate.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/debug")
@Log4j2
public class DebugController {
    @Autowired
    DebugService debugService;

    @Autowired
    JPAQueryFactory queryFactory;


    @Autowired
    ErrorsRepository errorsRepository;

    @Autowired
    MatchesLinkRepository matchesLinkRepository;

    @Autowired
    BetConditionRepository betConditionRepository;

    @Autowired
    PlayerOnMapResultsRepository playerOnMapResultsRepository;

    @Autowired
    ResultsLinkRepository resultsLinkRepository;

    @Autowired
    RoundHistoryRepository roundHistoryRepository;

    @Autowired
    StatsResponseRepository statsResponseRepository;

    private static final QResultsLink resultsLink = new QResultsLink("resultsLink");
    private static final QPlayerOnMapResults playerOnMapResults = new QPlayerOnMapResults("playerOnMapResults");
    private static final QRoundHistory roundHistory = new QRoundHistory("roundHistory");
    private static final QStatsResponse statsResponse = new QStatsResponse("statsResponse");
    private static final QErrors errors = new QErrors("errors");
    private static final QMatchesLink matchesLink = new QMatchesLink("matchesLink");
    private static final QBetCondition betCondition = new QBetCondition("betCondition");

    @GetMapping("/results-link-processed/{value}")
    public void setResultsLingProcessed(@PathVariable Boolean value) {
        debugService.setResultsLinkProcessed(value);
    }

    @GetMapping("/enabled")
    public Boolean thisServiceEnabled() {
        return true;
    }

    @GetMapping("/errors-exist")
    public Boolean errorsExist() {
        try {
            queryFactory.from(errors).select(errors.Id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Transactional
    @GetMapping("/create-errors")
    public void createErrorsTable() {
        errorsRepository.createErrorsTable();
    }

    @GetMapping("/matches-link-exist")
    public Boolean matchesLinkExist() {
        try {
            queryFactory.from(matchesLink).select(matchesLink.matchId).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Transactional
    @GetMapping("/create-matches-link")
    public void createMatchesLinkTable() {
        matchesLinkRepository.createMatchesLinkTable();
    }

    @GetMapping("/bet-condition-exist")
    public Boolean betConditionExist() {
        try {
            queryFactory.from(betCondition).select(betCondition.matchId).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Transactional
    @GetMapping("/create-bet-condition")
    public void createBetConditionTable() {
        betConditionRepository.createBetConditionTable();
    }

    @GetMapping("/player-on-map-results-exist")
    public Boolean playerOnMapResultsExist() {
        try {
            queryFactory.from(playerOnMapResults).select(playerOnMapResults.id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @GetMapping("/player-on-map-results-filled")
    public Boolean playerOnMapResultsFilled() {
        Integer tableSize = 0;
        try {
            tableSize = queryFactory.from(playerOnMapResults).select(playerOnMapResults.id).fetch().size();
        } catch (Exception exception) {
            return false;
        }
        return tableSize != 0;
    }

    @Transactional
    @GetMapping("/create-player-on-map-results")
    public void createPlayerOnMapResultsTable() {
        playerOnMapResultsRepository.createPlayerOnMapResultsTable();
    }

    @GetMapping("/results-link-exist")
    public Boolean resultsLinkExist() {
        try {
            queryFactory.from(resultsLink).select(resultsLink.resultId).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @GetMapping("/results-link-filled")
    public Boolean resultsLinkFilled() {
        Integer tableSize = 0;
        try {
            tableSize = queryFactory.from(resultsLink).select(resultsLink.resultId).fetch().size();
        } catch (Exception exception) {
            return false;
        }
        return tableSize != 0;
    }

    @Transactional
    @GetMapping("/create-results-link")
    public void createResultsLinkTable() {
        resultsLinkRepository.createResultsLinkTable();
    }

    @GetMapping("/round-history-exist")
    public Boolean roundHistoryExist() {
        try {
            queryFactory.from(roundHistory).select(roundHistory.id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @GetMapping("/round-history-filled")
    public Boolean roundHistoryFilled() {
        Integer tableSize = 0;
        try {
            tableSize = queryFactory.from(roundHistory).select(roundHistory.id).fetch().size();
        } catch (Exception exception) {
            return false;
        }
        return tableSize != 0;
    }

    @Transactional
    @GetMapping("/create-round-history")
    public void createRoundHistoryTable() {
        roundHistoryRepository.createRoundHistoryTable();
    }

    @GetMapping("/stats-response-exist")
    public Boolean statsResponseExist() {
        try {
            queryFactory.from(statsResponse).select(statsResponse.id).fetch();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Transactional
    @GetMapping("/create-stats-response")
    public void createStatsResponseTable() {
        statsResponseRepository.createStatsResponseTable();
    }
}
