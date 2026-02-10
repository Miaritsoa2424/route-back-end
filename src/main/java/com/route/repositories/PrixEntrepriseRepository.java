package com.route.repositories;

import com.route.models.PrixEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrixEntrepriseRepository extends JpaRepository<PrixEntreprise, Integer> {
    List<PrixEntreprise> findByEntrepriseIdEntrepriseOrderByDateDesc(Integer idEntreprise);
}
