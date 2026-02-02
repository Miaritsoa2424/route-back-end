package com.route.controllers;

import com.route.models.LoginRequest;
import com.route.models.Users;
import com.route.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Users user = loginService.login(loginRequest);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getIdentifiant());
            
            // Prepare response with user and token
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "LoginFailed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
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