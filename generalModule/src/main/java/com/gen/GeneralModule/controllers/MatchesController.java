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

    @PostMapping("/write-links")
    public MatchesWithTimeDto writeAllLinks() {
        matchesParserService.deleteAll();
        long fullTime = System.currentTimeMillis();
        List<String> allLinks = matchesPageParser.parseMatches();
        List<MatchesLink> matchesLinks = new ArrayList<>();
        List<MatchesDto> listMatchesDto = new ArrayList<>();
        MatchesWithTimeDto matchesWithTimeDto = new MatchesWithTimeDto();
        //Random rand = new Random();
        allLinks.forEach(link -> {
            // Искусственное замедление
            commonUtils.waiter(400);
            long now = System.currentTimeMillis();
            MatchesLink matchesLink = new MatchesLink();
            matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
            matchesLink.matchUrl = link;
            Document doc = commonUtils.reliableConnectAndGetDocument(link);
            List<String> teamsNames = matchPageParser.getTeamsNames(doc);
            matchesLink.leftTeam = teamsNames.get(0);
            matchesLink.rightTeam = teamsNames.get(1);
            matchesLink.matchFormat = matchPageParser.getMatchFormat(doc);
            List<String> mapsNames = matchPageParser.getMatchMapsNames(doc);
            matchesLink.matchMapsNames = String.join("\n", mapsNames);
            List<String> teamsOdds = matchPageParser.getTeamsOdds(doc);
            matchesLink.leftTeamOdds = teamsOdds.get(0);
            matchesLink.rightTeamOdds = teamsOdds.get(1);
            matchesLinks.add(matchesLink);

            MatchesDto matchesDto = constructDto(matchesLink, mapsNames, now);
            listMatchesDto.add(matchesDto);
            //System.out.println("Обработано " + matchesDto.size() + " из " + allLinks.size());
        });
        matchesParserService.saveAll(matchesLinks);
        matchesWithTimeDto.matches = listMatchesDto;
        matchesWithTimeDto.fullTime = (int) (System.currentTimeMillis() - fullTime);
        System.out.println("Полное время: " + matchesWithTimeDto.fullTime);
        return matchesWithTimeDto;
    }

    private MatchesDto constructDto(MatchesLink matchesLink, List<String> mapsNames, long now) {
        MatchesDto matchesDtoN = new MatchesDto();
        matchesDtoN.id = matchesLink.matchId;
        matchesDtoN.matchesUrl = matchesLink.matchUrl;
        matchesDtoN.leftTeam = matchesLink.leftTeam;
        matchesDtoN.rightTeam = matchesLink.rightTeam;
        matchesDtoN.matchFormat = matchesLink.matchFormat;
        matchesDtoN.matchMapsNames = mapsNames;
        matchesDtoN.leftTeamOdds = matchesLink.leftTeamOdds;
        matchesDtoN.rightTeamOdds = matchesLink.rightTeamOdds;
        matchesDtoN.matchTime = (int) (System.currentTimeMillis() - now);
        return matchesDtoN;
    }
}
