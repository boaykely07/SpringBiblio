-- Supprimer toutes les données existantes pour un jeu de test propre (optionnel)
-- TRUNCATE TABLE ... CASCADE; -- Attention, cette commande est destructive.

-- 1. Insertion dans les tables de référence (sans dépendances)

INSERT INTO Profils_Adherent (id_profil, nom_profil, quota_emprunts_simultanes) VALUES
(1, 'Etudiant', 5),
(2, 'Professeur', 10),
(3, 'Professionnel', 7),
(4, 'Anonyme', 1);

INSERT INTO Auteurs (id_auteur, nom, prenom) VALUES
(1, 'Hugo', 'Victor'),
(2, 'Camus', 'Albert'),
(3, 'Rowling', 'J.K.'),
(4, 'Asimov', 'Isaac');

INSERT INTO Editeurs (id_editeur, nom) VALUES
(1, 'Gallimard'),
(2, 'Hachette'),
(3, 'Fleuve Noir');

INSERT INTO Categories (id_categorie, nom) VALUES
(1, 'Roman'),
(2, 'Science-Fiction'),
(3, 'Philosophie'),
(4, 'Jeunesse'),
(5, 'Histoire');

INSERT INTO Jours_Feries (date_ferie, description) VALUES
('2024-12-25', 'Noël'),
('2025-01-01', 'Jour de l''An'),
('2025-05-01', 'Fête du Travail');

INSERT INTO Statuts_Reservation (id_statut, code_statut) VALUES
(1, 'En attente'),
(2, 'Disponible'),
(3, 'Annulée'),
(4, 'Honorée');

INSERT INTO Statuts_Emprunt (id_statut, code_statut) VALUES
(1, 'En cours'),
(2, 'Retourné'),
(3, 'En retard'),
(4, 'Perdu');

INSERT INTO Type_emprunts (id_type_emprunt, nom_type) VALUES
(1, 'Sur place'),
(2, 'A domicile');


-- 2. Insertion des Livres
INSERT INTO Livres (id_livre, titre, isbn, annee_publication, resume, id_editeur, id_auteur) VALUES
(1, 'Les Misérables', '978-2070360029', 1862, 'Un roman sur la misère et l''injustice sociale à Paris au XIXe siècle.', 1, 1),
(2, 'L''Étranger', '978-2070360012', 1942, 'L''histoire de Meursault, un homme indifférent au monde qui l''entoure.', 1, 2),
(3, 'Harry Potter à l''école des sorciers', '978-2070518425', 1997, 'Les débuts du jeune sorcier Harry Potter à Poudlard.', 2, 3),
(4, 'Fondation', '978-2070415958', 1951, 'L''histoire d''un empire galactique en déclin et d''une fondation pour préserver le savoir.', 3, 4),
(5, 'La Peste', '978-2070364287', 1947, 'Chronique de la vie des habitants d''Oran pendant une épidémie de peste.', 1, 2);

-- Liaison Livres <-> Catégories
INSERT INTO Livres_Categories (id_livre, id_categorie) VALUES
(1, 1), (1, 5), -- Les Misérables -> Roman, Histoire
(2, 1), (2, 3), -- L'Étranger -> Roman, Philosophie
(3, 1), (3, 4), -- Harry Potter -> Roman, Jeunesse
(4, 2),         -- Fondation -> Science-Fiction
(5, 1);          -- La Peste -> Roman

-- 3. Insertion des Utilisateurs (base pour Adhérents et Bibliothécaires)
-- Les mots de passe sont des exemples et devraient être "hachés" dans une vraie application.
INSERT INTO Utilisateurs (id_utilisateur, email, mot_de_passe_hash) VALUES
(1, 'jean.dupont@email.com', 'hash_password_123'),
(2, 'marie.curie@email.com', 'hash_password_456'),
(3, 'paul.martin@email.com', 'hash_password_789'),
(4, 'anonyme123@email.com', 'hash_password_abc'),
(5, 'alice.lemaire@biblio.com', 'hash_password_def'),
(6, 'luc.petit@email.com', 'hash_password_ghi');


-- 4. Insertion des Adhérents et Bibliothécaires

