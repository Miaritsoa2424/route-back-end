package com.route.controllers;

import com.route.models.Users;
import com.route.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public java.util.List<Users> getBlockedUsers() {
        return userRepository.findByBlockedTrue();
    }
}
