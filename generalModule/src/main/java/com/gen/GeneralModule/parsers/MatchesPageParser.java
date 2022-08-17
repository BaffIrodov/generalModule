package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MatchesPageParser {
    List<String> listOfLinks = new ArrayList<>();
    private CommonUtils commonUtils = new CommonUtils();

    public List<String> parseMatches() {
        listOfLinks.clear();
        long now = System.currentTimeMillis();
        Document doc = commonUtils.reliableConnectAndGetDocument("https://www.hltv.org/matches");
        if (doc != null) {
            Elements elementsWithHrefs = doc.body().getElementsByClass("liveMatch");
            listOfLinks.addAll(getMatchesHrefs(elementsWithHrefs));
            elementsWithHrefs = doc.body().getElementsByClass("upcomingMatch");
            listOfLinks.addAll(getMatchesHrefs(elementsWithHrefs));
            //listOfLinks.add("https://www.hltv.org/matches/2357236/onetap-vs-norway-european-championship-2022"); // Назову это "проблемный матч". Он чисто для теста
        }
        System.out.println("Запрос будущих матчей: " + (System.currentTimeMillis() - now));
        System.out.println("Всего возможных матчей: " + listOfLinks.size());
        //parseAllPlayers(listOfLinks.subList(0, 1));
        //parseAllPlayers(listOfLinks);
        return listOfLinks;
    }

//    private void parseAllPlayers(List<String> listOfLinks) {
//        listOfLinks.forEach(matchParser::parseMatch);
//    }

    private List<String> getMatchesHrefs(Elements elementsWithHrefs) {
        // Элемент в childNodes содержит две или одну ноду. Две если известны соперники и есть кнопка ставок. Одна в обратном случае.
        // нода с ссылкой на матч всегда стоит первой, но для проверки нужно смотреть на класс, который лежит в атрибутах
        // этот класс получается "match a-reset". У ставок класс "matchAnalytics"
        // В ноде с есть два атрибута в виде мапы. По ключу href находится чистая ссылка на матч
        // ссылка такого образца /matches/2356525/eternal-fire-vs-saw-esl-pro-league-season-16-conference-play-in
        List<String> links = elementsWithHrefs.stream().map(element -> {
            // Здесь двойная проверка на матч, доступный для ставок. Во-первых, у него должно быть две ноды. Во-вторых,
            // класс второй ноды должен быть "matchAnalytics".
            if (element.childNodes().get(0).attributes().get("class").equals("match a-reset") && element.childNodes().size() == 2 &&
                    element.childNodes().get(1).attributes().get("class").equals("matchAnalytics")) {
                return "https://www.hltv.org" + element.childNodes().get(0).attributes().get("href");
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return links;
    }
}
