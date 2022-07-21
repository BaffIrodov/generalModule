package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.ResultsLink;
import com.gen.GeneralModule.repositories.ResultsLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebugService {
    @Autowired
    ResultsLinkRepository resultsLinkRepository;

    public void setResultsLinkProcessed(Boolean value) {
        List<ResultsLink> allLinks = resultsLinkRepository.findAll();
        allLinks.forEach(e -> e.processed = value);
        resultsLinkRepository.deleteAll();
        resultsLinkRepository.saveAll(allLinks);
    }
}
