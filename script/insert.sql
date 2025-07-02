-- Insertion de profils d'adhérent exemples
INSERT INTO Profils_Adherent (nom_profil, quota_emprunts_simultanes) VALUES
  ('Étudiant', 3),
  ('Professeur', 2),
  ('Public', 5);


-- Insertion d'auteurs
INSERT INTO Auteurs (nom, prenom) VALUES
  ('Hugo', 'Victor'),
  ('Zola', 'Émile'),
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
INSERT INTO Livres (titre, isbn, annee_publication, resume, id_editeur) VALUES
  ('Les Misérables', '9782070409189', 1862, 'Roman historique de Victor Hugo.', 1),
  ('Germinal', '9782070413117', 1885, 'Roman d Emile Zola sur le monde ouvrier.', 2),
  ('Harry Potter à l ecole des sorciers', '9782070643026', 1997, 'Premier tome de la saga Harry Potter.', 3);

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

-- Insertion de liaisons Livres_Auteurs
INSERT INTO Livres_Auteurs (id_livre, id_auteur) VALUES
  (1, 1), -- Les Misérables, Victor Hugo
  (2, 2), -- Germinal, Émile Zola
  (3, 3), -- Harry Potter, J.K. Rowling
  (3, 4); -- Harry Potter, Albert Camus (exemple multi-auteur)

-- Insertion de liaisons Livres_Categories
INSERT INTO Livres_Categories (id_livre, id_categorie) VALUES
  (1, 1), -- Les Misérables, Roman
  (2, 1), -- Germinal, Roman
  (2, 2), -- Germinal, Policier (exemple multi-catégorie)
  (3, 4), -- Harry Potter, Jeunesse
  (3, 3); -- Harry Potter, Science-Fiction
