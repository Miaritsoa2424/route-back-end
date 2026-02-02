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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Firestore firestore;

    public void syncFailedAttemptsFromFirebase() throws Exception {
        List<QueryDocumentSnapshot> documents = firestore.collection("tentative").get().get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            String email = document.getString("email");
            Long tentative = document.getLong("tentative");

            if (email != null && tentative != null) {
                Users user = userRepository.findByIdentifiant(email);
                if (user != null) {
                    user.setFailedAttempts(tentative.intValue());
                    if (tentative >= 3) {
                        user.setBlocked(true);
                    }
                    userRepository.save(user);
                }
            }
        }
    }
}