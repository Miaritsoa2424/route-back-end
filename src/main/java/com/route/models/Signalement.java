package com.route.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;

@Getter
@Setter
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
}