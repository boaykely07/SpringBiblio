

-- Données de test pour Type_emprunts
INSERT INTO Type_emprunts (nom_type) VALUES ('Sur place');
INSERT INTO Type_emprunts (nom_type) VALUES ('A domicile');

-- Insertion de statuts d'emprunt
INSERT INTO Statuts_Emprunt (code_statut) VALUES ('En cours');
INSERT INTO Statuts_Emprunt (code_statut) VALUES ('Rendu');
INSERT INTO Statuts_Emprunt (code_statut) VALUES ('Retourner');
INSERT INTO Statuts_Emprunt (code_statut) VALUES ('Perdu');

-- Insertion de statuts de réservation
INSERT INTO Statuts_Reservation (code_statut) VALUES ('En attente');
INSERT INTO Statuts_Reservation (code_statut) VALUES ('Validee');
INSERT INTO Statuts_Reservation (code_statut) VALUES ('Annulee');
INSERT INTO Statuts_Reservation (code_statut) VALUES ('Expiree');

-- Données de base pour Statuts_Prolongement
INSERT INTO Statuts_Prolongement (code_statut) VALUES
    ('En attente'),
    ('Valide'),
    ('Refuse');

-- 1. Profils_Adherent
INSERT INTO Profils_Adherent (nom_profil, quota_emprunts_simultanes, quota_penalites_jours, jour_pret, reservation_pret, prolongement_pret) VALUES
('Etudiant', 2, 10, 7, 1, 3),
('Enseignant', 3, 9, 9, 2, 5),
('Professionnel', 4, 8, 12, 3, 7);

-- 2. Auteurs
INSERT INTO Auteurs (nom, prenom) VALUES
('Hugo', 'Victor'),
('Camus', 'Albert'),
('Rowling', 'J.K.');

-- 3. Editeurs
INSERT INTO Editeurs (nom) VALUES
('Gallimard'),
('Flammarion');

-- 4. Categories
INSERT INTO Categories (nom) VALUES
('Littérature classique'),
('Philosophie'),
('Jeunesse / Fantastique');

-- 5. Livres
INSERT INTO Livres (titre, isbn, annee_publication, id_editeur, id_auteur) VALUES
('Les Misérables', '9782070409189', 1862, 1, 1),
('L''Étranger', '9782070360022', 1942, 1, 2),
('Harry Potter à l''école des sorciers', '9782070643026', 1997, 2, 3);

-- 6. Livres_Categories
INSERT INTO Livres_Categories (id_livre, id_categorie) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 7. Utilisateurs
INSERT INTO Utilisateurs (email, mot_de_passe_hash) VALUES
('etu001@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('etu002@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('etu003@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('ens001@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('ens002@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('ens003@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('prof001@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW'),
('prof002@example.com', '$2a$10$85pMllpNrAtnA48lYdgJu.p2I8T3KJjKxuNJGTh7ujtmzps.a6ZRW');

-- 8. Adherents
INSERT INTO Adherents (id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(1, 'ETU001', 'ETU001', '2000-01-01', 1),
(2, 'ETU002', 'ETU002', '2000-01-02', 1),
(3, 'ETU003', 'ETU003', '2000-01-03', 1),
(4, 'ENS001', 'ENS001', '1980-01-01', 2),
(5, 'ENS002', 'ENS002', '1980-01-02', 2),
(6, 'ENS003', 'ENS003', '1980-01-03', 2),
(7, 'PROF001', 'PROF001', '1970-01-01', 3),
(8, 'PROF002', 'PROF002', '1970-01-02', 3);

-- 9. Abonnements
INSERT INTO Abonnements (id_adherent, date_debut, date_fin) VALUES
(1, '2025-02-01', '2025-07-24'),
(2, '2025-02-01', '2025-07-01'),
(3, '2025-04-01', '2025-12-01'),
(4, '2025-07-01', '2026-07-01'),
(5, '2025-08-01', '2026-05-01'),
(6, '2025-07-01', '2026-06-01'),
(7, '2025-06-01', '2025-12-01'),
(8, '2024-10-01', '2025-06-01');

-- 10. Exemplaires
INSERT INTO Exemplaires (id_livre) VALUES
(1), (1), (1), -- MIS001, MIS002, MIS003
(2), (2),     -- ETR001, ETR002
(3);          -- HAR001

-- 11. Jours_Feries
INSERT INTO Jours_Feries (date_ferie, description) VALUES
('2025-07-13', 'Jour férié'),
('2025-07-20', 'Jour férié'),
('2025-07-26', 'Jour férié'),
('2025-07-27', 'Jour férié'),
('2025-08-03', 'Jour férié'),
('2025-08-10', 'Jour férié'),
('2025-08-17', 'Jour férié');


