package com.gen.GeneralModule.services;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.dtos.requestResponseDtos.StatsRequestDto;
import com.gen.GeneralModule.entities.*;
import com.gen.GeneralModule.parsers.ResultPageParser;
import com.gen.GeneralModule.parsers.StatsPageParser;
import com.gen.GeneralModule.repositories.PlayerRepository;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import com.gen.GeneralModule.repositories.RoundHistoryRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class StatsParserService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private ResultsLinkRepository resultsLinkRepository;

    /*@Autowired
    private StatsPageParser statsPageParser;*/

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
        List<String> links = queryFactory
                .from(resultsLink).select(resultsLink.resultUrl)
                .where(resultsLink.processed.eq(false)).fetch();
        for (String link : links) {
            if (index < request.batchSize) {
                List<List<PlayerOnMapResults>> result = resultPageParser.parseMapStats(link);
                result.forEach(stats -> {
                    playerRepository.saveAll(stats);
                });
                setLinkProcessed(link);
                index++;
            } else {
                break;
            }
        }
    }

    private void setLinkProcessed(String link) {
        Integer id = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
        ResultsLink res = resultsLinkRepository
                .findById(id)
                .orElse(null);
        resultsLinkRepository.deleteById(id);
        res.processed = true;
        resultsLinkRepository.save(res);
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
