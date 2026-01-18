package com.route.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tentative")
public class Tentative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tentative_log")
    private Integer idTentativeLog;

    @Column(name = "date_tentative", nullable = false)
    private LocalDateTime dateTentative;

    @Column(name = "succes", nullable = false)
    private Boolean succes;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    // Getters and Setters
    public Integer getIdTentativeLog() {
        return idTentativeLog;
    }

    public void setIdTentativeLog(Integer idTentativeLog) {
        this.idTentativeLog = idTentativeLog;
    }

    public LocalDateTime getDateTentative() {
        return dateTentative;
    }

    public void setDateTentative(LocalDateTime dateTentative) {
        this.dateTentative = dateTentative;
    }

    public Boolean getSucces() {
        return succes;
    }

    public void setSucces(Boolean succes) {
        this.succes = succes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
