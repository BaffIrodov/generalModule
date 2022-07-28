package parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

public class MatchesPageParserTests {

    @MockBean
    private MatchesPageParser matchesPageParser = new MatchesPageParser();

    @MockBean
    private MatchPageParser matchPageParser = new MatchPageParser();

    @Test
    void matchPlayers() {
        String link = matchesPageParser.parseMatches().get(0);
        System.out.println(link);
        Document doc = CommonUtils.reliableConnectAndGetDocument(link);
        if (doc != null) {
            Elements elementsWithPlayers = doc.body().getElementsByClass("player-photo");
            // Проверка на то, что в матче присутствуют 10 игроков
            Assertions.assertEquals(elementsWithPlayers.size(), 10);
            elementsWithPlayers.forEach(player -> {
                // Проверка на то, что игрок не пустой
                Assertions.assertNotEquals("", player.attributes().get("alt"));
            });
            List<List<String>> teams = matchPageParser.getAllPlayers(doc);
            // Проверка на то, записываются ли все id игроков
            Assertions.assertNotEquals(null, teams);
            Assertions.assertNotEquals("", teams);
        }
    }
}
