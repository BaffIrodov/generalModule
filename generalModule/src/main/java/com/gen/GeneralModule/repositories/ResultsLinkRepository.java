package com.gen.GeneralModule.repositories;


import com.gen.GeneralModule.entities.ResultsLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultsLinkRepository extends JpaRepository<ResultsLink, Integer> {

}
