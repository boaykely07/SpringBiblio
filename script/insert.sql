-- Insertion de profils d'adhérent exemples
INSERT INTO Profils_Adherent (nom_profil, quota_emprunts_simultanes, quota_penalites_jours) VALUES
  ('Etudiant', 3, 10),
  ('Professeur', 2, 5),
  ('Public', 5, 7);


-- Insertion d'auteurs
INSERT INTO Auteurs (nom, prenom) VALUES
  ('Hugo', 'Victor'),
  ('Zola', 'Emile'),
  ('Rowling', 'J.K.'),
  ('Camus', 'Albert');

-- Insertion d'éditeurs
INSERT INTO Editeurs (nom) VALUES
  ('Gallimard'),
  ('Flammarion'),
  ('Le Livre de Poche');

-- Insertion de catégories
INSERT INTO Categories (nom) VALUES
  ('Roman'),
  ('Policier'),
  ('Science-Fiction'),
  ('Jeunesse');

-- Insertion de livres
INSERT INTO Livres (titre, isbn, annee_publication, resume, id_editeur, id_auteur) VALUES
  ('Les Miserables', '9782070409189', 1862, 'Roman historique de Victor Hugo.', 1, 1),
  ('Germinal', '9782070413117', 1885, 'Roman d Emile Zola sur le monde ouvrier.', 2, 2),
  ('Harry Potter a l ecole des sorciers', '9782070643026', 1997, 'Premier tome de la saga Harry Potter.', 3, 3);

-- Insertion d'exemplaires
INSERT INTO Exemplaires (id_livre, quantite) VALUES
  (1, 3),
  (2, 2),
  (3, 5);

-- Insertion de droits d'emprunt spécifiques
INSERT INTO Droits_Emprunt_Specifiques (id_livre, id_profil, age, emprunt_surplace_autorise, emprunt_domicile_autorise) VALUES
  (1, 1, 18, TRUE, TRUE),
  (2, 2, 16, TRUE, TRUE),
  (3, 3, 10, TRUE, FALSE);

-- Insertion de liaisons Livres_Categories
INSERT INTO Livres_Categories (id_livre, id_categorie) VALUES
  (1, 1), -- Les Misérables, Roman
  (2, 1), -- Germinal, Roman
  (2, 2), -- Germinal, Policier (exemple multi-catégorie)
  (3, 4), -- Harry Potter, Jeunesse
  (3, 3); -- Harry Potter, Science-Fiction

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