package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.common.Config;
import com.gen.GeneralModule.common.MapsEnum;
import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.dtos.MatchesWithTimeDto;
import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.entities.QPlayerForce;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.services.MatchesParserService;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/matches")
public class MatchesController {
    @Autowired
    private MatchesParserService matchesParserService;

    @Autowired
    private MatchesPageParser matchesPageParser;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private MatchPageParser matchPageParser;

    @Autowired
    private JPAQueryFactory queryFactory; //TODO вот это надо бы в сервис

    private static final QPlayerForce playerForce = new QPlayerForce("playerForce");

    // В данный момент не используется. Будет ли?
    @GetMapping("/write-links")
    public MatchesWithTimeDto writeAllLinks() {
        matchesParserService.deleteAll();
        long fullTime = System.currentTimeMillis();
        List<String> allLinks = matchesPageParser.parseMatches();
        //allLinks = allLinks.subList(0, 20);
        List<MatchesLink> matchesLinks = new ArrayList<>();
        List<MatchesDto> listMatchesDto = new ArrayList<>();
        MatchesWithTimeDto matchesWithTimeDto = new MatchesWithTimeDto();
        allLinks.forEach(link -> {
            // Искусственное замедление
            commonUtils.waiter(400);
            long now = System.currentTimeMillis();
            MatchesLink matchesLink = new MatchesLink();
            List<List<String>> teams = matchPageParser.parseMatch(link);
            matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
            matchesLink.matchUrl = link;
            Document doc = commonUtils.reliableConnectAndGetDocument(link);
            List<String> teamNames = matchPageParser.getTeamsNames(doc);
            matchesLink.leftTeam = teamNames.get(0);
            matchesLink.rightTeam = teamNames.get(1);
            matchesLink.matchFormat = matchPageParser.getMatchFormat(doc);
            String matchDate = matchPageParser.getMatchDate(doc);
            List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
            matchesLink.matchMapsNames = String.join("\n", mapNames);
            List<String> teamOdds = matchPageParser.getTeamsOdds(doc);
            matchesLink.leftTeamOdds = teamOdds.get(0);
            matchesLink.rightTeamOdds = teamOdds.get(1);
            matchesLinks.add(matchesLink);
            matchesLink.matchTime = (int) (System.currentTimeMillis() - now);
            matchesParserService.save(matchesLink);

            MatchesDto matchesDto = constructDto(matchDate, matchesLink, mapNames, teams.get(0), teams.get(1));
            if (matchesDto.mapsPredict.size() != 0) {
                int i = 0;
            }
            listMatchesDto.add(matchesDto);
            //System.out.println("Обработано " + matchesDto.size() + " из " + allLinks.size());
        });
        //matchesParserService.saveAll(matchesLinks);
        matchesWithTimeDto.matches = listMatchesDto;
        matchesWithTimeDto.fullTime = (int) (System.currentTimeMillis() - fullTime);
        System.out.println("Полное время: " + matchesWithTimeDto.fullTime);
        return matchesWithTimeDto;
    }

    @PostMapping("/write-one-match")
    public MatchesDto writeOneMatch(@RequestBody String link) {
        MatchesLink matchesLink = new MatchesLink();
        long now = System.currentTimeMillis();
        List<List<String>> teams = matchPageParser.parseMatch(link);
        matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
        matchesLink.matchUrl = link;
        Document doc = commonUtils.reliableConnectAndGetDocument(link);
        List<String> teamsNames = matchPageParser.getTeamsNames(doc);
        matchesLink.leftTeam = teamsNames.get(0);
        matchesLink.rightTeam = teamsNames.get(1);
        matchesLink.matchFormat = matchPageParser.getMatchFormat(doc);
        String matchDate = matchPageParser.getMatchDate(doc);
        List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
        matchesLink.matchMapsNames = String.join("\n", mapNames);
        List<String> teamsOdds = matchPageParser.getTeamsOdds(doc);
        matchesLink.leftTeamOdds = teamsOdds.get(0);
        matchesLink.rightTeamOdds = teamsOdds.get(1);
        matchesLink.matchTime = (int) (System.currentTimeMillis() - now);
        matchesParserService.save(matchesLink);
        MatchesDto matchesDto = constructDto(matchDate, matchesLink, mapNames, teams.get(0), teams.get(1));
        if (matchesDto.mapsPredict.size() != 0) {
            int i = 0;
        }
        return matchesDto;
    }

    private MatchesDto constructDto(String matchDate, MatchesLink matchesLink, List<String> mapNames, List<String> leftTeamIds, List<String> rightTeamIds) {
        MatchesDto matchesDtoN = new MatchesDto();
        if (leftTeamIds.size() > 0 && rightTeamIds.size() > 0) {
            getPredictByMapName(leftTeamIds, rightTeamIds, matchesDtoN.mapsPredict, matchesLink.matchUrl);
        }
        matchesDtoN.id = matchesLink.matchId;
        matchesDtoN.matchesUrl = matchesLink.matchUrl;
        matchesDtoN.leftTeam = matchesLink.leftTeam;
        matchesDtoN.rightTeam = matchesLink.rightTeam;
        matchesDtoN.matchFormat = matchesLink.matchFormat;
        matchesDtoN.matchDate = matchDate;
        matchesDtoN.matchMapsNames = mapNames;
        matchesDtoN.leftTeamOdds = matchesLink.leftTeamOdds;
        matchesDtoN.rightTeamOdds = matchesLink.rightTeamOdds;
        matchesDtoN.matchTime = matchesLink.matchTime;
        matchesDtoN.mapsPredict.forEach((k, v) -> {
            matchesDtoN.mapsPredictChanged.add(k + " - " + v);
        });
        return matchesDtoN;
    }

