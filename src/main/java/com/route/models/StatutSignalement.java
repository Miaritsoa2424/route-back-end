package com.route.models;

import jakarta.persistence.*;

@Entity
@Table(name = "statut_signalement")
public class StatutSignalement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_statut")
    private Integer idStatut;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

    // Getters and Setters
    public Integer getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(Integer idStatut) {
        this.idStatut = idStatut;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
