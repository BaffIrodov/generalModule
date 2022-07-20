package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.parsers.ResultsPageParser;
import com.gen.GeneralModule.services.ResultsParserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/results")
@Log4j2
public class ResultsController {
    @Autowired
    private ResultsParserService resultsParserService;

    @Autowired
    private ResultsPageParser resultsPageParser;

    @PostMapping("/write-links")
    public Integer writeAllLinks(@RequestBody Integer pageNumber){
        log.info("Write links started");
        long now = System.currentTimeMillis();
        List<String> allLinks = resultsPageParser.parseResultsGetAllLinks(pageNumber);
        resultsParserService.parseAndSaveLinks(allLinks);
        log.info("Write links done in: " + (System.currentTimeMillis() - now));
        return pageNumber;
    }
}
