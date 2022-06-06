package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;
import org.mainModule.common.UserAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResultPageParser {
    /**
     *
     * В этом классе представлены методы для парсинга страницы с результатами матча. В матче может содержаться одна или несколько карт,
     * потому здесь нет широкого функционала.
     * На странице вида (https://www.hltv.org/matches/2356569/pain-vs-godsent-esl-challenger-valencia-2022-north-america-closed-qualifier)
     * находится три строки (или 1, 2, 5) с сыгранными картами. Под каждой картой есть кнопка stats. Она содержит ссылку на результаты розыгрыша карты
     *
     * Кратко по логике: берем ссылки на статсы, отслеживая, чтобы сама игра существовала (не тех. победа), затем проваливаемся в StatsPageParser
     *
     */
    private final StatsPageParser statsPageParser = new StatsPageParser();
    public void parseMapStats(String resultUrl, int iterator) throws IOException {
        List<String> statsLinks = new ArrayList<>();
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(resultUrl).userAgent(UserAgent.USER_AGENT_CHROME).get();
        if (doc.connection().response().statusCode() == 200) {
            statsLinks = getAllStatsLinks(doc);
            for(String link : statsLinks){
                statsPageParser.parseMapStats(link);
            }
        }
        System.out.print("Обработано " + iterator + " из 100 игр" +
                "Время обработки одного матча результатов: " + (System.currentTimeMillis() - now) + "\r");
    }

    //получаем ссылки на все странички, на которых приведена полная детализация по карте
    public List<String> getAllStatsLinks(Document doc){
        Elements maps = doc.body().getElementsByClass("results-center-stats");
        List<String> statsLinks = maps.stream().map(e -> e.childNodes().stream().map
                (r -> r.attributes().get("href")).findFirst().orElse(null)).collect(Collectors.toList());
        //------
        // В следующей строчке выкидываются ситуации, когда команда выиграла технической победой или имела фору в одну карту (все карты default)
        //------
        statsLinks = statsLinks.stream().filter(Objects::nonNull).collect(Collectors.toList());
        // Если лист нулевой - то дальше парсинг и не пойдет - в статсы
        return CommonUtils.hltvLinkTemplate(statsLinks);
    }

    //бесполезно
    //получаем все названия карт по очередности
    public void getAllMapNames(Document doc){
        Elements maps = doc.body().getElementsByClass("stats-menu-link");
        List<String> mapNamesUnprocessed = maps.stream().map(e -> {
            List<Node> nodesWithMapName = e.childNodes().stream().filter(r -> {
                return (r.childNodes().size() == 1);
            }).collect(Collectors.toList());
            return nodesWithMapName.stream().map(r -> {
                Node ok = r.childNodes().get(0);
                return ok.toString().replace("\n", "");
            }).findFirst().orElse("");
        }).collect(Collectors.toList());
    }

    //бесполезно
    //получаю элементы со всеми игроками, но нет детализации по самим раундам
    public void getAllPlayers(Document doc){
        Elements players = doc.body().getElementsByClass("player");
        List<Element> playersAsList = players.stream().filter(e -> {
            return e.attributes().get("class").equals("player");
        }).collect(Collectors.toList());
    }
}
