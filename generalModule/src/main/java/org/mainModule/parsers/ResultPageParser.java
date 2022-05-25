package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ResultPageParser {
    public int timeout = 1;
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36";
    public void parsePlayers(String resultUrl, int iterator) throws IOException {
        long now = System.currentTimeMillis();
        Document doc = Jsoup.connect(resultUrl).userAgent(USER_AGENT).get();
        if (doc.connection().response().statusCode() == 200) {
            getAllPlayers(doc, iterator, now);
        }
    }

    public void getAllPlayers(Document doc, int iterator, long now){
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        Elements players = doc.body().getElementsByClass("player");
        List<Element> playersAsList = players.stream().filter(e -> {
            return e.attributes().get("class").equals("player");
        }).collect(Collectors.toList());
        System.out.print("Обработано " + iterator + " из 100 игр" +
                "Время обработки одного матча результатов: " + (System.currentTimeMillis() - now - (timeout* 1000L)) + "\r");
    }
}
