package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.dtos.requestResponseDtos.StatsRequestDto;
import com.gen.GeneralModule.dtos.requestResponseDtos.StatsResponseDto;
import com.gen.GeneralModule.services.StatsParserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stats")
@Log4j2
public class StatsController {
    @Autowired
    StatsParserService statsParserService;

    @PostMapping("/write-players")
    public StatsResponseDto writePlayers(@RequestBody StatsRequestDto request) {
        StatsResponseDto dto = new StatsResponseDto();
        log.info("Write players started");
        long now = System.currentTimeMillis();
        statsParserService.startParser(request);
        log.info("Write players done in: " + (System.currentTimeMillis() - now));
        fillResponse(dto, request.batchSize, (int)(System.currentTimeMillis() - now));
        return dto;
    }

    @GetMapping("/available-count")
    public Long getAvailableCountForParsing() {
        return statsParserService.getAvailableCount();
    }

    private StatsResponseDto fillResponse(StatsResponseDto dto, Integer batchSize, Integer batchTime) {
        dto.batchSize = batchSize;
        dto.batchTime = batchTime;
        dto.requestDate = new Date();
        return dto;
    }
}
