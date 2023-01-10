package com.gen.GeneralModule.parsers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.common.Config;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MatchesPageParser {

    @Autowired
    MatchPageParser matchPageParser;

    @Autowired
    PositiveAvailableGamesParser positiveAvailableGamesParser;

    //List<String> listOfLinks = new ArrayList<>();
    private CommonUtils commonUtils = new CommonUtils();

    public List<String> parseMatches() {
        List<String> listOfLinks = new ArrayList<>();
//        listOfLinks.clear();
//        positiveAvailableGamesParser.parseAvailableGames();
//        System.setProperty("webdriver.chrome.driver", "C:/chromedriver.exe");
//
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
//        chromeOptions.setExperimentalOption("useAutomationExtension", false);
//        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
//        WebDriver driver = new ChromeDriver(chromeOptions);
//        driver.get("https://csgopositive.be");
//        driver.manage().window().maximize();
//        commonUtils.waiter(15000);
//        driver.get("https://csgopositive.be");
//        String handle = driver.getWindowHandle();
//        driver.close();
//        commonUtils.waiter(5000);
//        driver = new ChromeDriver();
//        driver.switchTo().window(handle);
        long now = System.currentTimeMillis();
        Document doc = commonUtils.reliableConnectAndGetDocument("https://www.hltv.org/matches");
        if (doc != null) {
            Elements elementsWithHrefs = doc.body().getElementsByClass("liveMatch");
            listOfLinks.addAll(getMatchesHrefs(elementsWithHrefs));
            elementsWithHrefs = doc.body().getElementsByClass("upcomingMatch");
            listOfLinks.addAll(getMatchesHrefs(elementsWithHrefs));
        }
        System.out.println("Запрос будущих матчей: " + (System.currentTimeMillis() - now));
        System.out.println("Всего возможных матчей: " + listOfLinks.size());
        System.out.println("Берем из конфига вот столько матчей: " + Config.totalMatchesCount);
        if (listOfLinks.size() < Config.totalMatchesCount) {
            int i = 0;
        }
        listOfLinks = listOfLinks.subList(0, Math.min(listOfLinks.size(), Config.totalMatchesCount));
        return listOfLinks;
    }

    private void parseAllPlayers(List<String> listOfLinks) {
        for (String link : listOfLinks) {
            List<List<String>> players = matchPageParser.parseMatch(link);
        }
    }

    public List<List<String>> parseAllPlayersByLink(String link) {
        return matchPageParser.parseMatch(link);
    }

    private List<String> getMatchesHrefs(Elements elementsWithHrefs) {
        // Элемент в childNodes содержит две или одну ноду. Две если известны соперники и есть кнопка ставок. Одна в обратном случае.
        // нода с ссылкой на матч всегда стоит первой, но для проверки нужно смотреть на класс, который лежит в атрибутах
        // этот класс получается "match a-reset". У ставок класс "matchAnalytics"
        // В ноде с есть два атрибута в виде мапы. По ключу href находится чистая ссылка на матч
        // ссылка такого образца /matches/2356525/eternal-fire-vs-saw-esl-pro-league-season-16-conference-play-in
        List<String> links = elementsWithHrefs.stream().map(element -> {
            // Здесь двойная проверка на матч, доступный для ставок. Во-первых, у него должно быть две ноды. Во-вторых,
            // класс второй ноды должен быть "matchAnalytics".
            if (element.childNodes().get(0).attributes().get("class").equals("match a-reset") && element.childNodes().size() == 2 &&
                    element.childNodes().get(1).attributes().get("class").equals("matchAnalytics")) {
                return "https://www.hltv.org" + element.childNodes().get(0).attributes().get("href");
            } else return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return links;
    }
}
