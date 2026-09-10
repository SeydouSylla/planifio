package com.planifio.controller;

import com.planifio.dto.EvenementFormulaire;
import com.planifio.security.UtilisateurDetails;
import com.planifio.service.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Controller
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final EvenementService service;

    @GetMapping
    public String vueCalendrier(
            @RequestParam(required = false) String mois,
            Model model,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        // Sans paramètre "mois" dans l'URL, on affiche le mois en cours.
        YearMonth moisAffiche = (mois != null) ? YearMonth.parse(mois) : YearMonth.now();

        model.addAttribute("semaines", service.grilleDuMois(moisAffiche, principal.getUtilisateurId()));
        model.addAttribute("moisAffiche", moisAffiche);
        model.addAttribute("moisPrecedent", moisAffiche.minusMonths(1));
        model.addAttribute("moisSuivant", moisAffiche.plusMonths(1));
        if (!model.containsAttribute("evenementFormulaire")) {
            model.addAttribute("evenementFormulaire", new EvenementFormulaire());
        }
        return "agenda";
    }

    @PostMapping
    public String creer(
            @Valid @ModelAttribute("evenementFormulaire") EvenementFormulaire formulaire,
            BindingResult resultat,
            Model model,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        if (resultat.hasErrors()) {
            YearMonth moisAffiche = YearMonth.now();
            model.addAttribute("semaines", service.grilleDuMois(moisAffiche, principal.getUtilisateurId()));
            model.addAttribute("moisAffiche", moisAffiche);
            model.addAttribute("moisPrecedent", moisAffiche.minusMonths(1));
            model.addAttribute("moisSuivant", moisAffiche.plusMonths(1));
            return "agenda";
        }

        service.creer(formulaire.getTitre(), formulaire.getDescription(),
                formulaire.getDateDebut(), formulaire.getDateFin(), principal.getUtilisateurId());
        return "redirect:/agenda?mois=" + YearMonth.from(formulaire.getDateDebut());
    }

    @GetMapping("/{id}/modifier")
    public String formulaireModification(
            @PathVariable Long id,
            @RequestParam String mois,
            Model model,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        var evenement = service.trouverParId(id, principal.getUtilisateurId());

        EvenementFormulaire formulaire = new EvenementFormulaire();
        formulaire.setTitre(evenement.titre());
        formulaire.setDescription(evenement.description());
        formulaire.setDateDebut(evenement.dateDebut());
        formulaire.setDateFin(evenement.dateFin());

        model.addAttribute("evenementId", id);
        model.addAttribute("evenementFormulaire", formulaire);
        model.addAttribute("mois", mois);
        return "agenda-modifier";
    }

    @PostMapping("/{id}/modifier")
    public String modifier(
            @PathVariable Long id,
            @Valid @ModelAttribute("evenementFormulaire") EvenementFormulaire formulaire,
            BindingResult resultat,
            Model model,
            @RequestParam(required = false) String mois,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        if (resultat.hasErrors()) {
            model.addAttribute("evenementId", id);
            model.addAttribute("mois", mois);
            return "agenda-modifier";
        }

        service.modifier(id, formulaire.getTitre(), formulaire.getDescription(),
                formulaire.getDateDebut(), formulaire.getDateFin(), principal.getUtilisateurId());
        return "redirect:/agenda?mois=" + YearMonth.from(formulaire.getDateDebut());
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(
            @PathVariable Long id,
            @RequestParam String mois,
            @AuthenticationPrincipal UtilisateurDetails principal) {

        service.supprimer(id, principal.getUtilisateurId());
        return "redirect:/agenda?mois=" + mois;
    }
}
