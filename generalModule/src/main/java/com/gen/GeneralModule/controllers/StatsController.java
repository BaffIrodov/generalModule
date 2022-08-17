package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.dtos.requestResponseDtos.StatsRequestDto;
import com.gen.GeneralModule.dtos.requestResponseDtos.StatsResponseDto;
import com.gen.GeneralModule.entities.StatsResponse;
import com.gen.GeneralModule.repositories.StatsResponseRepository;
import com.gen.GeneralModule.services.StatsParserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stats")
@Log4j2
public class StatsController {
    @Autowired
    StatsParserService statsParserService;

    @Autowired
    StatsResponseRepository statsResponseRepository;

    @PostMapping("/write-players")
    public StatsResponseDto writePlayers(@RequestBody StatsRequestDto request) {
        StatsResponseDto dto = new StatsResponseDto();
        log.info("Write players started");
        long now = System.currentTimeMillis();
        statsParserService.startParser(request);
        log.info("Write players done in: " + (System.currentTimeMillis() - now));
        fillResponse(dto, request.batchSize, (int)(System.currentTimeMillis() - now));
        fillAndSaveResponseEntity(dto);
        return dto;
    }

    @GetMapping("/available-count")
    public Long getAvailableCountForParsing() {
        return statsParserService.getAvailableCount();
    }

    @GetMapping("/total-count")
    public Long getTotalCountForParsing() { return statsParserService.getTotalCount(); }

    @GetMapping("/response-analytics")
    public List<StatsResponse> getResponseAnalytics() {
        return statsParserService.getResponseAnalytics();
    }

    private StatsResponseDto fillResponse(StatsResponseDto dto, Integer batchSize, Integer batchTime) {
        dto.batchSize = batchSize;
        dto.batchTime = batchTime;
        dto.requestDate = new Date();
        return dto;
    }

    private void fillAndSaveResponseEntity(StatsResponseDto dto) {
        StatsResponse statsResponse = new StatsResponse();
        statsResponse.batchSize = dto.batchSize;
        statsResponse.batchTime = dto.batchTime;
        statsResponse.requestDate = dto.requestDate;
        statsResponseRepository.save(statsResponse);
    }
}
