package com.route.services;

import com.route.models.Signalement;
import com.route.models.SignalementStatut;
import com.route.models.StatutSignalement;
import com.route.models.Users;
import com.route.repositories.SignalementRepository;
import com.route.repositories.SignalementStatutRepository;
import com.route.repositories.StatutSignalementRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SignalementStatutService {

    @Autowired
    private SignalementRepository signalementRepository;

    @Autowired
    private SignalementStatutRepository signalementStatutRepository;

    @Autowired
    private StatutSignalementRepository statutSignalementRepository;

    @Autowired
    private FirebaseNotificationService firebaseNotificationService;

    public SignalementStatutService(SignalementStatutRepository signalementStatutRepository, SignalementRepository signalementRepository) {
        this.signalementStatutRepository = signalementStatutRepository;
        this.signalementRepository = signalementRepository;
    }

    public List<SignalementStatut> getLatestStatuses() {
        return signalementStatutRepository.findLatestStatuses();
    }

    public SignalementStatut updateStatut(Integer signalement, Integer statutSignalement) {
        SignalementStatut signalementStatut = new SignalementStatut();
        Signalement sig = signalementRepository.findById(signalement).orElse(null);
        StatutSignalement statut = statutSignalementRepository.findById(statutSignalement).orElse(null);

        signalementStatut.setSignalement(sig);
        signalementStatut.setStatutSignalement(statut);
        signalementStatut.setDateStatut(LocalDateTime.now());
        signalementStatut = signalementStatutRepository.save(signalementStatut);

        // Envoyer une notification à l'utilisateur qui a fait le signalement
        if (sig != null && sig.getUser() != null && statut != null) {
            Users userToNotify = sig.getUser();
            String title = "Mise à jour de votre signalement";
            String body = "Le statut de votre signalement a été mis à jour : " + statut.getLibelle();

            firebaseNotificationService.sendNotificationToUser(userToNotify, title, body);
        }

        return signalementStatut;
    }
}
