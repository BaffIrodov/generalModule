package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.services.StatsParserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stats")
@Log4j2
public class StatsController {
    @Autowired
    StatsParserService statsParserService;

    @GetMapping("/write-players")
    public Integer writePlayers() {
        log.info("Write players started");
        long now = System.currentTimeMillis();
        statsParserService.startParser();
        log.info("Write players done in: " + (System.currentTimeMillis() - now));
        return 1;
    }

    @GetMapping("/available-count")
    public Long getAvailableCountForParsing() {
        return statsParserService.getAvailableCount();
    }
}
