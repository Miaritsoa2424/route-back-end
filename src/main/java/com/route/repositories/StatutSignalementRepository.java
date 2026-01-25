package com.route.repositories;

import com.route.models.StatutSignalement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatutSignalementRepository extends JpaRepository<StatutSignalement, Integer> {
    StatutSignalement findStatutByLibelle(String libelle);
}
