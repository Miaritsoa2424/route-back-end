package com.route.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "prix_entreprise")
public class PrixEntreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "date_prix")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "id_entreprise",nullable = false)
    private Entreprise entreprise;
    
}
