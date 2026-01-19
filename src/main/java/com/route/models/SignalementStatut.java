package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "signalement_statut")
public class SignalementStatut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signalement_statut")
    private Integer idSignalementStatut;

    @Column(name = "date_statut", nullable = false)
    private LocalDateTime dateStatut;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "id_statut", nullable = false)
    private StatutSignalement statutSignalement;

    @ManyToOne
    @JoinColumn(name = "id_signalement", nullable = false)
    private Signalement signalement;

}
