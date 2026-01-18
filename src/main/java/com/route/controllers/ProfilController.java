package com.route.controllers;

import com.route.models.Profil;
import com.route.repositories.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/profils")
public class ProfilController {

    @Autowired
    private ProfilRepository profilRepository;

    @GetMapping
    public List<Profil> getAllProfils() {
        return profilRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Profil> getProfilById(@PathVariable Integer id) {
        return profilRepository.findById(id);
    }

    @PostMapping
    public Profil createProfil(@RequestBody Profil profil) {
        return profilRepository.save(profil);
    }

    @PutMapping("/{id}")
    public Profil updateProfil(@PathVariable Integer id, @RequestBody Profil profilDetails) {
        Optional<Profil> optionalProfil = profilRepository.findById(id);
        if (optionalProfil.isPresent()) {
            Profil profil = optionalProfil.get();
            profil.setLibelle(profilDetails.getLibelle());
            profil.setOrdre(profilDetails.getOrdre());
            return profilRepository.save(profil);
        } else {
            throw new RuntimeException("Profil not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteProfil(@PathVariable Integer id) {
        profilRepository.deleteById(id);
    }
}
