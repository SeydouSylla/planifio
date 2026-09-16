package com.planifio.controller;

import com.planifio.security.UtilisateurDetails;
import com.planifio.service.EvenementService;
import com.planifio.service.TacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AccueilController {

    private final TacheService tacheService;
    private final EvenementService evenementService;

    @GetMapping("/")
    public String accueil(Model model, @AuthenticationPrincipal UtilisateurDetails principal) {
        // Le contrôleur orchestre : il appelle les services et prépare la
        // vue, jamais lui-même la logique de "qu'est-ce qu'une tâche du
        // jour" - cette règle vit dans TacheService.tachesDuJour(), testable
        // indépendamment de toute couche web.
        model.addAttribute("nomUtilisateur", principal.getNom());
        model.addAttribute("tachesDuJour", tacheService.tachesDuJour(principal.getUtilisateurId()));
        model.addAttribute("evenementsAVenir", evenementService.evenementsAVenir(principal.getUtilisateurId(), 7));
        return "accueil";
    }
}
