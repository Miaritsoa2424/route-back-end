package com.route.repositories;

import com.route.models.Image;
import com.route.models.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image,Integer>{
    public boolean existsByIdFirestore(String idFirestore);
    public boolean existsByLien(String lien);

    List<Image> findBySignalement(Signalement signalement);
}
