package com.planifio.service;

import com.planifio.dto.RappelDTO;
import com.planifio.model.Evenement;
import com.planifio.model.Tache;
import com.planifio.model.TypeRappel;
import com.planifio.repository.EvenementRepository;
import com.planifio.repository.TacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RappelServiceImpl implements RappelService {

    private final TacheRepository tacheRepository;
    private final EvenementRepository evenementRepository;

    @Override
    public List<RappelDTO> trouverRappelsAEnvoyer(LocalDateTime maintenant, Duration fenetre) {
        LocalDateTime limite = maintenant.plus(fenetre);
        List<RappelDTO> rappels = new ArrayList<>();

        for (Tache tache : tacheRepository.trouverTachesAEcheanceProche(maintenant, limite)) {
            rappels.add(new RappelDTO(
                    TypeRappel.TACHE, tache.getTitre(), tache.getDateEcheance(), tache.getUtilisateur().getEmail()));
        }

        for (Evenement evenement : evenementRepository.trouverEvenementsProches(maintenant, limite)) {
            rappels.add(new RappelDTO(
                    TypeRappel.EVENEMENT, evenement.getTitre(), evenement.getDateDebut(), evenement.getUtilisateur().getEmail()));
        }

        return rappels;
    }
}
