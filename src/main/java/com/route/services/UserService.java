package com.route.services;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.route.models.Users;
import com.route.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Firestore firestore;

    public UserService(UserRepository userRepository, Firestore firestore) {
        this.userRepository = userRepository;
        this.firestore = firestore;
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
                        user.setIdFirestoreTentative(document.getId());  // Ajout pour définir l'ID du document Firebase
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
}