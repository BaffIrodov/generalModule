package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.parsers.ResultsPageParser;
import com.gen.GeneralModule.services.ResultsParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
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
        resultsParserService.parseAndSaveLinks(allLinks);
        return pageNumber;
    }
}
