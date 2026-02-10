package com.route.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image")
    private Integer id;

    @Column(name = "date_ajout")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "lien", nullable = false, length = 255)
    private String lien;

    @Column(name = "id_firestore", length = 50)
    private String idFirestore;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_signalement", nullable = false)
    private Signalement signalement;

}
