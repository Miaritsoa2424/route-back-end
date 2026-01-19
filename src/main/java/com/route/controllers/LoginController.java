package com.route.controllers;

import com.route.models.LoginRequest;
import com.route.models.Users;
import com.route.repositories.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}
