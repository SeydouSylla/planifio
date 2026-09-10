package com.planifio.mapper;

import com.planifio.dto.EvenementDTO;
import com.planifio.model.Evenement;

public class EvenementMapper {
    public static EvenementDTO versDto(Evenement evenement) {
        return new EvenementDTO(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getDateDebut(),
                evenement.getDateFin()
        );
    }
}
