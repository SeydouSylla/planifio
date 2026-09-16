package com.planifio.dto;

//tauxCompletionGlobal est une valeur entre 0.0 et 1.0 (jamais un
//pourcentage déjà multiplié par 100)

public record StatistiquesDTO(
        int tachesTermineesSemaine,
        int tachesTotalSemaine,
        int tachesEnRetard,
        double tauxCompletionGlobal,
        int evenementsCeMois
) {}
