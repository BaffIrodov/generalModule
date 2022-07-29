package parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MatchesPageParserTests {

    @MockBean
    private MatchesPageParser matchesPageParser = new MatchesPageParser();

    @MockBean
    private MatchPageParser matchPageParser = new MatchPageParser();

    final List<String> links = matchesPageParser.parseMatches();
    final Document doc = CommonUtils.reliableConnectAndGetDocument(links.get(0));

    @Order(1)
    @Test
    public void matchLinks() {
        Assertions.assertFalse(links.contains(""));
        Assertions.assertFalse(links.contains(null));
        Assertions.assertNotEquals(null, doc);
    }

    @Order(2)
    @Test
    public void matchPlayersNumberAndNotNull() {
        Elements elementsWithPlayers = doc.body().getElementsByClass("player-photo");
        // Проверка на то, что в матче присутствуют 10 игроков
        Assertions.assertEquals(elementsWithPlayers.size(), 10);
        elementsWithPlayers.forEach(player -> {
            // Проверка на то, что игрок не пустой
            Assertions.assertNotEquals("", player.attributes().get("alt"));
        });
    }

    @Order(3)
    @Test
    public void matchPlayersId() {
        List<List<String>> teams = matchPageParser.getAllPlayers(doc);
        // Проверка на то, записываются ли все id игроков
        Assertions.assertNotEquals(null, teams);
        teams.forEach(team -> {
            Assertions.assertFalse(team.contains(""));
            Assertions.assertFalse(team.contains(null));
        });
    }

    @Order(4)
    @Test
    public void matchTeamNames() {
        List<String> teamNames = matchPageParser.getTeamsNames(doc);
        Assertions.assertEquals(teamNames.size(), 2);
        Assertions.assertFalse(teamNames.contains(""));
        Assertions.assertFalse(teamNames.contains(null));
    }

    @Order(5)
    @Test
    public void matchFormat() {
        String format = matchPageParser.getMatchFormat(doc);
        Assertions.assertTrue(format.matches(".*[135]$"));
    }

    @Order(6)
    @Test
    public void matchMapNames() {
        List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
        Assertions.assertFalse(mapNames.contains(""));
        Assertions.assertFalse(mapNames.contains(null));
    }

    @Order(7)
    @Test
    public void matchTeamOdds() {
        List<String> teamOdds = matchPageParser.getTeamsOdds(doc);
        Assertions.assertTrue(teamOdds.size() >= 2);
    }
}
