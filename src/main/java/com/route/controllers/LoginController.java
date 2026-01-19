package com.route.controllers;

import com.route.models.LoginRequest;
import com.route.models.Users;
import com.route.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LoginController {
    private final UserRepository userRepository;

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Users login(@RequestBody LoginRequest loginRequest) {
        Users user = userRepository.findByIdentifiantAndPassword(loginRequest.getIdentifiant(), loginRequest.getPassword());
        if (user != null) {
            return user;
        } else {
            throw new RuntimeException("Identifiants incorrects");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users newUser) {
        // Vérifier si l'identifiant existe déjà
        if (userRepository.findByIdentifiant(newUser.getIdentifiant()) != null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "IdentifiantExists");
            error.put("message", "L'idendifiant existe déjà, veuillez en choisir un autre.");
            return ResponseEntity.badRequest().body(error);
        }
        // Définir la date de création si elle n'est pas fournie
        if (newUser.getDateCreation() == null) {
            newUser.setDateCreation(LocalDateTime.now());
        }
        // La date de dernière connexion peut être laissée nulle
        Users savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }
}
