package com.gen.GeneralModule;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.parsers.ResultsPageParser;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import parsers.MatchesPageParserTests;
import parsers.ResultsPageParserTests;

import java.util.List;

@SpringBootTest
class GeneralModuleApplicationTests {
    MatchesPageParserTests matchesPageParserTests = new MatchesPageParserTests();
    ResultsPageParserTests resultsPageParserTests = new ResultsPageParserTests();

    @Test
    void matchesPageParserTest() {
        MatchesPageParser matchesPageParser = new MatchesPageParser();
        List<String> links = matchesPageParser.parseMatches();
        Document doc = CommonUtils.reliableConnectAndGetDocument(links.get(0));

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
        Document doc = CommonUtils.reliableConnectAndGetDocument(links.get(0));

        resultsPageParserTests.resultLinks(links, doc);

    }

}
