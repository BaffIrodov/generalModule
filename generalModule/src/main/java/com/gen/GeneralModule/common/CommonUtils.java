package com.gen.GeneralModule.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//This comment
public class CommonUtils {
    public static void waiter(int timeoutInMS){
        try {
            TimeUnit.MILLISECONDS.sleep(timeoutInMS);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
    }

    public static List<String> hltvLinkTemplate(List<String> notProcessedList){
        return notProcessedList.stream().map(notProcessedLink -> {
            return "https://www.hltv.org" + notProcessedLink;
        }).collect(Collectors.toList());
    }

    public static String hltvLingTemplateOne(String notProcessedUrl){
        return "https://www.hltv.org" + notProcessedUrl;
    }

    public static Date standardParserDate(String date){ //на хлтв будут встречаться только такие даты (всегда ли?)
        Date dateFinal = new Date();
        try {
            dateFinal = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date);
        } catch (ParseException p) {
            String except = "whatever";
        }
        return dateFinal;
    }

    public static Document reliableConnectAndGetDocument(String url){ //если отваливается сервак или сеть у компа, то делаем повторный запрос
        Document doc = null;
        for(int i = 0; i < 5; i++) {
            try {
                doc = Jsoup.connect(url).userAgent(UserAgent.USER_AGENT_CHROME).get();
            } catch (IOException exception) {
                System.out.println("IOException в запросе по адресу: " + url);
            }
            if(doc != null && doc.connection().response().statusCode() == 200) {
                break;
            } else {
                System.out.println("Коннект статус код не равен 200");
                waiter(1000 * i+1); //делаем запросы с увеличивающимся таймаутом. Покрываем 15 секунд (11.06.22)
            }
        }
        return doc;
    }
}
