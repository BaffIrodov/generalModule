package parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.RoundHistory;
import com.gen.GeneralModule.parsers.StatsPageParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatsPageParserTests {

    @MockBean
    StatsPageParser statsPageParser = new StatsPageParser();

    // Проверяем ссылки на статсы и страницу со статистикой.
    @Test
    public void statsLinks(List<String> links, Document doc) {
        Assertions.assertFalse(links.contains(""));
        Assertions.assertFalse(links.contains(null));
        Assertions.assertNotEquals(0, links.size());
        Assertions.assertTrue(links.size() <= 5);
        Assertions.assertNotEquals(null, doc);
    }

    // Проверяем, что на странице со статистикой присутствуют нужные веб-элементы.
    @Test
    public void webElementsExist(Document doc) {
        // Табличка с информацией о матче.
        Elements webElement = doc.body().getElementsByClass("match-info-box");
        Assertions.assertEquals(1, webElement.size());
        // Табличка с результатами матча, в которой должно быть две строки для двух команд.
        webElement = doc.body().getElementsByClass("standard-box round-history-con");
        Assertions.assertNotEquals(0, webElement.size());
        webElement = doc.body().getElementsByClass("round-history-team-row");
        Assertions.assertNotEquals(0, webElement.size());
        // Две таблички с результатами игроков.
        webElement = doc.body().getElementsByClass("stats-table totalstats ");
        Assertions.assertEquals(2, webElement.size());
        Assertions.assertEquals(5, webElement.get(0).childNodes().size());
        Assertions.assertEquals(5, webElement.get(1).childNodes().size());
    }

    // Проверка, что история матча состоит только из элементов L и R, и длинна истории не больше 30 символов.
    @Test
    public void fullRoundHistory(String roundSequence) {
        Assertions.assertTrue(roundSequence.matches("[LR]+"));
        Assertions.assertTrue(roundSequence.length() <= 30);
    }

    // Проверка на то, что в матче были террористы.
    @Test
    public void teamIsTerrorist(Document doc) {
        Elements elem = doc.body().getElementsByClass("round-history-team-row");
        List<String> round = new ArrayList<>();
        for (Node e : elem.get(0).childNodes()) {
            round.add(e.attributes().get("src"));
        }
        for (Node e : elem.get(1).childNodes()) {
            round.add(e.attributes().get("src"));
        }
        boolean terrorist = false;
        for (String str : round) {
            if (str.contains("/t_win") || str.contains("/bomb_exploded")) {
                terrorist = true;
                break;
            }
        }
        Assertions.assertTrue(terrorist);
    }

    // Проверка игроков в матче.
    @Test
    public void allPlayers(List<PlayerOnMapResults> players) {
        Assertions.assertEquals(10, players.size());
        players.forEach(player -> {
            Assertions.assertNotEquals(null, player.playerName);
            Assertions.assertNotEquals(null, player.teamWinner);
            Assertions.assertNotEquals(null, player.team);
            // Возможно, ещё что-то стоит добавить
        });
    }

    // Проверка победившей команды.
    @Test
    public void winner(Document doc, PlayerOnMapResults player) {
        Elements score = doc.body().getElementsByClass("match-info-row");
        String winner = "right";
        if (score.get(0).childNodes().get(1).childNodes().get(0).attributes().get("class").equals("won")) {
            winner = "left";
        }
        Assertions.assertEquals(player.teamWinner, winner);
    }
}
