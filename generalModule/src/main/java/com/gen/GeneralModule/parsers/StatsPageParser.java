package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.common.MapsEnum;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.RoundHistory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
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

    @Transactional
    public List<PlayerOnMapResults> parseMapStats(String statsUrl) {
        List<PlayerOnMapResults> listPlayersLeftAndRight = new ArrayList<>();
        RoundHistory roundHistoryToBD = new RoundHistory();
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis(); //#OPTIMIZATION 11.06 - фулл парсинг одной страницы занимает 180-220 мс. Хороший результат. Можно улучшить?
        Document doc = CommonUtils.reliableConnectAndGetDocument(statsUrl);
        if (doc != null) {
            Date date = getCurrentMapDate(doc);
            String idStatsMap = getStatsId(statsUrl);
            roundHistoryToBD = getFullRoundHistory(doc, idStatsMap, date);
            listPlayersLeftAndRight = getAllPlayers(doc, idStatsMap, date);
            if (roundHistoryToBD != null && listPlayersLeftAndRight != null) {
                //всё хорошо, так и должно быть, запись в БД
            } else {
                System.out.println("Валидация не прошла");
            }
        }
        return listPlayersLeftAndRight;
    }

    public RoundHistory getFullRoundHistory(Document doc, String idStatsMap, Date dateOfMatch) {
        RoundHistory result = new RoundHistory();
        List<String> notProcessedListOfRoundResults = new ArrayList<>();
        List<String> processedListOfRoundResults = new ArrayList<>();
        Elements wow = doc.body().getElementsByClass("standard-box round-history-con");
        if (wow.size() == 1) {
            Element historyElement = wow.get(0);
            getNotProcessedList(historyElement, notProcessedListOfRoundResults);
            processedListOfRoundResults = convertNotProcessedListOfRoundResults(notProcessedListOfRoundResults);
        } else if (wow.size() == 2) {
            Element historyElementMainTime = wow.get(0);
            getNotProcessedList(historyElementMainTime, notProcessedListOfRoundResults);
            processedListOfRoundResults = convertNotProcessedListOfRoundResults(notProcessedListOfRoundResults);
            Element historyElementOverTime = wow.get(1); //TODO нужно учитывать овертаймы в расчете? Они рандомные, вряд ли что-то могут сказать о типичной игре
        } else {
            //never happen (30+ rounds of overtime?)
        }
        result.idStatsMap = idStatsMap;
        result.dateOfMatch = dateOfMatch;
        // Преобразование массива с результатами в строку победителей
        StringBuilder roundResults = new StringBuilder();
        int leftScore = 0;
        for (String roundResult : processedListOfRoundResults) {
            if ((roundResult.charAt(0) - '0') > leftScore) {
                roundResults.append("L");
            } else {
                roundResults.append("R");
            }
            leftScore = roundResult.charAt(0) - '0';
        }
        result.roundSequence = roundResults.toString();
        return returnValidatedObjectOrNull(result);
    }

    private void getNotProcessedList(Element historyElement, List<String> notProcessedListOfRoundResults) {
        List<Node> usefulNodes = historyElement.childNodes().stream().filter(node -> node instanceof Element).collect(Collectors.toList());
        usefulNodes.forEach(useful -> {
            notProcessedListOfRoundResults.addAll(useful.childNodes().stream().filter
                            (e -> !e.attributes().get("title").equals(""))
                    .map(r -> r.attributes().get("title")).collect(Collectors.toList()));
        });
    }

    //в этот лист попадают названия команд и лист перемешан
    private List<String> convertNotProcessedListOfRoundResults(List<String> notProcessedListOfRoundResults) {
        Map<Integer, String> mapOrderRoundToResultRound = new HashMap<>();
        notProcessedListOfRoundResults = notProcessedListOfRoundResults.stream().filter(e -> {
            boolean isValidatedElement = true;
            List<String> dividedList = Arrays.stream(e.split("-")).collect(Collectors.toList()); //у всех нужных элементов формат "15-2"
            if (dividedList.size() == 2) {
                try {
                    Integer.parseInt(dividedList.get(0));
                    Integer.parseInt(dividedList.get(1));
                } catch (NumberFormatException exception) {
                    isValidatedElement = false;
                }
            } else {
                isValidatedElement = false;
            }
            if (isValidatedElement) {
                mapOrderRoundToResultRound.put(dividedList.stream().map(Integer::parseInt).mapToInt(Integer::intValue).sum(), e);
            }
            return isValidatedElement;
        }).collect(Collectors.toList());
        List<String> result = new ArrayList<>(mapOrderRoundToResultRound.values());
        return result;
    }

    public List<PlayerOnMapResults> getAllPlayers(Document doc, String idStatsMap, Date date) {
        List<PlayerOnMapResults> players = new ArrayList<>();
        //получаем текущую карту
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
                        currentMap, idStatsMap, date, "left");
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
                        currentMap, idStatsMap, date, "right");
                playersRight.set(iteratorRight.get(), calculatedPlayer);
            }
            iteratorRight.getAndIncrement();
        });
        players.addAll(playersLeft);
        players.addAll(playersRight);
        return returnValidatedListPlayersOrNull(players);
    }

    private PlayerOnMapResults getPlayerResultsInOneMap(PlayerOnMapResults player, List<Node> nodes,
                                                        MapsEnum currentMap, String idStatsMap, Date dateOfMatch, String team) {
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
                    player.url = CommonUtils.hltvLingTemplateOne(linkPlayer);
                    //четвертый элемент всегда id - 22218
                    player.playerId = Integer.parseInt(CommonUtils.standardIdParsingByPlace(3, linkPlayer));
                    //пятый элемент всегда name - emi
                    player.playerName = CommonUtils.standardIdParsingByPlace(4, linkPlayer);
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
        player.playedMapString = currentMap.toString();
        player.team = team;
        player.idStatsMap = idStatsMap;
        player.dateOfMatch = dateOfMatch;
        player.calculateKD();
        return player.returnValidatedObjectOrNull();
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
        return CommonUtils.standardParserDate(result.get());
    }

    private String getStatsId(String link) {
        //link всегда имеет вид - https://www.hltv.org/stats/matches/mapstatsid/139187/kappa-bar-vs-lakeshow
        //Получаются такие элементы: "https:", "", "www.hltv.org", "stats", "matches", "mapstatsid", "139187", "kappa-bar-vs-lakeshow"
        return CommonUtils.standardIdParsingByPlace(6, link);
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
