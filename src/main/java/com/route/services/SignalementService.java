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
import java.util.Optional;

import com.route.FirebaseDTO.SignalementDto;
import com.route.models.Entreprise;
import com.route.models.Signalement;
import com.route.models.SignalementStatut;
import com.route.models.StatutSignalement;
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

    public List<SignalementDto> listSignalements()
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

    public SignalementDto getSignalementByIdFireStore(String idFirestore) throws ExecutionException, InterruptedException{
        for (SignalementDto dto : listSignalements()) {
            if (dto.getIdFirestore().equals(idFirestore)) {
                return dto;
            }
        }
        return null;
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
        
        data.put("email", signalementDto.getUser());
        data.put("dernier_statut", signalementDto.getDernierStatut());
        data.put("entreprise", signalementDto.getEntreprise());
        
        // Use the ID as document ID, or generate one if null
        String docId = signalementDto.getIdFirestore() != null ? signalementDto.getIdFirestore() : db.collection(COLLECTION_NAME).document().getId();

        Signalement signalement = signalementRepository.findById(Integer.parseInt(signalementDto.getId())).orElse(null);

            if (signalement != null) {
                // Update the Firestore ID in the database
                signalement.setFirestoreId(docId);
                signalementRepository.save(signalement);
            }

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
        List<SignalementDto> dtos = listSignalements(); // Fetch from Firestore
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
            
            
            // Ensure StatutSignalement exists to avoid not-null constraint violations
            StatutSignalement statut = statutSignalementRepository.findStatutByLibelle(dto.getDernierStatut());
            if (statut == null) {
                statut = new StatutSignalement();
                statut.setLibelle(dto.getDernierStatut());
                statutSignalementRepository.save(statut);
            }
            SignalementStatut signalementStatut = new SignalementStatut();
            signalementStatut.setStatutSignalement(statut);
             signalementStatut.setDateStatut(java.time.LocalDateTime.now());
             signalementStatut.setUser(user);
             signalementStatut.setSignalement(signalement);
            

            signalementRepository.save(signalement);
            signalementStatutRepository.save(signalementStatut);
            // Save to DB (this will insert or update based on ID)
        }
        return "Sync from Firestore to DB completed";
    }


    public SignalementStatut getLastStatut(Signalement signalement) {
        return signalementStatutRepository.findTopBySignalementIdSignalementOrderByDateStatutDesc(signalement.getIdSignalement());
    }

    // Nouvel ajout: met à jour le champ "dernier_statut" d'un signalement dans Firestore
    // public void updateDernierStatutInFirestore(Signalement signalement) throws ExecutionException, InterruptedException {

    //     SignalementStatut lastStatut = getLastStatut(signalement);

    //     Firestore db = FirestoreClient.getFirestore();
    //     DocumentReference docRef = db.collection(COLLECTION_NAME).document(signalement.getFirestoreId());

    //     Map<String, Object> updates = new HashMap<>();
    //     updates.put("dernier_statut", lastStatut.getStatutSignalement().getLibelle());

    //     ApiFuture<WriteResult> writeFuture = docRef.update(updates);
    //     writeFuture.get(); // attendre la fin de la mise à jour
    // }

    // Ajouter cette méthode dans SignalementService
    private String mapStatutToFirestore(String libelle) {
        if (libelle == null) return "nouveau";
        
        switch(libelle) {
            case "Signalé": return "nouveau";
            case "En cours": return "en_cours";
            case "Résolu": return "resolu";
            case "Rejeté": return "rejete";
            default: return "nouveau";
        }
    }

    // Modifier la méthode updateDernierStatutInFirestore(Signalement signalement)
    public void updateDernierStatutInFirestore(Signalement signalement) throws ExecutionException, InterruptedException {
        SignalementStatut lastStatut = getLastStatut(signalement);
        
        if (lastStatut == null || signalement.getFirestoreId() == null) {
            return; // Skip si pas de statut ou pas d'ID Firestore
        }

        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(signalement.getFirestoreId());

        // Mapper le statut PostgreSQL vers le format Firestore
        String statutFirestore = mapStatutToFirestore(lastStatut.getStatutSignalement().getLibelle());

        Map<String, Object> updates = new HashMap<>();
        updates.put("dernier_statut", statutFirestore); // Utiliser le statut mappé

        ApiFuture<WriteResult> writeFuture = docRef.update(updates);
        writeFuture.get(); // attendre la fin de la mise à jour
    }

    public void updateDernierStatutInFirestore(){
        List<Signalement> signalements = signalementRepository.findAll();
        for (Signalement s : signalements) {
            try {
                if (s.getFirestoreId() == null) {
                    continue; // Skip if no Firestore ID
                }
                updateDernierStatutInFirestore(s);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFireStoreId(Signalement signalement, String firestoreId) {
        signalement.setFirestoreId(firestoreId);
        signalementRepository.save(signalement);
    }
}