-- Adhérent 1: Étudiant, abonnement actif
INSERT INTO Adherents (id_adherent, id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(1, 1, 'Dupont', 'Jean', '2002-05-15', 1); -- Profil Etudiant

-- Adhérent 2: Professeur, abonnement actif
INSERT INTO Adherents (id_adherent, id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(2, 2, 'Curie', 'Marie', '1980-11-07', 2); -- Profil Professeur

-- Adhérent 3: Professionnel, abonnement expiré
INSERT INTO Adherents (id_adherent, id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(3, 3, 'Martin', 'Paul', '1990-01-20', 3); -- Profil Professionnel

-- Adhérent 4: Anonyme, abonnement actif
INSERT INTO Adherents (id_adherent, id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(4, 4, 'Térieur', 'Alain', '1975-08-30', 4); -- Profil Anonyme

-- Adhérent 5: Un autre étudiant
INSERT INTO Adherents (id_adherent, id_utilisateur, nom, prenom, date_naissance, id_profil) VALUES
(5, 6, 'Petit', 'Luc', '2003-03-10', 1); -- Profil Etudiant

-- Bibliothécaire
INSERT INTO Bibliothecaires (id_bibliothecaire, id_utilisateur, nom, prenom) VALUES
(1, 5, 'Lemaire', 'Alice');


-- 5. Insertion des Abonnements
INSERT INTO Abonnements (id_abonnement, id_adherent, date_debut, date_fin) VALUES
(1, 1, '2023-09-01', '2024-08-31'), -- Jean Dupont (actif)
(2, 2, '2023-09-01', '2024-08-31'), -- Marie Curie (actif)
(3, 3, '2022-06-01', '2023-05-31'), -- Paul Martin (expiré)
(4, 4, '2024-01-01', '2024-12-31'), -- Alain Térieur (actif)
(5, 5, '2023-09-15', '2024-09-14'); -- Luc Petit (actif)

-- 6. Insertion des Exemplaires de livres
INSERT INTO Exemplaires (id_exemplaire, id_livre, quantite) VALUES
(1, 1, 3), -- 3 exemplaires de "Les Misérables"
(2, 2, 2), -- 2 exemplaires de "L'Étranger"
(3, 3, 5), -- 5 exemplaires de "Harry Potter"
(4, 4, 1), -- 1 seul exemplaire de "Fondation"
(5, 5, 2); -- 2 exemplaires de "La Peste"


-- 7. Droits spécifiques (Exemple : un livre interdit à un certain profil)
INSERT INTO Droits_Emprunt_Specifiques (id_livre, id_profil, emprunt_domicile_autorise) VALUES
(4, 4, FALSE); -- Le livre "Fondation" (id 4) ne peut pas être emprunté à domicile par le profil "Anonyme" (id 4).

-- 8. Création de scénarios d'emprunts et de transactions

-- Scénario 1: Emprunt simple, en cours
INSERT INTO Emprunts (id_emprunt, id_exemplaire, id_adherent, id_type_emprunt, date_emprunt, date_retour_prevue) VALUES
(1, 2, 1, 2, NOW() - INTERVAL '10 days', NOW() + INTERVAL '4 days'); -- Jean (Etudiant) emprunte "L'Étranger", à domicile
-- Mouvement associé
INSERT INTO Mvt_Emprunt (id_emprunt, id_statut_nouveau, date_mouvement) VALUES
(1, 1, NOW() - INTERVAL '10 days'); -- Statut: En cours

-- Scénario 2: Emprunt en retard, avec pénalité
INSERT INTO Emprunts (id_emprunt, id_exemplaire, id_adherent, id_type_emprunt, date_emprunt, date_retour_prevue) VALUES
(2, 1, 2, 2, NOW() - INTERVAL '30 days', NOW() - INTERVAL '16 days'); -- Marie (Professeur) a emprunté "Les Misérables", est en retard
-- Mouvements associés
INSERT INTO Mvt_Emprunt (id_emprunt, id_statut_nouveau, date_mouvement) VALUES
(2, 1, NOW() - INTERVAL '30 days'); -- Passage à 'En cours'
INSERT INTO Mvt_Emprunt (id_emprunt, id_statut_nouveau, date_mouvement) VALUES
(2, 3, NOW() - INTERVAL '15 days'); -- Passage à 'En retard'
-- Pénalité associée
INSERT INTO Penalites (id_penalite, id_emprunt, id_adherent, date_debut, jour, raison) VALUES
(1, 2, 2, NOW() - INTERVAL '15 days', 15, 'Retour en retard de plus de 14 jours');

-- Scénario 3: Emprunt prolongé
INSERT INTO Emprunts (id_emprunt, id_exemplaire, id_adherent, id_type_emprunt, date_emprunt, date_retour_prevue) VALUES
(3, 3, 5, 2, NOW() - INTERVAL '21 days', NOW() + INTERVAL '7 days'); -- Luc (Etudiant) emprunte "Harry Potter"
-- Mouvement associé
INSERT INTO Mvt_Emprunt (id_emprunt, id_statut_nouveau, date_mouvement) VALUES
(3, 1, NOW() - INTERVAL '21 days'); -- Statut: En cours
-- Prolongement (la date de retour prévue dans la table Emprunts doit refléter la nouvelle date)
INSERT INTO Prolongements (id_prolongement, id_emprunt, date_fin, date_prolongement) VALUES
(1, 3, NOW() + INTERVAL '7 days', NOW() - INTERVAL '5 days'); -- Prolongé il y a 5 jours

-- 9. Création de réservations

-- Scénario 4: Réservation en attente
-- On suppose que le seul exemplaire de "Fondation" (id_exemplaire 4) est déjà emprunté.
-- On crée donc la réservation pour un adhérent qui le souhaite.
INSERT INTO Reservations (id_reservation, id_livre, id_adherent, date_demande, date_a_reserver) VALUES
(1, 4, 1, NOW() - INTERVAL '2 days', CURRENT_DATE); -- Jean (Etudiant) réserve "Fondation"
-- Mouvement de réservation
INSERT INTO Mvt_Reservation (id_mvt_reservation, id_reservation, id_statut_nouveau, date_mouvement) VALUES
(1, 1, 1, NOW() - INTERVAL '2 days'); -- Statut: En attente