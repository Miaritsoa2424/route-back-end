package com.route.controllers;

import com.route.models.Tentative;
import com.route.repositories.TentativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/tentatives")
public class TentativeController {

    @Autowired
    private TentativeRepository tentativeRepository;

    @GetMapping
    public List<Tentative> getAllTentatives() {
        return tentativeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Tentative> getTentativeById(@PathVariable Integer id) {
        return tentativeRepository.findById(id);
    }

    @PostMapping
    public Tentative createTentative(@RequestBody Tentative tentative) {
        return tentativeRepository.save(tentative);
    }

    @PutMapping("/{id}")
    public Tentative updateTentative(@PathVariable Integer id, @RequestBody Tentative tentativeDetails) {
        Optional<Tentative> optionalTentative = tentativeRepository.findById(id);
        if (optionalTentative.isPresent()) {
            Tentative tentative = optionalTentative.get();
            tentative.setDateTentative(tentativeDetails.getDateTentative());
            tentative.setSucces(tentativeDetails.getSucces());
            tentative.setUser(tentativeDetails.getUser());
            return tentativeRepository.save(tentative);
        } else {
            throw new RuntimeException("Tentative not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteTentative(@PathVariable Integer id) {
        tentativeRepository.deleteById(id);
    }
}
