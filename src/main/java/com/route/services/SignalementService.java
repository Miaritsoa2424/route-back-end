package com.route.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.route.FirebaseDTO.SignalementDto;
import com.route.FirebaseDTO.LocalisationDto;
import com.google.cloud.firestore.GeoPoint;

@Service
public class SignalementService {

    private static final String COLLECTION_NAME = "signalement";

    public List<SignalementDto> listProjets()
            throws ExecutionException, InterruptedException {

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME).get();

        QuerySnapshot querySnapshot = future.get();

        List<SignalementDto> projets = new ArrayList<>();

        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

            SignalementDto signalement = new SignalementDto();
            signalement.setId(doc.getId());

            Long budget = doc.getLong("budget");
            Long avancement = doc.getLong("avancement");
            Long surface = doc.getLong("surface");

            signalement.setBudget(budget != null ? budget.intValue() : 0);
            signalement.setAvancement(avancement != null ? avancement.intValue() : 0);
            signalement.setSurface(surface != null ? surface.intValue() : 0);

            // ✅ CORRECTION ICI
            GeoPoint geoPoint = doc.getGeoPoint("localisation");
            if (geoPoint != null) {
                LocalisationDto loc = new LocalisationDto();
                loc.setLatitude(geoPoint.getLatitude());
                loc.setLongitude(geoPoint.getLongitude());
                signalement.setLocalisation(loc);
            }

            signalement.setUser(doc.getString("user"));
            signalement.setDernierStatut(doc.getString("dernier_statut"));
            signalement.setEntreprise(doc.getString("entreprise"));

            projets.add(signalement);
        }

        return projets;
    }

}
