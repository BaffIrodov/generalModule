package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.common.MapsEnum;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.RoundHistory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class StatsPageParser {
    /**
     * В этом классе представлены методы для парсинга страницы статистики по одной карте
     * Карт в матче может быть несколько, но без разницы, в каком порядке их обрабатывать. Они, в целом, не имеют привязки к матчу
     * На странице есть несколько основных веб-элементов. Интересны два: standard-box round-history-con - результаты каждого раунда на карте,
     * две штуки stats-table - результаты каждого игрока на карте
     * Здесь отдельно парсятся эти две сущности, создаются на их основе объекты, которые пересылаются в бд. Расчет сил игроков производится в другом сервисе
     * <p>
     * Кратко по логике: смотрим на страничку, определяем дату игры и название карты,
     * парсим бокс, если результата два, то это значит, что случились овертаймы (вероятно, если овертаймы не влезут в 30 раундов, то результата будет три,
     * но это невозможно)
     * парсим две таблицы, из таблиц извлекаем информацию, маппим в экземпляры игроков, игроков сохраняем в бд (07.06.22)
     */

    private CommonUtils commonUtils = new CommonUtils();

    @Transactional
    public Map<List<PlayerOnMapResults>, RoundHistory> parseMapStats(String statsUrl) {
        List<PlayerOnMapResults> listPlayersLeftAndRight = new ArrayList<>();
        Map<List<PlayerOnMapResults>, RoundHistory> resultMap = new HashMap<>();
        RoundHistory roundHistory = new RoundHistory();
        commonUtils.waiter(300);
        Document doc = commonUtils.reliableConnectAndGetDocument(statsUrl);
        if (doc != null) {
            Date date = getCurrentMapDate(doc);
            String idStatsMap = getStatsId(statsUrl);
            roundHistory = getFullRoundHistory(doc, idStatsMap, date);
            listPlayersLeftAndRight = getAllPlayers(doc, idStatsMap, date);
        }
        resultMap.put(listPlayersLeftAndRight, roundHistory);
        return resultMap;
    }

    public RoundHistory getFullRoundHistory(Document doc, String idStatsMap, Date dateOfMatch) {
        RoundHistory result = new RoundHistory();
        String roundSequence = "";
        Elements elements = doc.body().getElementsByClass("round-history-team-row");
        List<Boolean> leftTeamRow = getRoundHistoryTeamRow(elements.get(0));
        List<Boolean> rightTeamRow = getRoundHistoryTeamRow(elements.get(1));
        Boolean leftTeamIsTerrorists = thisTeamIsTerrorists(elements.get(0));
        //в теории может произойти 15 0 и не будет понятно, кто где
        Boolean rightTeamIsTerrorists = thisTeamIsTerrorists(elements.get(1));
        for (int i = 0; i < leftTeamRow.size(); i++) {
            if (leftTeamRow.get(i) != rightTeamRow.get(i)) {
                if (leftTeamRow.get(i)) {
                    roundSequence += "L";
                } else {
                    roundSequence += "R";
                }
            } else {
                break;
            }
        }
        result.idStatsMap = Integer.parseInt(idStatsMap);
        result.dateOfMatch = dateOfMatch;
        result.roundSequence = roundSequence;
        if (leftTeamIsTerrorists) {
            result.leftTeamIsTerroristsInFirstHalf = true;
        } else if (rightTeamIsTerrorists) {
            result.leftTeamIsTerroristsInFirstHalf = false;
        }
        return returnValidatedObjectOrNull(result);
    }

    private Boolean thisTeamIsTerrorists(Element historyRow) {
        Boolean result = false;
        int index = 0;
        for (Node e : historyRow.childNodes()) {
            index++;
            // Проверка до 21 элемента, потому что история матча начинается только с 5-ого элемента
            if (index < 21 && (e.attributes().get("src").contains("/t_win")
                    || e.attributes().get("src").contains("/bomb_exploded"))) {
                result = true;
                break;
            } else if (index >= 21) {
                break;
            }
        }
        return result;
    }

    private List<Boolean> getRoundHistoryTeamRow(Element historyRow) {
        List<Boolean> teamRow = new ArrayList<>();
        historyRow.childNodes().forEach(e -> {
            if (e.attributes().get("class").equals("round-history-outcome")) {
                if (e.attributes().get("title").equals("")) {
                    teamRow.add(false);
                } else {
                    teamRow.add(true);
                }
            }
        });
        return teamRow;
    }

    public List<PlayerOnMapResults> getAllPlayers(Document doc, String idStatsMap, Date date) {
        List<PlayerOnMapResults> players = new ArrayList<>();
        //получаем текущую карту
        String winnerTeam = getWinner(doc);
        MapsEnum currentMap = getCurrentMapName(doc);
        //получаем все элементы, принадлежащие таблицам с игроками и их результатами
        Elements table = doc.body().getElementsByClass("stats-table totalstats ");
        //всего таблицы две - одна сверху, вторая ниже. В каждой находятся по 5 человек из каждой команды
        //далее получаем строчки, принадлежащие команде
        List<List<Node>> teamElement = table.stream().map(
                e -> e.childNodes().stream().filter(r -> (
                        r.getClass().equals(Element.class) && ((Element) r).tagName().equals("tbody")
                )).collect(Collectors.toList())
        ).toList();
        //строчки преобразуются в лист из элементов, принадлежащих игрокам. Здесь есть один лишний уровень вложенности листов
        List<List<List<Node>>> teams = teamElement.stream().map(
                t -> t.stream().map(e -> e.childNodes().stream().filter(q -> (
                                q.getClass().equals(Element.class) && ((Element) q).tagName().equals("tr")
                        )).collect(Collectors.toList())
                ).collect(Collectors.toList())).toList();
        //отрезаем лишний уровень
        List<List<Node>> teamsConverted = teams.stream().flatMap(List::stream).toList();
        //получаем список элементов, которые принадлежат игрокам каждой команды
        List<Node> leftTeam = teamsConverted.get(0);
        List<Node> rightTeam = teamsConverted.get(1);
        //инициализация команд
        List<PlayerOnMapResults> playersLeft = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playersLeft.add(new PlayerOnMapResults());
        }
        List<PlayerOnMapResults> playersRight = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playersRight.add(new PlayerOnMapResults());
        }
        AtomicInteger iteratorLeft = new AtomicInteger();
        leftTeam.forEach(currentPlayer -> {
            //получаем все ноды каждого игрока, нод будет 19, но в них входят textElement`ы, их надо отфильтровать
            List<Node> nodes = currentPlayer.childNodes();
            //фильтрация нод - нам нужны только элемент-классы с тэгом
            nodes = nodes.stream().filter(node -> (node.getClass().equals(Element.class) && ((Element) node).tagName().equals("td"))).collect(Collectors.toList());
            //на случай замены в игре - отследить достоверно, кого точно заменили нельзя, поэтому просто отрезаем последнего
            if (iteratorLeft.intValue() < 5) {
                PlayerOnMapResults calculatedPlayer = getPlayerResultsInOneMap(playersLeft.get(iteratorLeft.get()), nodes,
                        currentMap, idStatsMap, date, winnerTeam, "left");
                playersLeft.set(iteratorLeft.get(), calculatedPlayer);
            }
            iteratorLeft.getAndIncrement();
        });
        AtomicInteger iteratorRight = new AtomicInteger();
        rightTeam.forEach(currentPlayer -> {
            //получаем все ноды каждого игрока, нод будет 19, но в них входят textElement`ы, их надо отфильтровать
            List<Node> nodes = currentPlayer.childNodes();
            //фильтрация нод - нам нужны только элемент-классы с тэгом
            nodes = nodes.stream().filter(node -> (node.getClass().equals(Element.class) && ((Element) node).tagName().equals("td"))).collect(Collectors.toList());
            //на случай замены в игре - отследить достоверно, кого точно заменили нельзя, поэтому просто отрезаем последнего
            if (iteratorRight.intValue() < 5) {
                PlayerOnMapResults calculatedPlayer = getPlayerResultsInOneMap(playersRight.get(iteratorRight.get()), nodes,
                        currentMap, idStatsMap, date, winnerTeam, "right");
                playersRight.set(iteratorRight.get(), calculatedPlayer);
            }
            iteratorRight.getAndIncrement();
        });
        players.addAll(playersLeft);
        players.addAll(playersRight);
        return returnValidatedListPlayersOrNull(players);
    }

    private PlayerOnMapResults getPlayerResultsInOneMap(PlayerOnMapResults player, List<Node> nodes,
                                                        MapsEnum currentMap, String idStatsMap, Date dateOfMatch,
                                                        String winnerTeam, String team) {
        nodes.forEach(node -> {
            String currentClass = node.attributes().get("class");
            switch (currentClass) {
                case "st-player" -> {
                    Node nodeWithPlayerInfo = node.childNodes().stream().filter(n -> (n.getClass().equals(Element.class) && ((Element) n).tagName().equals("div"))).collect(Collectors.toList()).get(0);
                    Node nodePlayer = nodeWithPlayerInfo.childNodes().stream().filter(e -> {
                        return !(e.attributes().get("href").equals(""));
                    }).toList().get(0);
                    String linkPlayer = nodePlayer.attributes().get("href");
                    //url в формате /stats/players/22218/emi
                    player.url = commonUtils.hltvLingTemplateOne(linkPlayer);
                    //четвертый элемент всегда id - 22218
                    player.playerId = Integer.parseInt(commonUtils.standardIdParsingByPlace(3, linkPlayer));
                    //пятый элемент всегда name - emi
                    player.playerName = commonUtils.standardIdParsingByPlace(4, linkPlayer);
                    ;
                    int i = 0;
                }
                case "st-kills" -> { //первый элемент - всегда число киллов,
                    // второй элемент - нода, у которой в детях хедшоты в формате " (8)", поэтому регуляркой ищем пробел, левую и правую скобки
                    player.kills = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
                    player.headshots = Integer.parseInt(node.childNodes().get(1).childNodes().get(0).toString().replaceAll("[ |(|)]", ""));
                }
                case "st-assists" -> { //первый (и единственный) элемент - ассисты, далее - точно так же
                    player.assists = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
                }
                case "st-deaths" -> {
                    player.deaths = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
                }
                case "st-kdratio" -> {
                    if (node.childNodes().get(0).toString().equals("-")) {
                        player.cast20 = 0;
                    } else {
                        player.cast20 = Float.parseFloat(node.childNodes().get(0).toString().replaceAll("[%| ]", ""));
                    }
                }
                case "st-adr" -> {
                    if (node.childNodes().get(0).toString().equals("-")) {
                        player.adr = 0;
                    } else {
                        player.adr = Float.parseFloat(node.childNodes().get(0).toString().replace(" ", ""));
                    }
                }
                case "st-rating" -> {
                    player.rating20 = Float.parseFloat(node.childNodes().get(0).toString().replace(" ", ""));
                }
            }
        });
        player.playedMap = currentMap;
        player.teamWinner = winnerTeam;
        player.playedMapString = currentMap.toString();
        player.team = team;
        player.idStatsMap = Integer.parseInt(idStatsMap);
        player.dateOfMatch = dateOfMatch;
        player.calculateKD();
        return player.returnValidatedObjectOrNull();
    }

    private String getWinner(Document doc) {
        AtomicReference<String> result = new AtomicReference<>("");
        Elements mapInfoBox = doc.body().getElementsByClass("match-info-box"); //всегда один элемент должен быть
        mapInfoBox.get(0).childNodes().forEach(e -> {
            if (e.attributes().get("class").toString().equals("team-left")) {
                e.childNodes().forEach(r -> {
                    if (r.toString().contains("bold won")) {
                        result.set("left");
                    } else if (r.toString().contains("bold lost")) {
                        result.set("right");
                    }
                });
            }
        });
        return result.get();
    }

    private MapsEnum getCurrentMapName(Document doc) {
        AtomicReference<MapsEnum> mapsEnum = new AtomicReference<>();
        Elements mapInfoBox = doc.body().getElementsByClass("match-info-box"); //всегда один элемент должен быть
        mapInfoBox.get(0).childNodes().forEach(e -> { //в childs валяется 14 элементов, один из них - название карты
            //получаем название карты брутфорсом
            String value = e.toString().replace(" ", "").toUpperCase();
            switch (value) {
                case "DUST2" -> mapsEnum.set(MapsEnum.DUST2);
                case "MIRAGE" -> mapsEnum.set(MapsEnum.MIRAGE);
                case "INFERNO" -> mapsEnum.set(MapsEnum.INFERNO);
                case "NUKE" -> mapsEnum.set(MapsEnum.NUKE);
                case "OVERPASS" -> mapsEnum.set(MapsEnum.OVERPASS);
                case "VERTIGO" -> mapsEnum.set(MapsEnum.VERTIGO);
                case "ANCIENT" -> mapsEnum.set(MapsEnum.ANCIENT);
                case "CACHE" -> mapsEnum.set(MapsEnum.CACHE);
                case "TRAIN" -> mapsEnum.set(MapsEnum.TRAIN);
                case "TUSCAN" -> mapsEnum.set(MapsEnum.TUSCAN);
            }
        });
        return mapsEnum.get();
    }

    private Date getCurrentMapDate(Document doc) {
        AtomicReference<String> result = new AtomicReference<>("");
        Elements mapInfoBox = doc.body().getElementsByClass("match-info-box"); //всегда один элемент должен быть
        mapInfoBox.get(0).childNodes().forEach(e -> { //в childs валяется 14 элементов, один из них - название карты
            if (e.attributes().get("class").equals("small-text")) {
                Node dateNode = e.childNodes().stream().filter(r -> !r.attributes().get("data-unix").equals("")).collect(Collectors.toList()).get(0);
                result.set(dateNode.childNodes().get(0).toString());
            }
        });
        return commonUtils.standardParserDate(result.get());
    }

    private String getStatsId(String link) {
        //link всегда имеет вид - https://www.hltv.org/stats/matches/mapstatsid/139187/kappa-bar-vs-lakeshow
        //Получаются такие элементы: "https:", "", "www.hltv.org", "stats", "matches", "mapstatsid", "139187", "kappa-bar-vs-lakeshow"
        return commonUtils.standardIdParsingByPlace(6, link);
    }

    private List<PlayerOnMapResults> returnValidatedListPlayersOrNull(List<PlayerOnMapResults> players) {
        boolean alright = true;
        for (PlayerOnMapResults player : players) {
            if (player == null) {
                alright = false;
                break;
            }
        }
        if (alright) {
            return players;
        } else {
            return null;
        }
    }

    public boolean validateThisObject(RoundHistory result) {
        return !result.idStatsMap.equals("") &&
                result.dateOfMatch != null &&
                result.roundSequence.length() > 0;
    }

    public RoundHistory returnValidatedObjectOrNull(RoundHistory result) {
        if (validateThisObject(result)) {
            return result;
        } else {
            return null;
        }
    }

}
