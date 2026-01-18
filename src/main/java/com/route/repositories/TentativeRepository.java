package com.route.repositories;

import com.route.models.Tentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TentativeRepository extends JpaRepository<Tentative, Integer> {
}
