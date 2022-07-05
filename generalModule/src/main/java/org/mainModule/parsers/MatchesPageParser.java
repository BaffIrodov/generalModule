package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MatchesPageParser {
    List<String> listOfLinks = new ArrayList<>();
    private final MatchPageParser matchPlayer = new MatchPageParser();

    public void parseMatches() {
        long now = System.currentTimeMillis();
        Document doc = CommonUtils.reliableConnectAndGetDocument("https://www.hltv.org/matches");
        if (doc != null) {
            Elements elementsWithHrefs = doc.body().getElementsByClass("upcomingMatch");
            listOfLinks = elementsWithHrefs.stream().map(element -> {
                //элемент в childNodes содержит две или одну ноду. Две если известны соперники и есть кнопка ставок. Одна в обратном случае.
                //нода с ссылкой на матч всегда стоит первой, но для проверки нужно смотреть на класс, который лежит в атрибутах
                //этот класс получается "match a-reset". У ставок класс "matchAnalytics"
                //В ноде с есть два атрибута в виде мапы. По ключу href находится чистая ссылка на матч
                //ссылка такого образца /matches/2356525/eternal-fire-vs-saw-esl-pro-league-season-16-conference-play-in
                if (element.childNodes().get(0).attributes().get("class").equals("match a-reset") && element.childNodes().size() == 2) {
                    return "https://www.hltv.org" + element.childNodes().get(0).attributes().get("href");
                } else return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        System.out.println("Запрос будущих матчей: " + (System.currentTimeMillis() - now));
        System.out.println("Всего возможных матчей: " + listOfLinks.size());
        parseAllPlayers(listOfLinks.subList(0, 1));
    }

    private void parseAllPlayers(List<String> listOfLinks) {
        AtomicInteger iterator = new AtomicInteger();
        listOfLinks.forEach(link -> {
            iterator.getAndIncrement();
            matchPlayer.parseMatch(link);
        });
    }
}
