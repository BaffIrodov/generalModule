package org.mainModule.parsers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ResultsPageParser {

    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36";

    public void getHtml() throws IOException {
        Document document = Jsoup.connect("https://www.hltv.org/results").userAgent(USER_AGENT).get();
        System.out.print(document.body().toString());
    }

//    Response response= Jsoup.connect(location)
//            .ignoreContentType(true)
//            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
//            .referrer("http://www.google.com")
//            .timeout(12000)
//            .followRedirects(true)
//            .execute();
//
//    Document doc = response.parse();

}
