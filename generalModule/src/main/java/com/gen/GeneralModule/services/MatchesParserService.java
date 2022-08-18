package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.MatchesLink;
import com.gen.GeneralModule.entities.QResultsLink;
import com.gen.GeneralModule.repositories.MatchesLinkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchesParserService {

    @Autowired
    private MatchesLinkRepository matchesLinkRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    private static final QResultsLink matchesLink = new QResultsLink("matchesLink");

    public List<MatchesLink> saveAll(List<MatchesLink> matchesLink) {
        return matchesLinkRepository.saveAll(matchesLink);
    }

    public void deleteAll() {
        matchesLinkRepository.deleteAll();
    }

    public void deleteById(Integer id) {
        matchesLinkRepository.deleteById(id);
    }

    public Long getProcessedMatchesCount(){
        Long count = queryFactory.from(matchesLink).stream().count();
        return count;
    }
}
