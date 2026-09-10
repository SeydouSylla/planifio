package com.planifio.dto;

import java.time.LocalDate;
import java.util.List;


public record JourCalendrier(
        LocalDate date,
        boolean dansLeMoisCourant,
        boolean estAujourdHui,
        List<EvenementDTO> evenements
) {}
