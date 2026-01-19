package com.route.controllers;

import com.route.models.StatutSignalement;
import com.route.repositories.StatutSignalementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/statut-signalements")
public class StatutSignalementController {

    private final StatutSignalementRepository statutSignalementRepository;

    public StatutSignalementController(StatutSignalementRepository statutSignalementRepository) {
        this.statutSignalementRepository = statutSignalementRepository;
    }

    @GetMapping
    public List<StatutSignalement> getAllStatutSignalements() {
        return statutSignalementRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<StatutSignalement> getStatutSignalementById(@PathVariable Integer id) {
        return statutSignalementRepository.findById(id);
    }

    @PostMapping
    public StatutSignalement createStatutSignalement(@RequestBody StatutSignalement statutSignalement) {
        return statutSignalementRepository.save(statutSignalement);
    }

    @PutMapping("/{id}")
    public StatutSignalement updateStatutSignalement(@PathVariable Integer id, @RequestBody StatutSignalement statutSignalementDetails) {
        Optional<StatutSignalement> optionalStatutSignalement = statutSignalementRepository.findById(id);
        if (optionalStatutSignalement.isPresent()) {
            StatutSignalement statutSignalement = optionalStatutSignalement.get();
            statutSignalement.setLibelle(statutSignalementDetails.getLibelle());
            statutSignalement.setOrdre(statutSignalementDetails.getOrdre());
            return statutSignalementRepository.save(statutSignalement);
        } else {
            throw new RuntimeException("StatutSignalement not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteStatutSignalement(@PathVariable Integer id) {
        statutSignalementRepository.deleteById(id);
    }
}
