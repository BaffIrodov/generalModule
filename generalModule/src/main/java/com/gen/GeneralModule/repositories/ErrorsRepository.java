package com.gen.GeneralModule.repositories;

import com.gen.GeneralModule.entities.Errors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorsRepository extends JpaRepository<Errors, Integer> {

}
