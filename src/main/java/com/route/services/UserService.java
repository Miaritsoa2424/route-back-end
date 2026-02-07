package com.route.services;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.route.models.Users;
import com.route.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Firestore firestore;
    private final FirebaseAuth firebaseAuth;

    public UserService(UserRepository userRepository, Firestore firestore, FirebaseAuth firebaseAuth) {
        this.userRepository = userRepository;
        this.firestore = firestore;
        this.firebaseAuth = firebaseAuth;
    }

    public void syncFailedAttemptsFromFirebase() throws Exception {
        System.err.println("---------- COMMENCE LA SYNC ----------");
        List<QueryDocumentSnapshot> documents = firestore.collection("tentative").get().get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            String email = document.getString("email");
            Long tentative = document.getLong("tentative");

            if (email != null && tentative != null) {
                if (email != null && tentative != null) {
                    Users user = userRepository.findByIdentifiant(email);
                    if (user != null) {
                        user.setFailedAttempts(tentative.intValue());
                        user.setIdFirestoreTentative(document.getId()); // Ajout pour définir l'ID du document Firebase
                        if (tentative >= 3) {
                            user.setBlocked(true);
                        }
                        userRepository.save(user);
                        System.err.println("Updated user " + email + " with failed attempts: " + tentative);
                    }
                }
            }
        }
    }

    public void saveUserToFirestore(Users user) throws Exception {
        try {
            // 1. Créer l'utilisateur dans Firebase Authentication via Admin SDK
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(user.getIdentifiant())
                    .setPassword(user.getPassword()) // À remplacer par un mot de passe approprié
                    .setDisplayName(user.getProfil() != null ? user.getProfil().getLibelle() : "")
                    .setEmailVerified(false);

            try {
                UserRecord userRecord = firebaseAuth.createUser(request);
                System.err.println("User created in Firebase Auth with UID: " + userRecord.getUid());
            } catch (Exception authError) {
                System.err.println("Warning: Could not create user in Firebase Auth: " + authError.getMessage());
                // Continue même si l'authentification échoue (peut-être que l'utilisateur existe déjà)
            }

            // 2. Enregistrer les données utilisateur dans Firestore
            Map<String, Object> userData = new HashMap<>();
            userData.put("mail", user.getIdentifiant());
            userData.put("dateCreation", user.getDateCreation());
            userData.put("blocked", false);
            userData.put("failedAttempts", 0);
            userData.put("profilId", user.getProfil() != null ? user.getProfil().getLibelle() : null);

            firestore.collection("user").document(user.getIdentifiant()).set(userData).get();
            System.err.println("User " + user.getIdentifiant() + " saved to Firestore");

        } catch (Exception e) {
            throw new Exception("Erreur d'envoie de +'" + user.getIdentifiant() + "': " + e.getMessage());
        }
    }

    public Map<String, Object> getUserFromFirestore(String email) throws Exception {
        DocumentSnapshot document = firestore.collection("user").document(email).get().get();

        if (document.exists()) {
            return document.getData();
        }
        return null;
    }

    public boolean checkIfUserExistInDb(String email) {
        Users user = userRepository.findByIdentifiant(email);
        return user != null;
    }

    public void syncUsers() throws Exception {
        System.err.println("---------- COMMENCE LA SYNC DES USERS ----------");

        // Récupérer tous les utilisateurs de la base de données
        List<Users> allUsers = userRepository.findAll();
        int syncedCount = 0;
        int alreadyExistCount = 0;

        for (Users user : allUsers) {
            String email = user.getIdentifiant();

            // Vérifier si l'utilisateur existe dans Firestore
            Map<String, Object> firestoreUser = getUserFromFirestore(email);

            if (firestoreUser == null) {
                // L'utilisateur n'existe pas dans Firestore, on l'insère
                saveUserToFirestore(user);
                syncedCount++;
                System.err.println("User " + email + " synchronized to Firestore");
            } else {
                alreadyExistCount++;
                System.err.println("User " + email + " already exists in Firestore");
            }
        }

        System.err.println("---------- SYNC TERMINÉE ----------");
        System.err.println("Utilisateurs synchronisés: " + syncedCount);
        System.err.println("Utilisateurs déjà présents: " + alreadyExistCount);
    }
}