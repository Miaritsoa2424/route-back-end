package com.route.repositories;

import com.route.models.Avancement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvancementRepository extends JpaRepository<Avancement, Integer> {
    List<Avancement> findBySignalement(com.route.models.Signalement signalement);
}
