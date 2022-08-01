package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.dtos.MatchesWithTimeDto;
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
    public MatchesWithTimeDto writeAllLinks() {
        matchesParserService.deleteAll();
        long full = System.currentTimeMillis();
        List<String> allLinks = matchesPageParser.parseMatches();
        List<MatchesLink> matchesLinks = new ArrayList<>();
        List<MatchesDto> matchesDto = new ArrayList<>();
        MatchesWithTimeDto matchesWithTimeDto = new MatchesWithTimeDto();
        allLinks.forEach(link -> {
            // Искусственное замедление
            CommonUtils.waiter(50);

            long now = System.currentTimeMillis();
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
            matchesDtoN.matchTime = (int) (System.currentTimeMillis() - now);
            System.out.println("Обработано " + matchesDto.size() + " из " + allLinks.size());
        });
        matchesParserService.saveAll(matchesLinks);
        matchesWithTimeDto.matches = matchesDto;
        matchesWithTimeDto.fullTime = (int) (System.currentTimeMillis() - full);
        System.out.println("Полное время: " + matchesWithTimeDto.fullTime);
        return matchesWithTimeDto;
    }
}
