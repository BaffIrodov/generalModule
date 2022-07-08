package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.parsers.StatsPageParser;
import com.gen.GeneralModule.repositories.PlayerRepository;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsParserService {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ResultsLinkRepository resultsLinkRepository;

    @Autowired
    private StatsPageParser statsPageParser;


    @Autowired
    private ResultsLink resultsLink;

    QresultsLink

    @Autowired
    private PlayerOnMapResults player;
//
//    @Autowired
//    private JPAQueryFactory queryFactory;

    public PlayerOnMapResults save(PlayerOnMapResults player) {
        return playerRepository.save(player);
    }

    public void startParser() {
        ResultsLink exampleLink = new ResultsLink();
        exampleLink.processed = false;
        Example<ResultsLink> example = Example.of(exampleLink);
        List<ResultsLink> oke = resultsLinkRepository.findAll(example);
//        JDBC
//        statsPageParser.parseMapStats(url);
    }
}
