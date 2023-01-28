package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MatchPageParser {
    @Autowired
    private CommonUtils commonUtils;
    public List<List<String>> parseMatch(String link) {
        // Периодически вываливается ошибка с подключением, потому что ограничена скорость (error 1015), из-за этого
        // не получается забрать игроков и он выводит лист с двумя пустыми листами.
        Document doc = commonUtils.reliableConnectAndGetDocument(link);
        long now = System.currentTimeMillis();
        commonUtils.waiter(400); // Искусственное замедление, которое позволяет зайти на все матчи. На 350 та же ошибка 1015
        List<List<String>> listPlayersLeftAndRight = getAllPlayers(doc);
        return listPlayersLeftAndRight;
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
            // Так же, как и в случае с названиями команд формат матча записан обычной строкой в отдельном
            // блоке на сайте. Поэтому добираемся до строки и вырезаем ей с помощью регулярного выражения,
            // так как в этом блоке есть и другая информация.
            Elements elementsWithFormat = doc.body().getElementsByClass("padding preformatted-text");
            format = ((TextNode) elementsWithFormat.get(0).childNodes().get(0)).getWholeText();
            format = format.replaceAll(" [(].*", "").replaceAll("\n.*", "");
        }
        return format;
    }

    public String getMatchDate(Document doc) {
        String res = "";
        AtomicReference<String> time = new AtomicReference<>("");
        AtomicReference<String> date = new AtomicReference<>("");
        if (doc != null) {
            Elements elementsWithDate = doc.body().getElementsByClass("timeAndEvent");
            elementsWithDate.get(0).childNodes().forEach(e -> {
                if (e instanceof Element) {
                    Element thisEl = (Element) e;
                    if(thisEl.className().equals("time")) {
                        time.set(e.childNodes().get(0).toString().replaceAll("\n", ""));
                    }
                    if(thisEl.className().equals("date")) {
                        date.set(e.childNodes().get(0).toString().replaceAll("\n", ""));
                    }
                }
            });
        }
        res = time.get() + " | " + date.get();
        return res;
    }

    public List<String> getMatchMapsNames(Document doc) {
        List<String> mapsNames = new ArrayList<>();
        if (doc != null) {
            // Названия карт располагаются в элементах с одинаковыми классами, поэтому мы перебираем их в цикле.
            // Сами названия находятся в 0 чайлдноде в атрибуте по имени alt.
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
            // Коэффициенты располагаются на сайте в табличке с тремя столбцами. Первый столбец - левая команда,
            // второй столбец - всегда пустой, третий столбец - правая команда. Так как ячейки таблицы с
            // одинаковыми классами, то мы берём все ячейки и шагаем по ним в цикле. Берём коэф для левой команды
            // и засовываем в левый список, следующую пустую ячейку пропускаем, берём коэф для правой команды
            // и засовываем в правый список. Так делаем пока не пройдём по всем ячейкам с классом odds-cell border-left.
            // Затем в общий список добавляем в начале левый, а потом правый списки.
//            Elements elementsWithOdds = doc.body().getElementsByClass("odds-cell border-left");
//            for(int i=0; i<(elementsWithOdds.size()-1); i+=3){
//                leftTeam = String.join(" ", leftTeam,
//                        ((TextNode) elementsWithOdds.get(i).childNodes().get(0).childNodes().get(0)).getWholeText());
//                rightTeam = String.join(" ", rightTeam,
//                        ((TextNode) elementsWithOdds.get(i+2).childNodes().get(0).childNodes().get(0)).getWholeText());
//            }
//            odds.add(leftTeam);
//            odds.add(rightTeam);
        }
        odds.add(leftTeam);
        odds.add(rightTeam);
        return odds;
    }
}
