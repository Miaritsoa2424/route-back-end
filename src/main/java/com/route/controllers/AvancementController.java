package com.route.controllers;

import com.route.models.Avancement;
import com.route.repositories.AvancementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/avancements")
public class AvancementController {

    @Autowired
    private AvancementRepository avancementRepository;

    @GetMapping
    public List<Avancement> getAllAvancements() {
        return avancementRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Avancement> getAvancementById(@PathVariable Integer id) {
        return avancementRepository.findById(id);
    }

    @PostMapping
    public Avancement createAvancement(@RequestBody Avancement avancement) {
        return avancementRepository.save(avancement);
    }

    @PutMapping("/{id}")
    public Avancement updateAvancement(@PathVariable Integer id, @RequestBody Avancement avancementDetails) {
        Optional<Avancement> optionalAvancement = avancementRepository.findById(id);
        if (optionalAvancement.isPresent()) {
            Avancement avancement = optionalAvancement.get();
            avancement.setDateAvancement(avancementDetails.getDateAvancement());
            avancement.setAvancement(avancementDetails.getAvancement());
            avancement.setSignalement(avancementDetails.getSignalement());
            return avancementRepository.save(avancement);
        } else {
            throw new RuntimeException("Avancement not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteAvancement(@PathVariable Integer id) {
        avancementRepository.deleteById(id);
    }
}
