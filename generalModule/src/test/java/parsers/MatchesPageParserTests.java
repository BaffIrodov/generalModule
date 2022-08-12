package parsers;

import com.gen.GeneralModule.parsers.MatchPageParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

public class MatchesPageParserTests {

    @MockBean
    private MatchPageParser matchPageParser = new MatchPageParser();

    @Test
    public void matchLinks(List<String> links, Document doc) {
        Assertions.assertFalse(links.contains(""));
        Assertions.assertFalse(links.contains(null));
        Assertions.assertNotEquals(0, links.size());
        Assertions.assertNotEquals(null, doc);
    }

    @Test
    public void matchPlayersNumberAndNotNull(Document doc) {
        Elements elementsWithPlayers = doc.body().getElementsByClass("player-photo");
        // Проверка на то, что в матче присутствуют 10 игроков
        Assertions.assertEquals(elementsWithPlayers.size(), 10);
        elementsWithPlayers.forEach(player -> {
            // Проверка на то, что игрок не пустой
            Assertions.assertNotEquals("", player.attributes().get("alt"));
        });
    }

    @Test
    public void matchPlayersId(Document doc) {
        List<List<String>> teams = matchPageParser.getAllPlayers(doc);
        // Проверка на то, записываются ли все id игроков
        Assertions.assertNotEquals(null, teams);
        teams.forEach(team -> {
            Assertions.assertFalse(team.contains(""));
            Assertions.assertFalse(team.contains(null));
        });
    }

    @Test
    public void matchTeamNames(Document doc) {
        List<String> teamNames = matchPageParser.getTeamsNames(doc);
        Assertions.assertEquals(teamNames.size(), 2);
        Assertions.assertFalse(teamNames.contains(""));
        Assertions.assertFalse(teamNames.contains(null));
    }

    @Test
    public void matchFormat(Document doc) {
        String format = matchPageParser.getMatchFormat(doc);
        Assertions.assertTrue(format.matches(".*[135]$"));
    }

    @Test
    public void matchMapNames(Document doc) {
        List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
        Assertions.assertFalse(mapNames.contains(""));
        Assertions.assertFalse(mapNames.contains(null));
    }

    @Test
    public void matchTeamOdds(Document doc) {
        List<String> teamOdds = matchPageParser.getTeamsOdds(doc);
        Assertions.assertTrue(teamOdds.size() >= 2);
    }
}
