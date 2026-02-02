package com.route.controllers;

import com.route.FirebaseDTO.LocalisationDto;
import com.route.FirebaseDTO.SignalementDto;
import com.route.models.Avancement;
import com.route.models.Signalement;
import com.route.models.SignalementStatut;
import com.route.repositories.AvancementRepository;
import com.route.repositories.SignalementRepository;
import com.route.repositories.SignalementStatutRepository;
import com.route.services.SignalementService;

import com.route.services.UserService;
import io.grpc.netty.shaded.io.netty.util.Signal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/signalements")
public class SignalementController {

    private final SignalementRepository signalementRepository;

    private final SignalementService signalementService;

    private final AvancementRepository avancementRepository;

    private final SignalementStatutRepository signalementStatutRepository;

    private final UserService userService;

    public SignalementController(SignalementRepository signalementRepository, SignalementService signalementService, AvancementRepository avancementRepository, SignalementStatutRepository signalementStatutRepository, UserService userService) {
        this.signalementRepository = signalementRepository;
        this.signalementService = signalementService;
        this.avancementRepository = avancementRepository;
        this.signalementStatutRepository = signalementStatutRepository;
        this.userService = userService;
    }

    @PostMapping("/sync")
    public String syncToFirebase() {
        try {
            List<Signalement> signalements = signalementRepository.findByFirestoreIdIsNull();
            List<SignalementDto> dtos = new ArrayList<>();
            for (Signalement s : signalements) {
                SignalementDto dto = new SignalementDto();
                dto.setId(s.getIdSignalement().toString()); // Use ID as string
                dto.setBudget(s.getBudget() != null ? s.getBudget().intValue() : 0);
                dto.setSurface(s.getSurface() != null ? s.getSurface().intValue() : 0);
                // Compute avancement: Sum latest Avancement.avancement for this signalement
                List<Avancement> avancements = avancementRepository.findBySignalement(s);
                dto.setAvancement(avancements.stream().mapToInt(a -> a.getAvancement().intValue()).sum()); // Or take latest
                // Compute dernierStatut: Latest StatutSignalement.libelle for this signalement
                List<SignalementStatut> statuts = signalementStatutRepository.findBySignalement(s);
                dto.setDernierStatut(statuts.stream().max((a,b) -> a.getDateStatut().compareTo(b.getDateStatut())).map(ss -> ss.getStatutSignalement().getLibelle()).orElse("Unknown"));
                dto.setUser(s.getUser().getIdentifiant()); // Assuming User has identifiant
                dto.setEntreprise(s.getEntreprise() != null ? s.getEntreprise().getNom() : null);
                // Localisation
                if (s.getLocalisation() != null) {
                    LocalisationDto loc = new LocalisationDto();
                    loc.setLatitude(s.getLatitude());
                    loc.setLongitude(s.getLongitude());
                    dto.setLocalisation(loc);
                }
                dtos.add(dto);
            }
            signalementService.syncFromFirebaseToDB();
            signalementService.syncAllSignalementsToFirebase(dtos);
            signalementService.updateDernierStatutInFirestore();
            userService.syncFailedAttemptsFromFirebase();

            return "Sync completed";
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return "Sync failed: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Sync failed: " + e.getMessage();
        }
    }

    @PostMapping("/update-firestore-status")
    public String updateFirestoreStatus() throws ExecutionException, InterruptedException {
       signalementService.updateDernierStatutInFirestore();
        return "Update completed";
    }

    @PostMapping("/sync-from-firebase")
    public String syncFromFirebase() throws ExecutionException, InterruptedException {
        return signalementService.syncFromFirebaseToDB();
    }

    @GetMapping("/dto")
    public List<SignalementDto> listSignalementsDto() throws ExecutionException, InterruptedException {
        // This method should ideally call a service to fetch data from Firestore
        // For simplicity, returning an empty list here
        return signalementService.listSignalements();
    }

    @GetMapping
    public List<Signalement> getAllSignalements() {
        return signalementRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Signalement> getSignalementById(@PathVariable Integer id) {
        return signalementRepository.findById(id);
    }

    @PostMapping
    public Signalement createSignalement(@RequestBody Signalement signalement) {
        return signalementRepository.save(signalement);
    }

    @PutMapping("/{id}")
    public Signalement updateSignalement(@PathVariable Integer id, @RequestBody Signalement signalementDetails) {
        Optional<Signalement> optionalSignalement = signalementRepository.findById(id);
        if (optionalSignalement.isPresent()) {
            Signalement signalement = optionalSignalement.get();
            signalement.setSurface(signalementDetails.getSurface());
            signalement.setBudget(signalementDetails.getBudget());
            signalement.setLocalisation(signalementDetails.getLocalisation());
            signalement.setEntreprise(signalementDetails.getEntreprise());
            signalement.setUser(signalementDetails.getUser());
            return signalementRepository.save(signalement);
        } else {
            throw new RuntimeException("Signalement not found with id " + id);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteSignalement(@PathVariable Integer id) {
        signalementRepository.deleteById(id);
    }
}
