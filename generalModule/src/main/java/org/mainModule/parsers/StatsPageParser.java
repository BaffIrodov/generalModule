package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;
import org.mainModule.common.UserAgent;
import org.mainModule.entities.MapsEnum;
import org.mainModule.entities.PlayerInMapResultsToBD;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StatsPageParser {


    public void parseMapStats(String statsUrl) throws IOException {
        List<List<PlayerInMapResultsToBD>> listPlayersLeftAndRight = new ArrayList<>();
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(statsUrl).userAgent(UserAgent.USER_AGENT_CHROME).get();
        if (doc.connection().response().statusCode() == 200) {
            listPlayersLeftAndRight = getAllPlayers(doc);
        }
        System.out.println((System.currentTimeMillis() - now));
        int i = 0;
    }

    public List<List<PlayerInMapResultsToBD>> getAllPlayers(Document doc) {
        List<List<PlayerInMapResultsToBD>> players = new ArrayList<>();
        //получаем текущую карту
        MapsEnum currentMap = getCurrentMapName(doc);
        String date = getCurrentMapDate(doc);
        //получаем все элементы, принадлежащие таблицам с игроками и их результатами
        Elements table = doc.body().getElementsByClass("stats-table");
        Elements maps = doc.body().getElementsByClass("st-player");
        //всего таблицы две - одна сверху, вторая ниже. В каждой находятся по 5 человек из каждой команды
        //далее получаем строчки, принадлежащие команде
        List<List<Node>> teamElement = table.stream().map(
                e -> e.childNodes().stream().filter(r -> (r.getClass().equals(Element.class) && ((Element) r).tagName().equals("tbody"))).collect(Collectors.toList())
        ).collect(Collectors.toList());
        //строчки преобразуются в лист из элементов, принадлежащих игрокам. Здесь есть один лишний уровень вложенности листов
        List<List<List<Node>>> teams = teamElement.stream().map(
                t -> t.stream().map(e -> e.childNodes().stream().filter(q -> (q.getClass().equals(Element.class) && ((Element) q).tagName().equals("tr"))).collect(Collectors.toList())
                ).collect(Collectors.toList())).collect(Collectors.toList());
        //отрезаем лишний уровень
        List<List<Node>> teamsConverted = teams.stream().flatMap(List::stream).collect(Collectors.toList());
        //получаем список элементов, которые принадлежат игрокам каждой команды
        List<Node> leftTeam = teamsConverted.get(0);
        List<Node> rightTeam = teamsConverted.get(1);
        //инициализация команд
        List<PlayerInMapResultsToBD> playersLeft = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playersLeft.add(new PlayerInMapResultsToBD());
        }
        List<PlayerInMapResultsToBD> playersRight = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            playersRight.add(new PlayerInMapResultsToBD());
        }
        AtomicInteger iteratorLeft = new AtomicInteger();
        leftTeam.stream().forEach(currentPlayer -> {
            //получаем все ноды каждого игрока, нод будет 19, но в них входят textElement`ы, их надо отфильтровать
            List<Node> nodes = currentPlayer.childNodes();
            //фильтрация нод - нам нужны только элемент-классы с тэгом
            nodes = nodes.stream().filter(node -> (node.getClass().equals(Element.class) && ((Element) node).tagName().equals("td"))).collect(Collectors.toList());
            PlayerInMapResultsToBD calculatedPlayer = getPlayerResultsInOneMap(playersLeft.get(iteratorLeft.get()), nodes, currentMap, date);
            playersLeft.set(iteratorLeft.get(), calculatedPlayer);
            iteratorLeft.getAndIncrement();
        });
        AtomicInteger iteratorRight = new AtomicInteger();
        rightTeam.stream().forEach(currentPlayer -> {
            //получаем все ноды каждого игрока, нод будет 19, но в них входят textElement`ы, их надо отфильтровать
            List<Node> nodes = currentPlayer.childNodes();
            //фильтрация нод - нам нужны только элемент-классы с тэгом
            nodes = nodes.stream().filter(node -> (node.getClass().equals(Element.class) && ((Element) node).tagName().equals("td"))).collect(Collectors.toList());
            PlayerInMapResultsToBD calculatedPlayer = getPlayerResultsInOneMap(playersRight.get(iteratorRight.get()), nodes, currentMap, date);
            playersRight.set(iteratorRight.get(), calculatedPlayer);
            iteratorRight.getAndIncrement();
        });
        players.add(playersLeft);
        players.add(playersRight);
        return players;
    }

    private PlayerInMapResultsToBD getPlayerResultsInOneMap(PlayerInMapResultsToBD player, List<Node> nodes, MapsEnum currentMap, String date) {
        nodes.forEach(node -> {
            String currentClass = node.attributes().get("class");
            int ok = 0;
            switch (currentClass) {
                case "st-player" -> {
                    Node nodeWithPlayerInfo = node.childNodes().stream().filter(n -> (n.getClass().equals(Element.class) && ((Element) n).tagName().equals("div"))).collect(Collectors.toList()).get(0);
                    Node nodePlayer = nodeWithPlayerInfo.childNodes().stream().filter(e -> {
                        return !(e.attributes().get("href").equals(""));
                    }).collect(Collectors.toList()).get(0);
                    String linkPlayer = nodePlayer.attributes().get("href");
                    //url в формате /stats/players/22218/emi
                    player.url = CommonUtils.hltvLingTemplateOne(linkPlayer);
                    List<String> splitedLink = Arrays.stream(linkPlayer.split("/")).collect(Collectors.toList());
                    //четвертый элемент всегда id - 22218
                    player.id = splitedLink.get(3);
                    //пятый элемент всегда name - emi
                    player.name = splitedLink.get(4);
                    int i = 0;
                }
                case "st-kills" -> { //первый элемент - всегда число киллов,
                    // второй элемент - нода, у которой в детях хедшоты в формате " (8)"
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
                    player.cast = Float.parseFloat(node.childNodes().get(0).toString().replaceAll("[%| ]", ""));
                }
                case "st-adr" -> {
                    player.adr = Float.parseFloat(node.childNodes().get(0).toString().replace(" ", ""));
                }
                case "st-rating" -> {
                    player.rating20 = Float.parseFloat(node.childNodes().get(0).toString().replace(" ", ""));
                }
            }
        });
        player.map = currentMap;
        Date dateFinal = new Date();
        try {
            dateFinal = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date);
        } catch (ParseException p) {
            String except = "whatever";
        }
        player.dateOfMatch = dateFinal;
        player.calculateKD();
        return player;
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

    private String getCurrentMapDate(Document doc) {
        AtomicReference<String> result = new AtomicReference<>("");
        Elements mapInfoBox = doc.body().getElementsByClass("match-info-box"); //всегда один элемент должен быть
        mapInfoBox.get(0).childNodes().forEach(e -> { //в childs валяется 14 элементов, один из них - название карты
            if(e.attributes().get("class").equals("small-text")){
                Node dateNode = e.childNodes().stream().filter(r -> !r.attributes().get("data-unix").equals("")).collect(Collectors.toList()).get(0);
                result.set(dateNode.childNodes().get(0).toString());
            }
        });
        return result.get();
    }

}
