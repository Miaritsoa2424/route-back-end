-- Insertion dans profil
INSERT INTO profil (libelle, ordre) VALUES
('Admin', 1),
('Utilisateur', 2),
('Modérateur', 3);

-- Insertion dans entreprise
INSERT INTO entreprise (nom) VALUES
('Colas'),
('Magic');

-- Insertion dans statut_signalement
INSERT INTO statut_signalement (libelle, ordre) VALUES
('Signalé', 1),
('En cours', 2),
('Résolu', 3),
('Rejeté', 4);

-- Insertion dans users (liée à profil)
-- Ajout des colonnes failed_attempts et blocked pour conformité avec le modèle JPA
INSERT INTO users (identifiant, password, date_creation, date_derniere_connexion, id_profil, failed_attempts, blocked, id_firestore_tentative) VALUES
('rakoto@gmail.mg', 'adminadmin', '2026-01-01 10:00:00', '2026-10-01 15:30:00', 1, 0, false, NULL),
('sarah@gmail.mg', 'adminadmin', '2026-02-15 09:00:00', NULL, 2, 0, false, NULL),
('jean@gmail.mg', 'adminadmin', '2026-03-20 14:00:00', '2026-09-15 12:00:00', 3, 0, false, NULL),
('miaritsoa@mail.com', 'adminadmin', '2026-02-15 09:00:00', NULL, 1, 0, false, NULL);


-- Insertion dans signalement (liée à entreprise et users)
INSERT INTO signalement (surface, budget, localisation, id_entreprise, id_user) VALUES
(150.50, 5000000.00, ST_GeogFromText('POINT(47.5162 -18.8792)'), 1, 1),  -- Antananarivo
(200.00, 7500000.00, ST_GeogFromText('POINT(49.4050 -18.1667)'), 2, 2),  -- Toamasina
(100.25, 3000000.00, ST_GeogFromText('POINT(44.2833 -20.2833)'), 1, 3);  -- Toliara

-- Insertion dans signalement_statut (liée à users, statut_signalement, signalement)
INSERT INTO signalement_statut (date_statut, id_user, id_statut, id_signalement) VALUES
('2025-10-01 10:00:00', 1, 1, 1),
('2025-10-02 11:00:00', 2, 2, 2),
('2025-10-03 12:00:00', 3, 3, 3);

-- Insertion dans avancement (liée à signalement)
INSERT INTO avancement (date_avancement, avancement, id_signalement) VALUES
('2026-10-01', 25.00, 1),
('2026-10-15', 50.00, 2),
('2026-10-20', 75.00, 3);

-- Insertion dans log (liée à users)
INSERT INTO tentative (date_tentative, succes, id_user) VALUES
('2026-10-01 08:00:00', true, 1),
('2026-10-01 08:05:00', false, 2),
('2026-10-02 09:00:00', true, 3);
