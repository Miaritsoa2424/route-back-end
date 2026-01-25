package com.route.repositories;

import com.route.models.Avancement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AvancementRepository extends JpaRepository<Avancement, Integer> {
    List<Avancement> findBySignalement(com.route.models.Signalement signalement);

    @Query("SELECT AVG(a.avancement) FROM Avancement a")
    BigDecimal avgAvancement();
}
