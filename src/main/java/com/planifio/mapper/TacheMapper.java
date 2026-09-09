package com.planifio.mapper;

import com.planifio.dto.TacheDTO;
import com.planifio.model.Tache;

public class TacheMapper {
    public static TacheDTO versDto(Tache tache) {
        return new TacheDTO(
                tache.getId(),
                tache.getTitre(),
                tache.isTerminee(),
                tache.getDateEcheance(),
                tache.getPriorite(),
                tache.getCategorie()
        );
    }
}
