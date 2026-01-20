package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
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

    // Make the association optional/nullable so we can log attempts where the user does not exist
    @ManyToOne(optional = true)
    @JoinColumn(name = "id_user", nullable = true)
    private Users user;

}
