-- Nullable : toutes les tâches n'ont pas forcément d'échéance précise
-- (contrairement aux événements, où dateDebut/dateFin sont obligatoires).
ALTER TABLE taches ADD COLUMN date_echeance TIMESTAMP;

-- Accélère la requête du job de rappels : "toutes les tâches dont
-- l'échéance approche, tous utilisateurs confondus".
CREATE INDEX idx_taches_date_echeance ON taches (date_echeance) WHERE date_echeance IS NOT NULL;
