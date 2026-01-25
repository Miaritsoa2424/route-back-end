package com.route.controllers;

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

    public UserController(UserRepository userRepository, LoginService loginService) {
        this.userRepository = userRepository;
        this.loginService = loginService;
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Users> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id);
    }

    @PostMapping
    public Users createUser(@RequestBody Users user) {
        return userRepository.save(user);
    }

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
}
