package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ResultPageParser {
    public int timeout = 1;
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36";
    public void parsePlayers(String resultUrl, int iterator) throws IOException {
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(resultUrl).userAgent(USER_AGENT).get();
        if (doc.connection().response().statusCode() == 200) {
            getAllPlayers(doc, iterator, now);
            getAllMaps(doc, iterator, now);
        }
        System.out.print("Обработано " + iterator + " из 100 игр" +
                "Время обработки одного матча результатов: " + (System.currentTimeMillis() - now) + "\r");
    }

    public void getAllPlayers(Document doc, int iterator, long now){
        Elements players = doc.body().getElementsByClass("player");
        List<Element> playersAsList = players.stream().filter(e -> {
            return e.attributes().get("class").equals("player");
        }).collect(Collectors.toList());
    }

    public void getAllMaps(Document doc, int iterator, long now){
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
}
