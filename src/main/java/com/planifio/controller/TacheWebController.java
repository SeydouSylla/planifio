package com.planifio.controller;

import com.planifio.dto.TacheFormulaire;
import com.planifio.model.Priorite;
import com.planifio.security.UtilisateurDetails;
import com.planifio.service.TacheService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/taches")
@RequiredArgsConstructor
public class TacheWebController {

    private final TacheService service;

    
    //@AuthenticationPrincipal injecte directement l'utilisateur connecté,
    // sans jamais avoir à interroger manuellement le contexte de sécurité -
    //Spring Security le résout automatiquement à partir de la session active.
     
    @GetMapping
    public String liste(Model model, @AuthenticationPrincipal UtilisateurDetails principal) {
        model.addAttribute("taches", service.listerToutes(principal.getUtilisateurId()));
        model.addAttribute("nomUtilisateur", principal.getNom());
        model.addAttribute("priorites", Priorite.values());
        if (!model.containsAttribute("tacheFormulaire")) {
            model.addAttribute("tacheFormulaire", new TacheFormulaire());
        }
        return "taches";
    }

    @PostMapping
    public String ajouter(
            @Valid @ModelAttribute("tacheFormulaire") TacheFormulaire formulaire,
            BindingResult resultat,
            Model model,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        if (resultat.hasErrors()) {
            // Ré-affiche la même page, avec les erreurs de validation -
            // jamais une redirection ici, qui perdrait les erreurs en route.
            model.addAttribute("taches", service.listerToutes(principal.getUtilisateurId()));
            model.addAttribute("nomUtilisateur", principal.getNom());
            model.addAttribute("priorites", Priorite.values());
            return "taches";
        }

        service.creer(formulaire.getTitre(), formulaire.getDateEcheance(),
                formulaire.getPriorite(), formulaire.getCategorie(), principal.getUtilisateurId());
        return "redirect:/taches";
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(@PathVariable Long id, Model model,
                                          @AuthenticationPrincipal UtilisateurDetails principal) {
        var tache = service.trouverParId(id, principal.getUtilisateurId());

        // Pré-remplit le formulaire validé à partir des valeurs actuelles -
        // le template lie désormais tacheFormulaire, exactement la même
        // cible que le POST de modification, jamais deux objets différents
        // qui risqueraient de diverger.
        TacheFormulaire formulaire = new TacheFormulaire();
        formulaire.setTitre(tache.titre());
        formulaire.setDateEcheance(tache.dateEcheance());
        formulaire.setPriorite(tache.priorite());
        formulaire.setCategorie(tache.categorie());

        model.addAttribute("tacheId", id);
        model.addAttribute("tacheFormulaire", formulaire);
        model.addAttribute("priorites", Priorite.values());
        return "tache-modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("tacheFormulaire") TacheFormulaire formulaire,
            BindingResult resultat,
            Model model,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        if (resultat.hasErrors()) {
            model.addAttribute("tacheId", id);
            model.addAttribute("priorites", Priorite.values());
            return "tache-modifier";
        }

        service.modifier(id, formulaire.getTitre(), formulaire.getDateEcheance(),
                formulaire.getPriorite(), formulaire.getCategorie(), principal.getUtilisateurId());
        return "redirect:/taches";
    }

    @PostMapping("/{id}/terminer")
    public String terminer(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails principal) {
        service.terminer(id, principal.getUtilisateurId());
        return "redirect:/taches";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails principal) {
        service.supprimer(id, principal.getUtilisateurId());
        return "redirect:/taches";
    }
}
