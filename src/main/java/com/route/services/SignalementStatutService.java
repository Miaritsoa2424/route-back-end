package com.route.services;

import com.route.models.Signalement;
import com.route.models.SignalementStatut;
import com.route.models.StatutSignalement;
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

    public SignalementStatutService(SignalementStatutRepository signalementStatutRepository, SignalementRepository signalementRepository) {
        this.signalementStatutRepository = signalementStatutRepository;
        this.signalementRepository = signalementRepository;
    }

    public List<SignalementStatut> getLatestStatuses() {
        return signalementStatutRepository.findLatestStatuses();
    }

    public SignalementStatut updateStatut(Integer signalement, Integer statutSignalement) {
        SignalementStatut signalementStatut = new SignalementStatut();
        signalementStatut.setSignalement(signalementRepository.findById(signalement).orElse(null));
        signalementStatut.setStatutSignalement(statutSignalementRepository.findById(statutSignalement).orElse(null));
        signalementStatut.setDateStatut(LocalDateTime.now());
        signalementStatut = signalementStatutRepository.save(signalementStatut);
        return signalementStatut;
    }
}
