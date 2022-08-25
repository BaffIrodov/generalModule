package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.dtos.MatchesWithTimeDto;
import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.services.ErrorsService;
import com.gen.GeneralModule.services.MatchesParserService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

    private MatchPageParser matchPageParser = new MatchPageParser();

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
            matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
            matchesLink.matchUrl = link;
            Document doc = commonUtils.reliableConnectAndGetDocument(link);
            List<String> teamNames = matchPageParser.getTeamsNames(doc);
            matchesLink.leftTeam = teamNames.get(0);
            matchesLink.rightTeam = teamNames.get(1);
            matchesLink.matchFormat = matchPageParser.getMatchFormat(doc);
            List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
            matchesLink.matchMapsNames = String.join("\n", mapNames);
            List<String> teamOdds = matchPageParser.getTeamsOdds(doc);
            matchesLink.leftTeamOdds = teamOdds.get(0);
            matchesLink.rightTeamOdds = teamOdds.get(1);
            matchesLinks.add(matchesLink);
            matchesLink.matchTime = (int) (System.currentTimeMillis() - now);
            matchesParserService.save(matchesLink);

            MatchesDto matchesDto = constructDto(matchesLink, mapNames);
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
        matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
        matchesLink.matchUrl = link;
        Document doc = commonUtils.reliableConnectAndGetDocument(link);
        List<String> teamsNames = matchPageParser.getTeamsNames(doc);
        matchesLink.leftTeam = teamsNames.get(0);
        matchesLink.rightTeam = teamsNames.get(1);
        matchesLink.matchFormat = matchPageParser.getMatchFormat(doc);
        List<String> mapNames = matchPageParser.getMatchMapsNames(doc);
        matchesLink.matchMapsNames = String.join("\n", mapNames);
        List<String> teamsOdds = matchPageParser.getTeamsOdds(doc);
        matchesLink.leftTeamOdds = teamsOdds.get(0);
        matchesLink.rightTeamOdds = teamsOdds.get(1);
        matchesLink.matchTime = (int) (System.currentTimeMillis() - now);
        matchesParserService.save(matchesLink);
        MatchesDto matchesDto = constructDto(matchesLink, mapNames);
        return matchesDto;
    }

    private MatchesDto constructDto(MatchesLink matchesLink, List<String> mapNames) {
        MatchesDto matchesDtoN = new MatchesDto();
        matchesDtoN.id = matchesLink.matchId;
        matchesDtoN.matchesUrl = matchesLink.matchUrl;
        matchesDtoN.leftTeam = matchesLink.leftTeam;
        matchesDtoN.rightTeam = matchesLink.rightTeam;
        matchesDtoN.matchFormat = matchesLink.matchFormat;
        matchesDtoN.matchMapsNames = mapNames;
        matchesDtoN.leftTeamOdds = matchesLink.leftTeamOdds;
        matchesDtoN.rightTeamOdds = matchesLink.rightTeamOdds;
        matchesDtoN.matchTime = matchesLink.matchTime;
        return matchesDtoN;
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
    public void clearAllMatches() { matchesParserService.deleteAll(); }

    @GetMapping("/matches-from-db")
    public List<MatchesDto> getMatchesFromDB() {
        List<MatchesDto> matchesDtoList = new ArrayList<>();
        List<MatchesLink> matches = matchesParserService.getMatchesFromDB();
        matches.forEach(match -> {
            List<String> mapNames = Arrays.stream(match.matchMapsNames.split("\n")).toList();
            matchesDtoList.add(constructDto(match, mapNames));
        });
        return matchesDtoList;
    }
}
