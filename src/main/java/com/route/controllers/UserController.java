package com.route.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.route.models.Users;
import com.route.repositories.UserRepository;
import com.route.services.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final LoginService loginService;
    private final FirebaseAuth firebaseAuth;

    public UserController(UserRepository userRepository, LoginService loginService, FirebaseAuth firebaseAuth) {
        this.userRepository = userRepository;
        this.loginService = loginService;
        this.firebaseAuth = firebaseAuth;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Users> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id);
    }

    // @PostMapping
    // public Users createUser(@RequestBody Users user) {
    //     return userRepository.save(user);
    // }

    @PutMapping("/{id}")
    public Users updateUser(@PathVariable Integer id, @RequestBody Users userDetails) {
        Optional<Users> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setIdentifiant(userDetails.getIdentifiant());
            user.setPassword(userDetails.getPassword());
            user.setDateCreation(userDetails.getDateCreation());
            user.setDateDerniereConnexion(userDetails.getDateDerniereConnexion());
            user.setProfil(userDetails.getProfil());
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userRepository.deleteById(id);
    }

    @GetMapping("/blocked")
    public List<Users> getBlockedUsers() {
        return userRepository.findByBlockedTrue();
    }

    
    @PostMapping("/{userId}/unblock")
    public ResponseEntity<?> unblockUserById(@PathVariable Integer userId) {
        try {
            Users updated = loginService.unblockUser(userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "UserNotFound");
            error.put("message", e.getMessage());   
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        try {
            // Sauvegarder en base de données
            Users savedUser = userRepository.save(user);

            // Créer dans Firebase Auth
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(savedUser.getIdentifiant())  // Assumant que identifiant est l'email
                    .setPassword(savedUser.getPassword())
                    .setEmailVerified(false);

            UserRecord firebaseUser = firebaseAuth.createUser(request);

            return ResponseEntity.ok(savedUser);
        } catch (FirebaseAuthException e) {
            // En cas d'erreur, supprimer de la DB pour éviter l'incohérence
            if (user.getIdUser() != null) {
                userRepository.deleteById(user.getIdUser());
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", "FirebaseAuthError");
            error.put("message", "Erreur Firebase: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "DatabaseError");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}
