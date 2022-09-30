package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.repositories.PlayerOnMapResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParserService {

    @Autowired
    private PlayerOnMapResultsRepository playerOnMapResultsRepository;

    public PlayerOnMapResults save(PlayerOnMapResults player) {
        return playerOnMapResultsRepository.save(player);
    }

}
