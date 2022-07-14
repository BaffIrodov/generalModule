package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.entities.MatchesLink;
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

    @PostMapping("/write-links")
    public List<MatchesLink> writeAllLinks() {
        List<String> allLinks = matchesPageParser.parseMatches();
        List<MatchesLink> matchesLinks = new ArrayList<>();
        allLinks.forEach(link -> {
            MatchesLink matchesLink = new MatchesLink();
            matchesLink.id = Integer.parseInt(link.replaceAll(".*/matches/", "").replaceAll("/.*", ""));
            matchesLink.matchUrl = link;
            matchesLinks.add(matchesLink);
        });
        matchesParserService.saveAll(matchesLinks);
        return matchesLinks;
    }
}
