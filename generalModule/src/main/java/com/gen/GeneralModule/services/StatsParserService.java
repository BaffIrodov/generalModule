package com.gen.GeneralModule.services;

import com.gen.GeneralModule.entities.PlayerOnMapResults;
import com.gen.GeneralModule.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsParserService {
    @Autowired
    private PlayerRepository playerRepository;

    public PlayerOnMapResults save(PlayerOnMapResults player) {
        return playerRepository.save(player);
    }
}
