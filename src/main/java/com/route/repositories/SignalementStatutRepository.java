package com.route.repositories;

import com.route.models.SignalementStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalementStatutRepository extends JpaRepository<SignalementStatut, Integer> {

    @Query("SELECT ss FROM SignalementStatut ss WHERE ss.dateStatut = (SELECT MAX(ss2.dateStatut) FROM SignalementStatut ss2 WHERE ss2.signalement = ss.signalement)")
    List<SignalementStatut> findLatestStatuses();
}
