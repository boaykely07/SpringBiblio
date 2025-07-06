Le resaka prolongement(mbola mety ve) mila angatahana zay vao validen admin aveo 
SELECT e.id_emprunt, e.id_adherent, a.nom, a.prenom, l.titre
FROM Emprunts e
JOIN Adherents a ON e.id_adherent = a.id_adherent
JOIN Exemplaires ex ON e.id_exemplaire = ex.id_exemplaire
JOIN Livres l ON ex.id_livre = l.id_livre 

SELECT m.id_emprunt, s.code_statut
FROM Mvt_Emprunt m
JOIN Statuts_Emprunt s ON m.id_statut_nouveau = s.id_statut
WHERE m.id_emprunt = 3
ORDER BY m.date_mouvement DESC
LIMIT 1;







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
(2, 'Livre1', '978-2070360012', 1942, 'L''histoire de Meursault, un homme indifférent au monde qui l''entoure.', 1, 2),
(3, 'Harry Potter à l''école des sorciers', '978-2070518425', 1997, 'Les débuts du jeune sorcier Harry Potter à Poudlard.', 2, 3),
(4, 'test', '978-2070415958', 1951, 'L''histoire d''un empire galactique en déclin et d''une fondation pour préserver le savoir.', 3, 4),
(5, 'La Peste', '978-2070364287', 1947, 'Chronique de la vie des habitants d''Oran pendant une épidémie de peste.', 1, 2);

-- Liaison Livres <-> Catégories
INSERT INTO Livres_Categories (id_livre, id_categorie) VALUES
(1, 1), (1, 5), -- Les Misérables -> Roman, Histoire
(2, 1), (2, 3), -- L'Étranger -> Roman, Philosophie
(3, 1), (3, 4), -- Harry Potter -> Roman, Jeunesse
(4, 2),         -- Fondation -> Science-Fiction
(5, 1);          -- La Peste -> Roman
INSERT INTO Exemplaires (id_exemplaire, id_livre, quantite) VALUES
(1, 1, 3), -- 3 exemplaires de "Les Misérables"
(2, 2, 2), -- 2 exemplaires de "L'Étranger"
(3, 3, 5), -- 5 exemplaires de "Harry Potter"
(4, 4, 2), -- 1 seul exemplaire de "Fondation"
(5, 5, 2); -- 2 exemplaires de "La Peste"


-- 7. Droits spécifiques (Exemple : un livre interdit à un certain profil)
INSERT INTO Droits_Emprunt_Specifiques (id_livre, id_profil, emprunt_domicile_autorise) VALUES
(4, 1, FALSE); -- Le livre "Fondation" (id 4) ne peut pas être emprunté à domicile par le profil "Etudiant" .
