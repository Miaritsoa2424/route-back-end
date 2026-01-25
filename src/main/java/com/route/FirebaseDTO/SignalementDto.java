package com.route.FirebaseDTO;

import lombok.Data;

@Data
public class SignalementDto {

    private String id;
    private Integer budget;
    private Integer avancement;
    private Integer surface;

    private LocalisationDto localisation;

    private String user;
    private String dernierStatut;
    private String entreprise;
}
