package com.gen.GeneralModule.repositories;


import com.gen.GeneralModule.entities.PlayerOnMapResults;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerOnMapResults, Integer> {

}
