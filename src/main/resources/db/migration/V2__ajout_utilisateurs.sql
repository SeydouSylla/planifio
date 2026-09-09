CREATE TABLE utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe_hache VARCHAR(255) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT now(),
    tentatives_echouees_consecutives INTEGER NOT NULL DEFAULT 0
);

-- Étape 1 : la colonne est ajoutée nullable - impossible de la rendre
-- obligatoire tant que les lignes existantes n'ont pas de valeur.
ALTER TABLE taches ADD COLUMN utilisateur_id BIGINT REFERENCES utilisateurs(id);

-- Étape 2 : nettoyage des données de test antérieures à l'introduction des
-- comptes utilisateurs (aucune tâche ne peut légitimement exister sans
-- propriétaire dans ce projet.
DELETE FROM taches WHERE utilisateur_id IS NULL;

-- Étape 3 : seulement maintenant, la contrainte NOT NULL peut être posée
-- sans échouer sur des lignes existantes incompatibles.
ALTER TABLE taches ALTER COLUMN utilisateur_id SET NOT NULL;
