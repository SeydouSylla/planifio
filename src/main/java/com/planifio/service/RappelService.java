package com.planifio.service;

import com.planifio.dto.RappelDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface RappelService {

    /*
     Détecte les tâches et événements dont l'échéance tombe dans la
     fenêtre [maintenant, maintenant + fenetre] - logique pure, testable
     sans jamais avoir besoin d'un vrai planificateur ni d'une vraie base.
     */
    List<RappelDTO> trouverRappelsAEnvoyer(LocalDateTime maintenant, Duration fenetre);
}
