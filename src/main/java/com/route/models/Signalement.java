package com.route.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Entity
@Table(name = "signalement")
public class Signalement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_signalement")
    private Integer idSignalement;

    @Column(name = "surface", precision = 15, scale = 2)
    private BigDecimal surface;

    @Column(name = "budget", precision = 15, scale = 2)
    private BigDecimal budget;

    @JdbcTypeCode(SqlTypes.GEOMETRY)
    @JsonIgnore
    @Column(name = "localisation", nullable = false, columnDefinition = "geography(Point,4326)")
    private Point localisation;

    // Ajoutez ces getters (assumant que localisation est un Point)
    public double getLongitude() {
        if (localisation instanceof Point) {
            return ((Point) localisation).getX();
        }
        return 0.0;
    }

    public double getLatitude() {
        if (localisation instanceof Point) {
            return ((Point) localisation).getY();
        }
        return 0.0;
    }

    @ManyToOne
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;

    public Integer getIdSignalement() {
        return idSignalement;
    }

    public void setIdSignalement(Integer idSignalement) {
        this.idSignalement = idSignalement;
    }

    public BigDecimal getSurface() {
        return surface;
    }

    public void setSurface(BigDecimal surface) {
        this.surface = surface;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Point getLocalisation() {
        return localisation;
    }

    public void setLocalisation(Point localisation) {
        this.localisation = localisation;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}