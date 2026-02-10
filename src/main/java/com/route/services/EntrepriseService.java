package com.route.services;

import com.route.dto.EntreprisePrixDto;
import com.route.models.Entreprise;
import com.route.models.PrixEntreprise;
import com.route.repositories.EntrepriseRepository;
import com.route.repositories.PrixEntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntrepriseService {

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @Autowired
    private PrixEntrepriseRepository prixEntrepriseRepository;

    public EntreprisePrixDto convertToDto(Entreprise entreprise) {
        EntreprisePrixDto dto = new EntreprisePrixDto();
        dto.setIdEntreprise(entreprise.getIdEntreprise());
        dto.setNom(entreprise.getNom());

        // Récupérer tous les prix de l'entreprise
        List<PrixEntreprise> prixList = prixEntrepriseRepository
                .findByEntrepriseIdEntrepriseOrderByDateDesc(entreprise.getIdEntreprise());

        // Convertir les prix en PrixDto
        List<EntreprisePrixDto.PrixDto> prixDtoList = prixList.stream()
                .map(prix -> new EntreprisePrixDto.PrixDto(
                        prix.getId(),
                        prix.getPrix(),
                        prix.getDate()
                ))
                .collect(Collectors.toList());

        dto.setPrix(prixDtoList);

        // Définir le dernier prix (le premier de la liste triée par date DESC)
        if (!prixDtoList.isEmpty()) {
            dto.setDernierPrix(prixDtoList.get(0).getPrix());
        }

        return dto;
    }

    public List<EntreprisePrixDto> getAllEntreprisesWithPrix() {
        List<Entreprise> entreprises = entrepriseRepository.findAll();
        return entreprises.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EntreprisePrixDto getEntrepriseWithPrix(Integer idEntreprise) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));
        return convertToDto(entreprise);
    }

    public PrixEntreprise addPrixToEntreprise(Integer idEntreprise, Double prix) {
        Entreprise entreprise = entrepriseRepository.findById(idEntreprise)
                .orElseThrow(() -> new RuntimeException("Entreprise not found with id " + idEntreprise));
        
        PrixEntreprise prixEntreprise = new PrixEntreprise();
        prixEntreprise.setPrix(prix);
        prixEntreprise.setDate(java.time.LocalDateTime.now());
        prixEntreprise.setEntreprise(entreprise);
        
        return prixEntrepriseRepository.save(prixEntreprise);
    }
}