    private void getPredictByMapName(List<String> leftTeamIds, List<String> rightTeamIds, Map<String, String> predictMap, String url) {
        List<Integer> leftIds = leftTeamIds.stream().map(Integer::parseInt).toList();
        List<Integer> rightIds = rightTeamIds.stream().map(Integer::parseInt).toList();
        Map<String, Float> leftTeamForcesByMap = new HashMap<>();
        Map<String, Float> rightTeamForcesByMap = new HashMap<>();

        for (int j = 0; j < 7; j++) {
            Float leftTeamForce = 0f;
            int currentMap = Config.activeMaps.get(j);
            String currentMapString = MapsEnum.values()[currentMap].toString();
            Map<Integer, Float> forceMap = queryFactory.from(playerForce)
                    .where(playerForce.playerId.in(leftIds)
                            .and(playerForce.map.eq(currentMapString)))
                    .transform(GroupBy.groupBy(playerForce.playerId).as(playerForce.playerForce));
            Map<Integer, Integer> stabilityMap = queryFactory.from(playerForce)
                    .where(playerForce.playerId.in(leftIds)
                            .and(playerForce.map.eq(currentMapString)))
                    .transform(GroupBy.groupBy(playerForce.playerId).as(playerForce.playerStability));
            Float finalForce = 0f;
            for(Integer key : forceMap.keySet().stream().toList()) {
                float force = (forceMap.get(key) * stabilityMap.get(key)) / 100;
                finalForce += force;
            }
            leftTeamForcesByMap.put(currentMapString, finalForce);
        }
        for (int j = 0; j < 7; j++) {
            Float rightTeamForce = 0f;
            int currentMap = Config.activeMaps.get(j);
            String currentMapString = MapsEnum.values()[currentMap].toString();
            Map<Integer, Float> forceMap = queryFactory.from(playerForce)
                    .where(playerForce.playerId.in(rightIds)
                            .and(playerForce.map.eq(currentMapString)))
                    .transform(GroupBy.groupBy(playerForce.playerId).as(playerForce.playerForce));
            Map<Integer, Integer> stabilityMap = queryFactory.from(playerForce)
                    .where(playerForce.playerId.in(rightIds)
                            .and(playerForce.map.eq(currentMapString)))
                    .transform(GroupBy.groupBy(playerForce.playerId).as(playerForce.playerStability));
            Float finalForce = 0f;
            for(Integer key : forceMap.keySet().stream().toList()) {
                float force = (forceMap.get(key) * stabilityMap.get(key)) / 100;
                finalForce += force;
            }
            rightTeamForcesByMap.put(currentMapString, finalForce);
        }
        for (String mapName : leftTeamForcesByMap.keySet().stream().toList()) {
            Float leftForce = getForceWithMinorMaps(mapName, leftTeamForcesByMap);
            Float rightForce = getForceWithMinorMaps(mapName, rightTeamForcesByMap);
            if (leftForce > rightForce * Config.compareMultiplier) {
                predictMap.put(mapName, "left");
            }
            if (rightForce > leftForce * Config.compareMultiplier) {
                predictMap.put(mapName, "right");
            }
        }
    }

    private Float getForceWithMinorMaps(String mapName, Map<String, Float> teamForcesByMap) {
        Float result = 0f;
        for (int i = 0; i < 7; i++) {
            int currentMap = Config.activeMaps.get(i);
            String currentMapString = MapsEnum.values()[currentMap].toString();
            float force = teamForcesByMap.get(currentMapString);
            force = (mapName.equals(currentMapString)) ? force: force * 0.05f;
            result += force;
        }
        return result;
    }

    @GetMapping("/total-matches-count")
    public List<String> getTotalMatchesCountForParsing() {
        //matchesParserService.deleteAll();
        List<String> allLinks = matchesPageParser.parseMatches();
        return allLinks;
    }

    @GetMapping("/processed-matches-count")
    public Long getProcessedMatchesCount() {
        return matchesParserService.getProcessedMatchesCount();
    }

    @GetMapping("/clear-all-matches")
    public void clearAllMatches() {
        matchesParserService.deleteAll();
    }

    @GetMapping("/matches-from-db")
    public List<MatchesDto> getMatchesFromDB() {
        List<MatchesDto> matchesDtoList = new ArrayList<>();
        List<MatchesLink> matches = matchesParserService.getMatchesFromDB();
        matches.forEach(match -> {
            List<String> mapNames = Arrays.stream(match.matchMapsNames.split("\n")).toList();
            matchesDtoList.add(constructDto("IM FROM DB", match, mapNames, new ArrayList<>(), new ArrayList<>()));
        });
        return matchesDtoList;
    }
}
