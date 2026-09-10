package com.planifio.service;

import com.planifio.dto.StatistiquesDTO;

public interface StatistiquesService {
    StatistiquesDTO calculerPour(Long utilisateurId);
}
