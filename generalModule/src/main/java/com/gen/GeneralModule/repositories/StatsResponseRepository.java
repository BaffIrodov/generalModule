package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.StatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsResponseRepository extends JpaRepository<StatsResponse, Integer> {
}
