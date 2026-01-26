package com.route.repositories;

import com.route.models.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Integer> {
    Signalement findByFirestoreId(String firestoreId);

    @Query("SELECT COUNT(s) FROM Signalement s")
    long countSignalements();

    @Query("SELECT SUM(s.surface) FROM Signalement s")
    BigDecimal sumSurface();

    @Query("SELECT SUM(s.budget) FROM Signalement s")
    BigDecimal sumBudget();

    List<Signalement> findByFirestoreIdIsNull();
}
