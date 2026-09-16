-- Nullable : comme dateEcheance, ni la priorité ni la catégorie ne sont
-- obligatoires pour qu'une tâche existe.
ALTER TABLE taches ADD COLUMN priorite VARCHAR(20);
ALTER TABLE taches ADD COLUMN categorie VARCHAR(100);
