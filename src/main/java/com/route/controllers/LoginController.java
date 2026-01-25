package com.route.controllers;

import com.route.models.LoginRequest;
import com.route.models.Users;
import com.route.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public Users login(@RequestBody LoginRequest loginRequest) {
        // Delegate authentication to service
        return loginService.login(loginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users newUser) {
        // Delegate registration to service and map service exceptions to HTTP responses
        try {
            Users savedUser = loginService.register(newUser);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "IdentifiantExists");
            error.put("message", "L'idendifiant existe déjà, veuillez en choisir un autre.");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/unblock/{identifiant}")
    public ResponseEntity<?> unblockUser(@PathVariable String identifiant) {
        try {
            Users updated = loginService.unblockUser(identifiant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "UserNotFound");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}