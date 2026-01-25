package com.route.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.route.FirebaseDTO.SignalementDto;
import com.route.models.Entreprise;
import com.route.models.Signalement;
import com.route.models.SignalementStatut;
import com.route.models.Users;
import com.route.repositories.EntrepriseRepository;
import com.route.repositories.SignalementRepository;
import com.route.repositories.SignalementStatutRepository;
import com.route.repositories.StatutSignalementRepository;
import com.route.repositories.UserRepository;
import com.route.FirebaseDTO.LocalisationDto;

@Service
public class SignalementService {

    private static final String COLLECTION_NAME = "signalement";

    private final SignalementRepository signalementRepository;
    private final UserRepository usersRepository;
    private final SignalementStatutRepository signalementStatutRepository;
    private final StatutSignalementRepository statutSignalementRepository;
    private final EntrepriseRepository entrepriseRepository;

    public SignalementService(SignalementRepository signalementRepository, UserRepository usersRepository, SignalementStatutRepository signalementStatutRepository, StatutSignalementRepository statutSignalementRepository, EntrepriseRepository entrepriseRepository) {
        this.signalementRepository = signalementRepository;
        this.usersRepository = usersRepository;
        this.signalementStatutRepository = signalementStatutRepository;
        this.statutSignalementRepository = statutSignalementRepository;
        this.entrepriseRepository = entrepriseRepository;
    }

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

            signalement.setUser(doc.getString("email"));
            signalement.setDernierStatut(doc.getString("dernier_statut"));
            signalement.setEntreprise(doc.getString("entreprise"));

            projets.add(signalement);
        }

        return projets;
    }


    // New method: Save a SignalementDto to Firebase (for syncing)
    public void saveSignalementToFirebase(SignalementDto signalementDto) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> data = new HashMap<>();

        data.put("budget", signalementDto.getBudget());
        data.put("avancement", signalementDto.getAvancement());
        data.put("surface", signalementDto.getSurface());
        
        if (signalementDto.getLocalisation() != null) {
            data.put("localisation", new GeoPoint(signalementDto.getLocalisation().getLatitude(), signalementDto.getLocalisation().getLongitude()));
        }
        
        data.put("user", signalementDto.getUser());
        data.put("dernier_statut", signalementDto.getDernierStatut());
        data.put("entreprise", signalementDto.getEntreprise());
        
        // Use the ID as document ID, or generate one if null
        String docId = signalementDto.getId() != null ? signalementDto.getId() : db.collection(COLLECTION_NAME).document().getId();
        
        ApiFuture<WriteResult> future = db.collection(COLLECTION_NAME).document(docId).set(data);
        future.get(); // Wait for completion
    }

    // New method: Sync all Signalements from DB to Firebase
    // Note: This assumes you inject repositories here (see below)
    // For now, placeholder - integrate with your controllers/services
    public void syncAllSignalementsToFirebase(List<SignalementDto> signalements) throws ExecutionException, InterruptedException {
        for (SignalementDto dto : signalements) {
            saveSignalementToFirebase(dto);
        }
    }

    public String syncFromFirebaseToDB() throws ExecutionException, InterruptedException {
        List<SignalementDto> dtos = listProjets(); // Fetch from Firestore
        GeometryFactory geometryFactory = new GeometryFactory(); // For PostGIS Point

        for (SignalementDto dto : dtos) {
            Signalement existingSignalement = signalementRepository.findByFirestoreId(dto.getId());
            if (existingSignalement != null) {
                System.out.println("Signalement déjà existant pour firestoreId: " + dto.getId() + ", sauté.");
                continue; // Saute l'insertion
            }

            // Map user: Assume dto.getUser() is the email/identifiant from Firebase Auth
            Users user = usersRepository.findByIdentifiant(dto.getUser()); // Query by identifiant (email)
            if (user == null) {
                // Handle missing user: Log error or skip (don't insert if user not found)
                System.err.println("User not found for identifiant: " + dto.getUser());
                continue; // Skip this signalement
                
            }

            // Create or update Signalement
            Signalement signalement = new Signalement();
            signalement.setFirestoreId(dto.getId());
            //signalement.setIdSignalement(Integer.parseInt(dto.getId())); // Assuming ID is integer
            signalement.setBudget(dto.getBudget() != null ? BigDecimal.valueOf(dto.getBudget()) : null);
            signalement.setSurface(dto.getSurface() != null ? BigDecimal.valueOf(dto.getSurface()) : null);
            signalement.setUser(user); // Set the mapped Users object

            // Localisation: Convert GeoPoint to PostGIS Point
            if (dto.getLocalisation() != null) {
                Point point = geometryFactory.createPoint(new Coordinate(dto.getLocalisation().getLongitude(), dto.getLocalisation().getLatitude()));
                point.setSRID(4326); // Assuming WGS84
                signalement.setLocalisation(point);
            }

            Entreprise entreprise = entrepriseRepository.findByNom(dto.getEntreprise());
            signalement.setEntreprise(entreprise); 
            
            
            SignalementStatut signalementStatut = new SignalementStatut();
            signalementStatut.setStatutSignalement(statutSignalementRepository.findStatutByLibelle(dto.getDernierStatut()));
            signalementStatut.setDateStatut(java.time.LocalDateTime.now());
            signalementStatut.setUser(user);
            signalementStatut.setSignalement(signalement);
            

            signalementRepository.save(signalement);
            signalementStatutRepository.save(signalementStatut);
            // Save to DB (this will insert or update based on ID)
        }
        return "Sync from Firestore to DB completed";
    }

}
