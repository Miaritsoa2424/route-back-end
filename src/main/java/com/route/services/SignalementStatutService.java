package com.route.services;

import com.route.models.SignalementStatut;
import com.route.repositories.SignalementStatutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignalementStatutService {

    private final SignalementStatutRepository signalementStatutRepository;

    public SignalementStatutService(SignalementStatutRepository signalementStatutRepository) {
        this.signalementStatutRepository = signalementStatutRepository;
    }

    public List<SignalementStatut> getLatestStatuses() {
        return signalementStatutRepository.findLatestStatuses();
    }
}
