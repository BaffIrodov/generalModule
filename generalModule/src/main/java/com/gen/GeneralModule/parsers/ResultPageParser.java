package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.RoundHistory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
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
    @Autowired
    private StatsPageParser statsPageParser;
    public Map<List<PlayerOnMapResults>, RoundHistory> parseMapStats(String resultUrl){
        List<String> statsLinks = new ArrayList<>();
        Map<List<PlayerOnMapResults>, RoundHistory> resultMap = new HashMap<>();
        List<List<PlayerOnMapResults>> allPlayersFromResult = new ArrayList<>();
        CommonUtils.waiter(50);
        Document doc = CommonUtils.reliableConnectAndGetDocument(resultUrl);
        if (doc != null) {
            statsLinks = getAllStatsLinks(doc);
            for(String link : statsLinks){
                Map<List<PlayerOnMapResults>, RoundHistory> parsingResult = statsPageParser.parseMapStats(link);
                resultMap.putAll(parsingResult);
            }
        }
        return resultMap;
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
