package com.planifio.dto;

import java.time.LocalDateTime;

public record EvenementDTO(
        Long id,
        String titre,
        String description,
        LocalDateTime dateDebut,
        LocalDateTime dateFin
) {}
