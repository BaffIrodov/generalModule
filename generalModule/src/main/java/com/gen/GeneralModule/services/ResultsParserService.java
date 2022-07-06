package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultsParserService {

    @Autowired
    private ResultsLinkRepository resultsLinkRepository;

    public ResultsLink save(ResultsLink resultsLink) {
        return resultsLinkRepository.save(resultsLink);
    }

    public List<ResultsLink> saveAll(List<ResultsLink> resultsLink) {
        return resultsLinkRepository.saveAll(resultsLink);
    }
}
