package com.route.repositories;

import com.route.models.SignalementStatut;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignalementStatutRepository extends JpaRepository<SignalementStatut, Integer> {
    List<SignalementStatut> findBySignalement(com.route.models.Signalement signalement);

}
