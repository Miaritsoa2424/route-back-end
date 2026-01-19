package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "identifiant", nullable = false, length = 50)
    private String identifiant;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_derniere_connexion")
    private LocalDateTime dateDerniereConnexion;

    @ManyToOne
    @JoinColumn(name = "id_profil", nullable = false)
    private Profil profil;

}
