package com.route.controllers;

import com.route.models.Image;
import com.route.models.Signalement;
import com.route.repositories.ImageRepository;
import com.route.repositories.SignalementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")  // ← AJOUTER CETTE LIGNE
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private SignalementRepository signalementRepository;

    @GetMapping("/signalements/{id}/images")
    public List<Image> getImagesBySignalement(@PathVariable("id") Integer id) {
        Signalement signalement = signalementRepository.findById(id).orElse(null);
        if (signalement == null) {
            return List.of();
        }
        return imageRepository.findBySignalement(signalement);
    }
}