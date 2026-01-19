package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
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

}
