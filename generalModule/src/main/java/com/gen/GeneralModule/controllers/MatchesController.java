package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.dtos.MatchesDto;
import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.parsers.MatchPageParser;
import com.gen.GeneralModule.parsers.MatchesPageParser;
import com.gen.GeneralModule.services.MatchesParserService;
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
        List<String> allLinks = matchesPageParser.parseMatches();
        List<MatchesLink> matchesLinks = new ArrayList<>();
        List<MatchesDto> matchesDto = new ArrayList<>();
        allLinks.forEach(link -> {
            MatchesLink matchesLink = new MatchesLink();
            matchesLink.id = Integer.parseInt(link.replaceAll(".*/matches/", "").replaceAll("/.*", ""));
            matchesLink.matchUrl = link;
            List<String> teamsNames = matchPageParser.getTeamsNames(link);
            matchesLink.leftTeam = teamsNames.get(0);
            matchesLink.rightTeam = teamsNames.get(1);
            //matchPageParser.getMatchFormat(link);
            matchesLinks.add(matchesLink);

            MatchesDto matchesDtoN = new MatchesDto();
            matchesDtoN.id = matchesLink.id;
            matchesDtoN.matchesUrl = matchesLink.matchUrl;
            matchesDtoN.leftTeam = matchesLink.leftTeam;
            matchesDtoN.rightTeam = matchesLink.rightTeam;
            matchesDto.add(matchesDtoN);
        });
        matchesParserService.saveAll(matchesLinks);
        return matchesDto;
    }
}
