CREATE TABLE evenements (
    id BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    date_debut TIMESTAMP NOT NULL,
    date_fin TIMESTAMP NOT NULL,
    utilisateur_id BIGINT NOT NULL REFERENCES utilisateurs(id)
);

-- Accélère les requêtes "événements du mois de tel utilisateur",
-- le type de requête le plus fréquent sur cette table (vue calendrier).
CREATE INDEX idx_evenements_utilisateur_dates ON evenements (utilisateur_id, date_debut);
