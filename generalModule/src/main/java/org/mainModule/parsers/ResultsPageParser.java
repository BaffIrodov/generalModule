package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ResultsPageParser {

    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36";
    private int offset = 0;
    List<String> listOfLinks = new ArrayList<>();
    private ResultPageParser resultPageParser = new ResultPageParser();

    public void parseResults(int targetResultsPageCount) throws IOException {
        long now = System.currentTimeMillis();
        Document document = Jsoup.connect("https://www.hltv.org/results").userAgent(USER_AGENT).get(); //первое подключение, не требует таргета
        if (document.connection().response().statusCode() == 200) {
            listOfLinks = getAllResultsHrefs(document);
            AtomicInteger iterator = new AtomicInteger();
            listOfLinks.forEach(link -> {
                try {
                    iterator.getAndIncrement();
                    resultPageParser.parsePlayers(link, iterator.get());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("Первый запрос результатов: " + (System.currentTimeMillis() - now));
        if (targetResultsPageCount != 0) {
            for (int i = 0; i < targetResultsPageCount; i++) {
                now = System.currentTimeMillis();
                Document doc = Jsoup.connect(getNextResultsUrl()).userAgent(USER_AGENT).get();
                listOfLinks = getAllResultsHrefs(doc);
                System.out.println((i+1) + "-й запрос результатов: " + (System.currentTimeMillis() - now));
            }
        }
    }

    private List<String> getAllResultsHrefs(Document doc) {
        Elements elementsWithHrefs = doc.body().getElementsByClass("result-con");
        if (offset == 0 && (elementsWithHrefs.size() - 100) != 0) //на первой странице результатов есть матчи из текущего большого чемпионата. Они дублируются
        {
            elementsWithHrefs = skipTheseResults(elementsWithHrefs, elementsWithHrefs.size() - 100);
        }
        List<String> listOfLinks = elementsWithHrefs.stream().map(element -> {
            //элемент в childNodes содержит только одну ноду. В ноде есть два атрибута в виде мапы. По ключу href находится чистая ссылка на матч
            //ссылка такого образца /matches/2356525/eternal-fire-vs-saw-esl-pro-league-season-16-conference-play-in
            return "https://www.hltv.org" + element.childNodes().get(0).attributes().get("href");
        }).collect(Collectors.toList());
        return listOfLinks;
    }

    private Elements skipTheseResults(Elements elements, int skipOffset) {
        int i = 1;
        Elements elementsAfterSkip = new Elements();
        for (Element element : elements) {
            if (i > skipOffset) {
                elementsAfterSkip.add(element);
            }
            i++;
        }
        return elementsAfterSkip;
    }

    private String getNextResultsUrl() { //получаем адрес следующих матчей
        offset += 100;
        return "https://www.hltv.org/results?offset=" + String.valueOf(offset);
    }

}
