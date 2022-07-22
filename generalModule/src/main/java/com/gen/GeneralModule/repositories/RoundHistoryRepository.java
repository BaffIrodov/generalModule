package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.RoundHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundHistoryRepository extends JpaRepository<RoundHistory, Integer> {

}
