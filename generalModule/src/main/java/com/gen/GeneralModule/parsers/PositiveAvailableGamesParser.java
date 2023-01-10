package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.QResultsLink;
import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.entities.RoundHistory;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PositiveAvailableGamesParser {
    /**
     *
     * В этом классе представлены методы для парсинга страницы с предстоящими матчами на КсГоПозитив. Там существуют не все игры,
     * поэтому надо на фронт отсылать информацию и про них тоже
     *
     * На странице вида (https://csgopositive.be/)
     * находится куча игр, надо смотреть csgo_event
     *
     * Кратко по логике: парсим названия команд, кэфы, название турнира и бест-оф, передаем информацию в matchesParser
     *
     */

    @Autowired
    JPAQueryFactory queryFactory;

    private CommonUtils commonUtils = new CommonUtils();

    public void parseAvailableGames(){
        Document docPositive = commonUtils.reliableConnectAndGetDocument("https://csgopositive.be/market/#");
        commonUtils.waiter(300);
        if (docPositive != null) {
            Elements csgoEvents = docPositive.body().getElementsByClass("event csgo_event");
            Elements csgoEventsHot = docPositive.body().getElementsByClass("event hot csgo_event");
            Elements csgoEventsHotLive = docPositive.body().getElementsByClass("event hot csgo_event live_betting_upcoming");
            int i = 0;
        }
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
        return commonUtils.hltvLinkTemplate(statsLinks);
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
