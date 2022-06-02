package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;
import org.mainModule.common.UserAgent;
import org.mainModule.entities.MapsEnum;
import org.mainModule.entities.Player;
import org.mainModule.entities.PlayerInMapResults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class StatsPageParser {


    public void parseMapStats(String statsUrl) throws IOException {
        List<Player> listPlayers = new ArrayList<>();
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(statsUrl).userAgent(UserAgent.USER_AGENT_CHROME).get();
        if (doc.connection().response().statusCode() == 200) {
            listPlayers = getAllPlayers(doc);
        }
//        System.out.print("Обработано " + iterator + " из 100 игр" +
//                "Время обработки одного матча результатов: " + (System.currentTimeMillis() - now) + "\r");
    }

    public List<Player> getAllPlayers(Document doc){
        List<Player> players = new ArrayList<>();
        //получаем текущую карту
        MapsEnum currentMap = getCurrentMapName(doc);
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
        List<Player> playersLeft = new ArrayList<>();
        for(int i = 0; i < 5; i++) {playersLeft.add(new Player());}
        List<Player> playersRight = new ArrayList<>();
        for(int i = 0; i < 5; i++) {playersRight.add(new Player());}
        AtomicInteger iterator = new AtomicInteger();
        leftTeam.stream().forEach(currentPlayer -> {
            List<Node> nodes = currentPlayer.childNodes();
            nodes = nodes.stream().filter(node -> (node.getClass().equals(Element.class) && ((Element) node).tagName().equals("td"))).collect(Collectors.toList());
            nodes.forEach(node -> {
                PlayerInMapResults currentPlayerResults = new PlayerInMapResults();
                getAttribute(playersLeft.get(iterator.get()), node, currentMap, currentPlayerResults);
            });
            int i = 0;
        iterator.getAndIncrement();
        });
        return players;
    }

    private Player getAttribute(Player player, Node node, MapsEnum currentMap, PlayerInMapResults currentPlayerResults){
        String currentClass = node.attributes().get("class");
        int ok = 0;
        switch (currentClass){
            case "st-player" -> {
                node.childNodes();
            }
            case "st-kills" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.kills = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
            }
            case "st-assists" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.assists = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
            }
            case "st-deaths" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.deaths = Integer.parseInt(node.childNodes().get(0).toString().replace(" ", ""));
            }
            case "st-kdratio" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.cast = Long.parseLong(node.childNodes().get(0).toString().replace(" ", ""));
            }
            case "st-adr" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.adr = Long.parseLong(node.childNodes().get(0).toString().replace(" ", ""));
            }
            case "st-rating" -> { //первый элемент - всегда число киллов, то же самое далее
                currentPlayerResults.power = Long.parseLong(node.childNodes().get(0).toString().replace(" ", ""));
            }
        }
        return player;
    }

    private MapsEnum getCurrentMapName(Document doc){
        AtomicReference<MapsEnum> mapsEnum = new AtomicReference<>();
        Elements mapName = doc.body().getElementsByClass("match-info-box"); //всегда один элемент должен быть
        mapName.get(0).childNodes().forEach(e -> { //в childs валяется 14 элементов, один из них - название карты
            try {
                TextNode textNode = (TextNode) e;
            } catch (Exception exception){
                String wow = "whatever";
            }
            String value = e.toString().replace(" ", "").toUpperCase();
            switch (value){
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

}
