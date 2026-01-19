package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "profil")
public class Profil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profil")
    private Integer idProfil;

    @Column(name = "libelle", nullable = false, length = 50)
    private String libelle;

    @Column(name = "ordre", nullable = false)
    private Integer ordre;

}
