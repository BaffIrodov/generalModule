package com.gen.GeneralModule.services;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.QMatchesLink;
import com.gen.GeneralModule.entities.QResultsLink;
import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.parsers.ResultPageParser;
import com.gen.GeneralModule.parsers.StatsPageParser;
import com.gen.GeneralModule.repositories.PlayerRepository;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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

    @Autowired
    private StatsPageParser statsPageParser;

    @Autowired
    private ResultPageParser resultPageParser;

    private static final QResultsLink resultsLink= new QResultsLink("resultsLink");


    public PlayerOnMapResults save(PlayerOnMapResults player) {
        return playerRepository.save(player);
    }

    public void startParser() {
        List<String> links = queryFactory
                .from(resultsLink).select(resultsLink.resultUrl)
                .where(resultsLink.archive.eq(false)).fetch();
        for (String link : links) {
            List<List<PlayerOnMapResults>> result = resultPageParser.parseMapStats(link);
            result.forEach(stats -> {
                playerRepository.saveAll(stats);
            });
            setLinkProcessed(link);
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
}