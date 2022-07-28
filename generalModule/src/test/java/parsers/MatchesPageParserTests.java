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
            // �������� �� ��, ��� � ����� ������������ 10 �������
            Assertions.assertEquals(elementsWithPlayers.size(), 10);
            elementsWithPlayers.forEach(player -> {
                // �������� �� ��, ��� ����� �� ������
                Assertions.assertNotEquals("", player.attributes().get("alt"));
            });
            List<List<String>> teams = matchPageParser.getAllPlayers(doc);
            // �������� �� ��, ������������ �� ��� id �������
            Assertions.assertNotEquals(null, teams);
            Assertions.assertNotEquals("", teams);
        }
    }
}
