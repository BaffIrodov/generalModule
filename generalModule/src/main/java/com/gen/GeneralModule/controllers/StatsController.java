package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.services.StatsParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stats")
public class StatsController {
    @Autowired
    StatsParserService statsParserService;

    @GetMapping("/write-players")
    public Integer writePlayers() {
        statsParserService.startParser();
        return 1;
    }
}
