CREATE TABLE profil(
                       id_profil SERIAL,
                       libelle VARCHAR(50)  NOT NULL,
                       ordre INTEGER NOT NULL,
                       PRIMARY KEY(id_profil)
);

CREATE TABLE entreprise(
                           id_entreprise SERIAL,
                           nom VARCHAR(50)  NOT NULL,
                           PRIMARY KEY(id_entreprise)
);

CREATE TABLE statut_signalement(
                                   id_statut SERIAL,
                                   libelle VARCHAR(50)  NOT NULL,
                                   ordre INTEGER NOT NULL,
                                   PRIMARY KEY(id_statut)
);

CREATE TABLE users(
                      id_user SERIAL,
                      identifiant VARCHAR(50)  NOT NULL,
                      password VARCHAR(50)  NOT NULL,
                      date_creation TIMESTAMP NOT NULL,
                      date_derniere_connexion TIMESTAMP,
                      id_profil INTEGER NOT NULL,
                      failed_attempts INTEGER DEFAULT 0,
                      blocked BOOLEAN DEFAULT FALSE,
                      id_firestore_tentative VARCHAR(100),
                      PRIMARY KEY(id_user),
                      FOREIGN KEY(id_profil) REFERENCES profil(id_profil)
);

CREATE TABLE signalement(
                            id_signalement SERIAL,
                            surface NUMERIC(15,2)  ,
                            budget NUMERIC(15,2)  ,
                            localisation GEOGRAPHY NOT NULL,
                            id_entreprise INTEGER,
                            id_user INTEGER NOT NULL,
                            PRIMARY KEY(id_signalement),
                            FOREIGN KEY(id_entreprise) REFERENCES entreprise(id_entreprise),
                            FOREIGN KEY(id_user) REFERENCES users(id_user)
);

CREATE TABLE signalement_statut(
                                   id_signalement_statut SERIAL,
                                   date_statut TIMESTAMP NOT NULL,
                                   id_user INTEGER NOT NULL,
                                   id_statut INTEGER NOT NULL,
                                   id_signalement INTEGER NOT NULL,
                                   PRIMARY KEY(id_signalement_statut),
                                   FOREIGN KEY(id_user) REFERENCES users(id_user),
                                   FOREIGN KEY(id_statut) REFERENCES statut_signalement(id_statut),
                                   FOREIGN KEY(id_signalement) REFERENCES signalement(id_signalement)
);

CREATE TABLE avancement(
                           id_avancement SERIAL,
                           date_avancement DATE NOT NULL,
                           avancement NUMERIC(15,2)   NOT NULL,
                           id_signalement INTEGER NOT NULL,
                           PRIMARY KEY(id_avancement),
                           FOREIGN KEY(id_signalement) REFERENCES signalement(id_signalement)
);

CREATE TABLE log(
                    id_tentative_log SERIAL,
                    date_tentative TIMESTAMP NOT NULL,
                    succes BOOLEAN NOT NULL,
                    id_user INTEGER NOT NULL,
                    PRIMARY KEY(id_tentative_log),
                    FOREIGN KEY(id_user) REFERENCES users(id_user)
);


ALTER COLUMN lien TYPE VARCHAR(255);

ALTER TABLE image 
ALTER COLUMN lien TYPE VARCHAR(255);