package com.route.controllers;

import com.route.dto.EntreprisePrixDto;
import com.route.models.Entreprise;
import com.route.models.PrixEntreprise;
import com.route.repositories.EntrepriseRepository;
import com.route.services.EntrepriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/entreprises")
public class EntrepriseController {

    private final EntrepriseRepository entrepriseRepository;
    
    @Autowired
    private EntrepriseService entrepriseService;

    public EntrepriseController(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    @GetMapping
    public ResponseEntity<List<EntreprisePrixDto>> getAllEntreprises() {
        List<EntreprisePrixDto> entreprises = entrepriseService.getAllEntreprisesWithPrix();
        return ResponseEntity.ok(entreprises);
    }

    @GetMapping("/{id}")
    public Optional<Entreprise> getEntrepriseById(@PathVariable Integer id) {
        return entrepriseRepository.findById(id);
    }

    @PostMapping
    public Entreprise createEntreprise(@RequestBody Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    @PutMapping("/{id}")
    public Entreprise updateEntreprise(@PathVariable Integer id, @RequestBody Entreprise entrepriseDetails) {
        Optional<Entreprise> optionalEntreprise = entrepriseRepository.findById(id);
        if (optionalEntreprise.isPresent()) {
            Entreprise entreprise = optionalEntreprise.get();
            entreprise.setNom(entrepriseDetails.getNom());
            return entrepriseRepository.save(entreprise);
        } else {
            throw new RuntimeException("Entreprise not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteEntreprise(@PathVariable Integer id) {
        entrepriseRepository.deleteById(id);
    }

    @PostMapping("/{id}/prix")
    public ResponseEntity<PrixEntreprise> addPrixToEntreprise(
            @PathVariable Integer id,
            @RequestBody Map<String, Double> request) {
        try {
            Double prix = request.get("prix");
            if (prix == null) {
                return ResponseEntity.badRequest().build();
            }
            PrixEntreprise nouveauPrix = entrepriseService.addPrixToEntreprise(id, prix);
            return ResponseEntity.ok(nouveauPrix);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
