package com.route.controllers;

import com.route.repositories.AvancementRepository;
import com.route.repositories.SignalementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class StatController {

    private final SignalementRepository signalementRepository;
    private final AvancementRepository avancementRepository;

    public StatController(SignalementRepository signalementRepository, AvancementRepository avancementRepository) {
        this.signalementRepository = signalementRepository;
        this.avancementRepository = avancementRepository;
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nbSignalement", signalementRepository.countSignalements());
        stats.put("surfaceTotal", signalementRepository.sumSurface());
        stats.put("budgetTotal", signalementRepository.sumBudget());
        stats.put("avancementGlobal", avancementRepository.avgAvancement());
        return stats;
    }
}
