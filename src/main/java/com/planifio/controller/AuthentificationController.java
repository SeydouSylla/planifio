package com.planifio.controller;

import com.planifio.dto.InscriptionForm;
import com.planifio.service.InscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthentificationController {

    private final InscriptionService inscriptionService;

    @GetMapping("/connexion")
    public String pageConnexion() {
        // Retourne simplement le template - Spring Security gère lui-même
        // le traitement du POST vers cette même URL.
        return "connexion";
    }

    @GetMapping("/inscription")
    public String formulaireInscription(Model model) {
        model.addAttribute("inscriptionForm", new InscriptionForm());
        return "inscription";
    }

    @PostMapping("/inscription")
    public String traiterInscription(
            @Valid @ModelAttribute("inscriptionForm") InscriptionForm form,
            BindingResult resultat,
            Model model) {

        if (resultat.hasErrors()) {
            return "inscription";
        }

        try {
            inscriptionService.inscrire(form);
        } catch (InscriptionService.EmailDejaUtiliseException e) {
            model.addAttribute("erreurEmail", e.getMessage());
            return "inscription";
        }

        return "redirect:/connexion?inscrit";
    }
}
