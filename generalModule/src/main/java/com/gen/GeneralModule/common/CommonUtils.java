package com.gen.GeneralModule.common;

import com.gen.GeneralModule.services.ErrorsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
//This comment
public class CommonUtils {
    static ErrorsService errorsService = new ErrorsService();

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
        UserAgent userAgent = new UserAgent();
        for(int i = 0; i < 7; i++) {
            try {
                System.setProperty("http.proxyHost", getRandomProxyHost());
                System.setProperty("http.proxyPort", getRandomProxyPort());
                doc = Jsoup.connect(url).userAgent(userAgent.getUserAgentChrome()).get();
            } catch (IOException exception) {
                errorsService.saveError(exception, url);
                System.out.println("IOException в запросе по адресу: " + url);
            }
            if(doc != null && doc.connection().response().statusCode() == 200) {
                break;
            } else {
                System.out.println("Коннект статус код не равен 200");
                waiter(1000 * i+1); //делаем запросы с увеличивающимся таймаутом. Покрываем 15 секунд (11.06.22)
                if(i >= 4) {
                    System.out.println("Врубаю долгое ожидание");
                    waiter(120 * 1000); //на две минуты передышка, бан должен пройти
                }
            }
        }
        return doc;
    }

    private static String getRandomProxyHost() {
        Random random = new Random();
        List<Integer> randomNumbers = new ArrayList<>();
        for(int i = 0; i < 4; i++) randomNumbers.add(random.nextInt(1,255));
        List<String> randomNumbersToString = new ArrayList<>();
        for(Integer number: randomNumbers) {
            randomNumbersToString.add(number.toString());
        }
        return String.join(".", randomNumbersToString);
    }

    private static String getRandomProxyPort() {
        Random random = new Random();
        Integer res = random.nextInt(1000, 2000);
        return res.toString();
    }

        public static String standardIdParsingBySlice(String strBeforeId, String processedString) {
        return (processedString.replaceAll(".*" + strBeforeId, "").replaceAll("/.*", ""));
    }

    public static String standardIdParsingByPlace(Integer idPosition, String processedString) {
        String[] splittedString = processedString.split("/");
        return splittedString[idPosition];
    }
}
