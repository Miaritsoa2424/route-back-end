package com.route.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.route.models.Users;
import org.springframework.stereotype.Service;

@Service
public class FirebaseNotificationService {

    public void sendNotificationToUser(Users user, String title, String body) {
        if (user.getFcmToken() != null) {  // Assumez que User a un champ fcmToken
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(notification)
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                // Gérer l'erreur (log ou throw)
                e.printStackTrace();
            }
        }
    }
}