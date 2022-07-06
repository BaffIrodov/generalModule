package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.parsers.ResultsPageParser;
import com.gen.GeneralModule.services.ResultsParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/results")
public class ResultsController {
    @Autowired
    private ResultsParserService resultsParserService;

    @Autowired
    private ResultsPageParser resultsPageParser;

    @PostMapping("/write-links")
    public Integer writeAllLinks(@RequestBody Integer pageNumber){
        List<String> allLinks = resultsPageParser.parseResultsGetAllLinks(pageNumber);
        List<ResultsLink> resultsLinks = new ArrayList<>();
        allLinks.forEach(link -> {
            ResultsLink resultsLink = new ResultsLink();
            resultsLink.matchUrl = link;
            resultsLink.processed = false;
            resultsLink.archive = false;
            resultsLinks.add(resultsLink);
        });
        resultsParserService.saveAll(resultsLinks);
        return pageNumber;
    }
}
