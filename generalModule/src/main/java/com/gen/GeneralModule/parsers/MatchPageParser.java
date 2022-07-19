package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MatchPageParser {
    public void parseMatch(String link) {
        // Периодически вываливается ошибка с подключением, потому что ограничена скорость (error 1015), из-за этого
        // не получается забрать игроков и он выводит лист с двумя пустыми листами.
        Document doc = CommonUtils.reliableConnectAndGetDocument(link);
        long now = System.currentTimeMillis();
        CommonUtils.waiter(400); // Искусственное замедление, которое позволяет зайти на все матчи. На 350 та же ошибка 1015
        List<List<String>> listPlayersLeftAndRight = getAllPlayers(doc);
        System.out.println(link);
        System.out.println("Время: " + (System.currentTimeMillis() - now));
        System.out.println("Команды: " + listPlayersLeftAndRight);
    }

    public List<List<String>> getAllPlayers(Document doc) {
        List<List<String>> players = new ArrayList<>();
        List<String> leftTeam = new ArrayList<>();
        List<String> rightTeam = new ArrayList<>();
        if (doc != null) {
            Elements elementsWithPlayers = doc.body().getElementsByClass("player player-image");
            // Элемент в childNodes чаще всего содержит 3 ноды, 0 и 2 из которых пустые, а 1 нода содержит информацию об игроке.
            // В этой ноде в атрибуте "data-player-id" находится id игрока, а в "data-team-ordinal" принадлежность к левой
            // или правой команде, здесь они обозначаются как 1 и 2.
            elementsWithPlayers.forEach(e -> {
                if (e.childNodes().size() > 1) {
                    if (e.childNodes().get(1).attributes().get("data-team-ordinal").equals("1")) {
                        leftTeam.add(e.childNodes().get(1).attributes().get("data-player-id"));
                    } else if (e.childNodes().get(1).attributes().get("data-team-ordinal").equals("2")) {
                        rightTeam.add(e.childNodes().get(1).attributes().get("data-player-id"));
                    }
                    // Бывает, что в childNodes всего одна нода, в атрибутах которой находится ссылка на страницу игрока.
                    // В таком случае id игрока приходится вырезать из ссылки. Принадлежность к команде здесь вообще отсутствует,
                    // поэтому приходится пользоваться размерностью листа. Пример подобного - "проблемный матч".
                } else {
                    if (leftTeam.size() < 5) {
                        leftTeam.add(CommonUtils.standardIdParsingBySlice("/player/", e.childNodes().get(0).attributes().get("href")));
                    } else {
                        rightTeam.add(CommonUtils.standardIdParsingBySlice("/player/", e.childNodes().get(0).attributes().get("href")));
                    }
                }
            });
        }
        players.add(leftTeam);
        players.add(rightTeam);
        return players;
    }

    public List<String> getTeamsNames(Document doc) {
        List<String> namesLeftAndRightTeams = new ArrayList<>();
        if (doc != null) {
            // Забираем названия команд из самой верхней части страницы с матчами.
            // Там названия прописаны не в атрибуте чайлднода, а являются обычной строкой.
            Elements elementsWithNames = doc.body().getElementsByClass("teamName");
            namesLeftAndRightTeams.add(((TextNode) elementsWithNames.get(0).childNodes().get(0)).getWholeText());
            namesLeftAndRightTeams.add(((TextNode) elementsWithNames.get(1).childNodes().get(0)).getWholeText());
        }
        return namesLeftAndRightTeams;
    }

    public String getMatchFormat(Document doc) {
        String format = "";
        if (doc != null) {
            Elements elementsWithFormat = doc.body().getElementsByClass("padding preformatted-text");
            format = ((TextNode) elementsWithFormat.get(0).childNodes().get(0)).getWholeText();
            format = format.replaceAll(" [(].*", "").replaceAll("\n.*", "");
        }
        return format;
    }

    public List<String> getMatchMapsNames(Document doc) {
        List<String> mapsNames = new ArrayList<>();
        if (doc != null) {
            Elements elementsWithMapsNames = doc.body().getElementsByClass("map-name-holder");
            for (Element element : elementsWithMapsNames) {
                mapsNames.add(element.childNodes().get(0).attributes().get("alt"));
            }
        }
        return mapsNames;
    }

    public List<String> getTeamsOdds(Document doc) {
        List<String> odds = new ArrayList<>();
        String leftTeam = "";
        String rightTeam = "";
        if (doc != null) {
            Elements elementsWithOdds = doc.body().getElementsByClass("odds-cell border-left");
            for(int i=0; i<(elementsWithOdds.size()-1); i+=3){
                leftTeam = String.join(" ", leftTeam,
                        ((TextNode) elementsWithOdds.get(i).childNodes().get(0).childNodes().get(0)).getWholeText());
                rightTeam = String.join(" ", rightTeam,
                        ((TextNode) elementsWithOdds.get(i+2).childNodes().get(0).childNodes().get(0)).getWholeText());
            }
            odds.add(leftTeam);
            odds.add(rightTeam);
        }
        return odds;
    }
}
