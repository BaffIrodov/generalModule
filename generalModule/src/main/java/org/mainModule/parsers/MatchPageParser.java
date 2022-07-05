package org.mainModule.parsers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mainModule.common.CommonUtils;
import org.mainModule.entities.Player;
import org.mainModule.entities.PlayerOnMapResultsToBD;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchPageParser {

    public void parseMatch(String link) {
        Document doc = CommonUtils.reliableConnectAndGetDocument(link);
        List<Player> listPlayersLeftAndRight = new ArrayList<>();
        listPlayersLeftAndRight = getAllPlayers(doc);

    }

    public List<Player> getAllPlayers(Document doc) {
        List<Player> players = new ArrayList<>();
        List<String> playerId = new ArrayList<>();
        List<String> playerName = new ArrayList<>();
        List<String> playerLink = new ArrayList<>();
        if (doc != null) {
            Elements elementsWithPlayers = doc.body().getElementsByClass("player player-image");
            playerId = elementsWithPlayers.stream().map(e -> e.childNodes().get(1).attributes().get("data-player-id")).collect(Collectors.toList());
            playerName = elementsWithPlayers.stream().map(e -> e.childNodes().get(1).childNodes().get(0).attributes().get("alt")).collect(Collectors.toList());
            //pls = elementsWithPlayers.stream().map()
            System.out.println("");
        }
        return players;
    }
}
