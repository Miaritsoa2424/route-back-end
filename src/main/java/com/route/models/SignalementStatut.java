package com.route.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_statut", nullable = false)
    private StatutSignalement statutSignalement;

    @ManyToOne
    @JoinColumn(name = "id_signalement", nullable = false)
    private Signalement signalement;

    // Getters and Setters
    public Integer getIdSignalementStatut() {
        return idSignalementStatut;
    }

    public void setIdSignalementStatut(Integer idSignalementStatut) {
        this.idSignalementStatut = idSignalementStatut;
    }

    public LocalDateTime getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(LocalDateTime dateStatut) {
        this.dateStatut = dateStatut;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public StatutSignalement getStatutSignalement() {
        return statutSignalement;
    }

    public void setStatutSignalement(StatutSignalement statutSignalement) {
        this.statutSignalement = statutSignalement;
    }

    public Signalement getSignalement() {
        return signalement;
    }

    public void setSignalement(Signalement signalement) {
        this.signalement = signalement;
    }
}
