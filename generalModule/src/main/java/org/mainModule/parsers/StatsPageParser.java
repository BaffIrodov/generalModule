package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;
import org.mainModule.common.UserAgent;
import org.mainModule.entities.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatsPageParser {


    public void parseMapStats(String statsUrl) throws IOException {
        List<Player> statsLinks = new ArrayList<>();
        CommonUtils.waiter(300);
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(statsUrl).userAgent(UserAgent.USER_AGENT_CHROME).get();
        if (doc.connection().response().statusCode() == 200) {
            statsLinks = getAllPlayers(doc);
        }
//        System.out.print("Обработано " + iterator + " из 100 игр" +
//                "Время обработки одного матча результатов: " + (System.currentTimeMillis() - now) + "\r");
    }

    public List<Player> getAllPlayers(Document doc){
        List<Player> players = new ArrayList<>();
        Elements maps = doc.body().getElementsByClass("st-player");
//        List<String> statsLinks = maps.stream().map(e -> e.childNodes().stream().map
//                (r -> r.attributes().get("href")).findFirst().orElse(null)).collect(Collectors.toList());
        return players;
    }

}
