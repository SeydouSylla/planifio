package com.planifio.controller;

import com.planifio.security.UtilisateurDetails;
import com.planifio.service.StatistiquesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatistiquesController {

    private final StatistiquesService service;

    @GetMapping("/statistiques")
    public String page(Model model, @AuthenticationPrincipal UtilisateurDetails principal) {
        model.addAttribute("stats", service.calculerPour(principal.getUtilisateurId()));
        return "statistiques";
    }
}
