package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.services.MatchesParserService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/matches")
public class MatchesController {
    @Autowired
    private MatchesParserService matchesParserService;

    @Autowired
    private MatchesPageParser matchesPageParser;

    private MatchPageParser matchPageParser = new MatchPageParser();

    @PostMapping("/write-links")
    public List<MatchesDto> writeAllLinks() {
        matchesParserService.deleteAll();
        long now = System.currentTimeMillis();

        List<String> allLinks = matchesPageParser.parseMatches();
        List<MatchesLink> matchesLinks = new ArrayList<>();
        List<MatchesDto> matchesDto = new ArrayList<>();
        allLinks.forEach(link -> {
            MatchesLink matchesLink = new MatchesLink();
            matchesLink.matchId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
            matchesLink.matchUrl = link;
            Document doc = CommonUtils.reliableConnectAndGetDocument(link);
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

            MatchesDto matchesDtoN = new MatchesDto();
            matchesDtoN.id = matchesLink.matchId;
            matchesDtoN.matchesUrl = matchesLink.matchUrl;
            matchesDtoN.leftTeam = matchesLink.leftTeam;
            matchesDtoN.rightTeam = matchesLink.rightTeam;
            matchesDtoN.matchFormat = matchesLink.matchFormat;
            matchesDtoN.matchMapsNames = mapsNames;
            matchesDtoN.leftTeamOdds = matchesLink.leftTeamOdds;
            matchesDtoN.rightTeamOdds = matchesLink.rightTeamOdds;
            matchesDto.add(matchesDtoN);
        });
        matchesParserService.saveAll(matchesLinks);

        System.out.println("Время записи данных: " + (System.currentTimeMillis() - now));
        return matchesDto;
    }
}