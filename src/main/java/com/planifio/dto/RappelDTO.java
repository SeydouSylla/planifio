package com.planifio.dto;

import com.planifio.model.TypeRappel;

import java.time.LocalDateTime;

public record RappelDTO(
        TypeRappel type,
        String titre,
        LocalDateTime echeance,
        String emailUtilisateur
) {}
