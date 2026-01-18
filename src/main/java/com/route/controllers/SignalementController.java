package com.route.controllers;

import com.route.models.Signalement;
import com.route.repositories.SignalementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/signalements")
public class SignalementController {

    @Autowired
    private SignalementRepository signalementRepository;

    @GetMapping
    public List<Signalement> getAllSignalements() {
        return signalementRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Signalement> getSignalementById(@PathVariable Integer id) {
        return signalementRepository.findById(id);
    }

    @PostMapping
    public Signalement createSignalement(@RequestBody Signalement signalement) {
        return signalementRepository.save(signalement);
    }

    @PutMapping("/{id}")
    public Signalement updateSignalement(@PathVariable Integer id, @RequestBody Signalement signalementDetails) {
        Optional<Signalement> optionalSignalement = signalementRepository.findById(id);
        if (optionalSignalement.isPresent()) {
            Signalement signalement = optionalSignalement.get();
            signalement.setSurface(signalementDetails.getSurface());
            signalement.setBudget(signalementDetails.getBudget());
            signalement.setLocalisation(signalementDetails.getLocalisation());
            signalement.setEntreprise(signalementDetails.getEntreprise());
            signalement.setUser(signalementDetails.getUser());
            return signalementRepository.save(signalement);
        } else {
            throw new RuntimeException("Signalement not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteSignalement(@PathVariable Integer id) {
        signalementRepository.deleteById(id);
    }
}
