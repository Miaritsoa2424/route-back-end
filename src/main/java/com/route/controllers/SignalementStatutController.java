package com.route.controllers;

import com.route.models.SignalementStatut;
import com.route.models.Users;
import com.route.repositories.SignalementStatutRepository;
import com.route.services.FirebaseNotificationService;
import com.route.services.SignalementStatutService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/signalement-statuts")
public class SignalementStatutController {

    private final SignalementStatutRepository signalementStatutRepository;
    private final SignalementStatutService signalementStatutService;
    public final FirebaseNotificationService firebaseNotificationService;

    public SignalementStatutController(SignalementStatutRepository signalementStatutRepository, SignalementStatutService signalementStatutService, FirebaseNotificationService firebaseNotificationService) {
        this.signalementStatutRepository = signalementStatutRepository;
        this.signalementStatutService = signalementStatutService;
        this.firebaseNotificationService = firebaseNotificationService;
    }

    @GetMapping
    public List<SignalementStatut> getAllSignalementStatuts() {
        return signalementStatutRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<SignalementStatut> getSignalementStatutById(@PathVariable Integer id) {
        return signalementStatutRepository.findById(id);
    }

    @PostMapping
    public SignalementStatut createSignalementStatut(@RequestBody SignalementStatut signalementStatut) {
        SignalementStatut saved = signalementStatutRepository.save(signalementStatut);
        Users user = saved.getSignalement().getUser();
        firebaseNotificationService.sendNotificationToUser(user, "Mise à jour du statut", "Votre signalement a été mis à jour.");
        return signalementStatutRepository.save(signalementStatut);
    }

    @PutMapping("/{id}")
    public SignalementStatut updateSignalementStatut(@PathVariable Integer id, @RequestBody SignalementStatut signalementStatutDetails) {
        Optional<SignalementStatut> optionalSignalementStatut = signalementStatutRepository.findById(id);
        if (optionalSignalementStatut.isPresent()) {
            SignalementStatut signalementStatut = optionalSignalementStatut.get();
            signalementStatut.setDateStatut(signalementStatutDetails.getDateStatut());
            signalementStatut.setUser(signalementStatutDetails.getUser());
            signalementStatut.setStatutSignalement(signalementStatutDetails.getStatutSignalement());
            signalementStatut.setSignalement(signalementStatutDetails.getSignalement());
            return signalementStatutRepository.save(signalementStatut);
        } else {
            throw new RuntimeException("SignalementStatut not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteSignalementStatut(@PathVariable Integer id) {
        signalementStatutRepository.deleteById(id);
    }

    @GetMapping("/latest")
    public List<SignalementStatut> getLatestStatuses() {
        return signalementStatutService.getLatestStatuses();
    }
}
