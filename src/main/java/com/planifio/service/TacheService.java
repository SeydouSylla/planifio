package com.planifio.service;

import com.planifio.dto.TacheDTO;
import com.planifio.model.Priorite;

import java.time.LocalDateTime;
import java.util.List;

public interface TacheService {
    List<TacheDTO> listerToutes(Long utilisateurId);

    TacheDTO creer(String titre, LocalDateTime dateEcheance, Priorite priorite, String categorie, Long utilisateurId);

    TacheDTO modifier(Long id, String titre, LocalDateTime dateEcheance, Priorite priorite, String categorie, Long utilisateurId);

    TacheDTO terminer(Long id, Long utilisateurId);

    void supprimer(Long id, Long utilisateurId);

    // Utilisée par la page d'accueil.
    TacheDTO trouverParId(Long id, Long utilisateurId);

    // Tâches non terminées dont l'échéance tombe aujourd'hui - logique métier, jamais dans un contrôleur.
    List<TacheDTO> tachesDuJour(Long utilisateurId);
}
