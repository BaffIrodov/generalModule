package com.gen.GeneralModule.services;

import com.gen.GeneralModule.common.CommonUtils;
import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ResultsParserService {

    @Autowired
    private ResultsLinkRepository resultsLinkRepository;

    public ResultsLink save(ResultsLink resultsLink) {
        return resultsLinkRepository.save(resultsLink);
    }

    public void parseAndSaveLinks(List<String> allLinks) {
        List<ResultsLink> resultsLinks = new ArrayList<>();
        allLinks.forEach(link -> {
            Integer resultId = Integer.parseInt(CommonUtils.standardIdParsingBySlice("/matches/", link));
            if(resultsLinkRepository.findById(resultId).isEmpty()) { //пишем в базу только то, что ранее записано не было
                ResultsLink resultsLink = new ResultsLink();
                resultsLink.resultId = resultId;
                resultsLink.resultUrl = link;
                resultsLink.processed = false;
                resultsLink.archive = false;
                resultsLinks.add(resultsLink);
            }
        });
        saveAll(resultsLinks);
    }

    public List<ResultsLink> saveAll(List<ResultsLink> resultsLink) {
        return resultsLinkRepository.saveAll(resultsLink);
    }
}
