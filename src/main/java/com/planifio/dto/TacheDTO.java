package com.planifio.dto;

import com.planifio.model.Priorite;

import java.time.LocalDateTime;

public record TacheDTO(
        Long id,
        String titre,
        boolean terminee,
        LocalDateTime dateEcheance,
        Priorite priorite,
        String categorie
) {}
