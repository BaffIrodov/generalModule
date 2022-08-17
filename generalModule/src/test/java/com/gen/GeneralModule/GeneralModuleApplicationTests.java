package com.gen.GeneralModule;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.RoundHistory;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.parsers.ResultPageParser;
import com.gen.GeneralModule.parsers.ResultsPageParser;
import com.gen.GeneralModule.parsers.StatsPageParser;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import parsers.MatchesPageParserTests;
import parsers.ResultsPageParserTests;
import parsers.StatsPageParserTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GeneralModuleApplicationTests {

    @Autowired
    private CommonUtils commonUtils;

    MatchesPageParserTests matchesPageParserTests = new MatchesPageParserTests();
    ResultsPageParserTests resultsPageParserTests = new ResultsPageParserTests();
    StatsPageParserTests statsPageParserTests = new StatsPageParserTests();

    @Test
    void matchesPageParserTest() {
        MatchesPageParser matchesPageParser = new MatchesPageParser();
        List<String> links = matchesPageParser.parseMatches();
        Document doc = commonUtils.reliableConnectAndGetDocument(links.get(0));

        matchesPageParserTests.matchLinks(links, doc);
        matchesPageParserTests.matchPlayersNumberAndNotNull(doc);
        matchesPageParserTests.matchPlayersId(doc);
        matchesPageParserTests.matchFormat(doc);
        matchesPageParserTests.matchTeamNames(doc);
        matchesPageParserTests.matchMapNames(doc);
        matchesPageParserTests.matchTeamOdds(doc);
    }

    @Test
    void resultsPageParserTest() {
        ResultsPageParser resultsPageParser = new ResultsPageParser();
        List<String> links = resultsPageParser.parseResultsGetAllLinks(0);
        Document doc = commonUtils.reliableConnectAndGetDocument(links.get(0));

        resultsPageParserTests.resultLinks(links, doc);
        resultsPageParserTests.statsLinks(doc);
    }

    @Test
    void statsPageParserTest() {
        ResultsPageParser resultsPageParser = new ResultsPageParser();
        List<String> matchLinks = resultsPageParser.parseResultsGetAllLinks(0);
        Document doc = commonUtils.reliableConnectAndGetDocument(matchLinks.get(0));
        ResultPageParser resultPageParser = new ResultPageParser();
        List<String> statsLinks = resultPageParser.getAllStatsLinks(doc);
        doc = commonUtils.reliableConnectAndGetDocument(statsLinks.get(0));

        statsPageParserTests.statsLinks(statsLinks, doc);
        statsPageParserTests.webElementsExist(doc);
        StatsPageParser statsPageParser = new StatsPageParser();
        Map.Entry map = statsPageParser.parseMapStats(statsLinks.get(0)).entrySet()
                .stream()
                .findFirst()
                .get();
        List<PlayerOnMapResults> players = (List<PlayerOnMapResults>) map.getKey();
        RoundHistory roundHistory = (RoundHistory) map.getValue();
        statsPageParserTests.fullRoundHistory(roundHistory.roundSequence);
        statsPageParserTests.teamIsTerrorist(doc);
        statsPageParserTests.allPlayers(players);
        statsPageParserTests.winner(doc, players.get(0));
    }

}
