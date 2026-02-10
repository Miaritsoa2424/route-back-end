package com.route.FirebaseDTO;

import lombok.Data;

@Data
public class SignalementDto {

    private String id;
    private String idFirestore;
    private Integer idSignalement;
    private Double budget;
    private Integer avancement;
    private Integer surface;

    private LocalisationDto localisation;

    private String user;
    private String dernierStatut;
    private String entreprise;


    public Integer getIdSignalement() {
        return idSignalement;
    }
    
    public void setIdSignalement(Integer idSignalement) {
        this.idSignalement = idSignalement;
    }
}
