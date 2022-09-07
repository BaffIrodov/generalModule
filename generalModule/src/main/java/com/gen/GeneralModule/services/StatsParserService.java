package com.gen.GeneralModule.services;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.dtos.requestResponseDtos.StatsRequestDto;
import com.gen.GeneralModule.entities.*;
import com.gen.GeneralModule.parsers.ResultPageParser;
import com.gen.GeneralModule.repositories.PlayerRepository;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import com.gen.GeneralModule.repositories.RoundHistoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class StatsParserService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ResultsLinkRepository resultsLinkRepository;

    @Autowired
    private ResultPageParser resultPageParser;

    @Autowired
    private RoundHistoryRepository roundHistoryRepository;

    private static final QResultsLink resultsLink = new QResultsLink("resultsLink");
    private static final QStatsResponse statsResponse = new QStatsResponse("statsResponse");


    public PlayerOnMapResults save(PlayerOnMapResults player) {
        return playerRepository.save(player);
    }

    public void startParser(StatsRequestDto request) {
        int index = 0;
        AtomicInteger mapsCount = new AtomicInteger();
        List<String> links = queryFactory
                .from(resultsLink).select(resultsLink.resultUrl)
                .where(resultsLink.processed.eq(false)).fetch();
        for (String link : links) {
            if (index < request.batchSize) {
                Map<List<PlayerOnMapResults>, RoundHistory> resultMap = resultPageParser.parseMapStats(link);
                if(resultMap != null) {
                    List<List<PlayerOnMapResults>> result = resultMap.keySet().stream().toList();
                    List<RoundHistory> resultValues = resultMap.values().stream().toList();
                    result.forEach(stats -> {
                        mapsCount.getAndIncrement();
                        playerRepository.saveAll(stats);
                    });
                    resultValues.forEach(this::saveRoundHistory);
                    setLinkProcessed(link);
                }
                index++;
            } else {
                break;
            }
        }
        log.info("Количество карт: " + mapsCount.toString());
    }

    private void setLinkProcessed(String link) {
        Integer id = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
        ResultsLink res = resultsLinkRepository
                .findById(id)
                .orElse(null);
        if(res != null) {
            resultsLinkRepository.deleteById(id);
            res.processed = true;
            resultsLinkRepository.save(res);
        }
    }

    public Long getAvailableCount() {
        Long count = queryFactory.from(resultsLink).where(resultsLink.processed.eq(false)).stream().count();
        return count;
    }

    public List<StatsResponse> getResponseAnalytics() {
        List<StatsResponse> result = (List<StatsResponse>) queryFactory.from(statsResponse).fetch();
        return result;
    }

    public RoundHistory saveRoundHistory(RoundHistory roundHistory) {
        return roundHistoryRepository.save(roundHistory);
    }
}
