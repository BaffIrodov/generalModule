package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.common.Config;
import com.gen.GeneralModule.common.MapsEnum;
import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.dtos.MatchesWithTimeDto;
import com.gen.GeneralModule.entities.BetCondition;
import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.entities.QBetCondition;
import com.gen.GeneralModule.entities.QPlayerForce;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.repositories.BetConditionRepository;
import com.gen.GeneralModule.services.MatchesParserService;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/bets")
public class BetsController {
    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    BetConditionRepository betConditionRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    private static final QBetCondition betCondition = new QBetCondition("betCondition");

    @PostMapping("/set-bet")
    public String setBet(@RequestBody Map<Integer, Integer> betByMatchId) {
        Integer matchId = betByMatchId.keySet().stream().toList().get(0);
        Integer betSize = betByMatchId.values().stream().toList().get(0);
        BetCondition betCondition = betConditionRepository.findById(matchId).orElse(null);
        if (betCondition == null) {
            betCondition = new BetCondition();
            betCondition.matchId = matchId;
            betCondition.alreadyBet = betSize;
            betCondition.betLimit = Config.betLimit;
            betCondition.dontShow = false;
        } else {
            betCondition.alreadyBet += betSize;
        }
        betConditionRepository.save(betCondition);
        return "Успешно: матч " + betCondition.matchId + ", ставка: " + betCondition.alreadyBet;
    }

    @PostMapping("/set-dont-show")
    public String setDontShow(@RequestBody Integer matchId) {
        BetCondition betCondition = betConditionRepository.findById(matchId).orElse(null);
        if (betCondition == null) {
            betCondition = new BetCondition();
            betCondition.matchId = matchId;
            betCondition.alreadyBet = 0;
            betCondition.betLimit = Config.betLimit;
            betCondition.dontShow = true;
        } else {
            betCondition.dontShow = true;
        }
        betConditionRepository.save(betCondition);
        return "Успешно: матч " + betCondition.matchId + " больше не будет показан";
    }

    @PostMapping("/set-match-won")
    public String setMatchWon(@RequestBody Integer matchId) {
        BetCondition betCondition = betConditionRepository.findById(matchId).orElse(null);
        if (betCondition == null) {
            betCondition = new BetCondition();
            betCondition.matchId = matchId;
            betCondition.alreadyBet = 0;
            betCondition.betLimit = Config.betLimit;
            betCondition.dontShow = false;
            betCondition.itWasWon = true;
        } else {
            betCondition.itWasWon = true;
        }
        betConditionRepository.save(betCondition);
        return "Успешно: матч " + betCondition.matchId + " выигран";
    }

    @PostMapping("/set-match-lost")
    public String setMatchLost(@RequestBody Integer matchId) {
        BetCondition betCondition = betConditionRepository.findById(matchId).orElse(null);
        if (betCondition == null) {
            betCondition = new BetCondition();
            betCondition.matchId = matchId;
            betCondition.alreadyBet = 0;
            betCondition.betLimit = Config.betLimit;
            betCondition.dontShow = false;
            betCondition.itWasWon = false;
        } else {
            betCondition.itWasWon = false;
        }
        betConditionRepository.save(betCondition);
        return "Успешно: матч " + betCondition.matchId + " проигран";
    }
}
