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

    @Column(name = "failed_attempts", nullable = false, columnDefinition = "integer default 0")
    private Integer failedAttempts = 0;

    @Column(name = "blocked", nullable = false, columnDefinition = "boolean default false")
    private Boolean blocked = false;

    @Column(name = "id_firestore_tentative", length = 100)
    private String idFirestoreTentative;

    // Note: Le FCM token n'est pas stocké ici, il est géré directement par l'app mobile dans Firestore

    @PrePersist
    private void prePersist() {
        if (this.failedAttempts == null) {
            this.failedAttempts = 0;
        }
        if (this.blocked == null) {
            this.blocked = false;
        }
        if (this.dateCreation == null) {
            this.dateCreation = LocalDateTime.now();
        }
    }
}
