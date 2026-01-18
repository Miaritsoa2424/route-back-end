package com.route.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "avancement")
public class Avancement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avancement")
    private Integer idAvancement;

    @Column(name = "date_avancement", nullable = false)
    private LocalDate dateAvancement;

    @Column(name = "avancement", nullable = false, precision = 15, scale = 2)
    private BigDecimal avancement;

    @ManyToOne
    @JoinColumn(name = "id_signalement", nullable = false)
    private Signalement signalement;

    // Getters and Setters
    public Integer getIdAvancement() {
        return idAvancement;
    }

    public void setIdAvancement(Integer idAvancement) {
        this.idAvancement = idAvancement;
    }

    public LocalDate getDateAvancement() {
        return dateAvancement;
    }

    public void setDateAvancement(LocalDate dateAvancement) {
        this.dateAvancement = dateAvancement;
    }

    public BigDecimal getAvancement() {
        return avancement;
    }

    public void setAvancement(BigDecimal avancement) {
        this.avancement = avancement;
    }

    public Signalement getSignalement() {
        return signalement;
    }

    public void setSignalement(Signalement signalement) {
        this.signalement = signalement;
    }
}
