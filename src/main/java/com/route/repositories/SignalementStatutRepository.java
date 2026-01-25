package com.route.repositories;

import com.route.models.SignalementStatut;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalementStatutRepository extends JpaRepository<SignalementStatut, Integer> {
    List<SignalementStatut> findBySignalement(com.route.models.Signalement signalement);


    @Query("SELECT ss FROM SignalementStatut ss WHERE ss.dateStatut = (SELECT MAX(ss2.dateStatut) FROM SignalementStatut ss2 WHERE ss2.signalement = ss.signalement)")
    List<SignalementStatut> findLatestStatuses();
}
