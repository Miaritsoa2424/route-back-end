package com.route.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntreprisePrixDto {
    private Integer idEntreprise;
    private String nom;
    private List<PrixDto> prix;
    private Double dernierPrix;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrixDto {
        private Integer id;
        private Double prix;
        private LocalDateTime datePrix;
    }
}
