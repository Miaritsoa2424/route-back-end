package com.route.services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.route.models.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseNotificationService.class);

    /**
     * Envoie une notification push à un utilisateur.
     * Le FCM token est récupéré depuis Firestore (pas stocké dans la base PostgreSQL).
     *
     * Workflow:
     * 1. L'app mobile génère un FCM token et le stocke dans Firestore (collection "fcmTokens", document = email)
     * 2. Spring Boot lit ce token depuis Firestore via l'email de l'utilisateur
     * 3. Spring Boot envoie la notification via FCM
     * 4. L'app mobile reçoit la notification
     *
     * @param user  L'utilisateur destinataire
     * @param title Le titre de la notification
     * @param body  Le corps de la notification
     */
    public void sendNotificationToUser(Users user, String title, String body) {
        try {
            // 1. Récupérer le FCM token depuis Firestore via l'email (identifiant)
            String email = user.getIdentifiant();
            String fcmToken = getFcmTokenFromFirestore(email);

            if (fcmToken == null || fcmToken.isEmpty()) {
                logger.warn("Aucun FCM token trouvé pour l'utilisateur {}", email);
                return;
            }

            // 2. Construire la notification
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 3. Construire le message avec le token récupéré de Firestore
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .build();

            // 4. Envoyer via FCM
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Notification envoyée avec succès à {}. Response: {}", email, response);

        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erreur lors de la récupération du FCM token pour {}", user.getIdentifiant(), e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification à {}", user.getIdentifiant(), e);
        }
    }

    /**
     * Récupère le FCM token depuis Firestore.
     *
     * Structure attendue dans Firestore:
     * Collection: "fcmTokens"
     * Document ID: email de l'utilisateur
     * Champ: "fcmToken"
     *
     * @param email L'email de l'utilisateur
     * @return Le FCM token ou null si non trouvé
     */
    private String getFcmTokenFromFirestore(String email) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot doc = db.collection("fcmTokens")
                .document(email)
                .get()
                .get(); // .get() bloquant pour obtenir le résultat

        if (doc.exists()) {
            return doc.getString("fcmToken");
        }

        return null;
    }
}