package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.MatchesLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchesLinkRepository extends JpaRepository<MatchesLink, Integer> {


}
