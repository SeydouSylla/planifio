package com.planifio.service;

import com.planifio.dto.EvenementDTO;
import com.planifio.dto.JourCalendrier;

import java.time.YearMonth;
import java.util.List;

public interface EvenementService {

    /*
     Retourne la grille complète du mois - une liste de semaines, chacune
     une liste de 7 jours - prête à être affichée par le template, sans
     qu'aucun calcul de date ne soit nécessaire côté Thymeleaf.
     */
    List<List<JourCalendrier>> grilleDuMois(YearMonth mois, Long utilisateurId);

    EvenementDTO creer(String titre, String description, java.time.LocalDateTime debut,
                        java.time.LocalDateTime fin, Long utilisateurId);

    EvenementDTO modifier(Long id, String titre, String description,
                           java.time.LocalDateTime debut, java.time.LocalDateTime fin, Long utilisateurId);

    void supprimer(Long id, Long utilisateurId);

    /** Utilisée pour préremplir le formulaire de modification. */
    EvenementDTO trouverParId(Long id, Long utilisateurId);

    /** Utilisée par la page d'accueil : les prochains événements, sur une fenêtre de N jours. */
    List<EvenementDTO> evenementsAVenir(Long utilisateurId, int nombreDeJours);
}
