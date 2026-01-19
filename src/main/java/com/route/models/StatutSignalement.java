package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}
