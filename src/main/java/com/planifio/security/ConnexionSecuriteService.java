package com.planifio.security;

import com.planifio.model.Utilisateur;
import com.planifio.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConnexionSecuriteService {

    
    //Seuil au-delà duquel un compte est considéré verrouillé - référencé
    //aussi par UtilisateurDetails.isAccountNonLocked(), qui doit rester
    //strictement cohérent avec cette valeur.
    public static final int SEUIL_VERROUILLAGE = 5;

    private final UtilisateurRepository repository;

    public void enregistrerEchec(String email) {
        // Aucune action si l'email n'existe pas - ne jamais créer d'effet de
        // bord observable qui permettrait de deviner si un email est
        // enregistré ou non.
        repository.findByEmail(email).ifPresent(utilisateur -> {
            int nouveauCompte = utilisateur.getTentativesEchoueesConsecutives() + 1;
            utilisateur.setTentativesEchoueesConsecutives(nouveauCompte);
            repository.save(utilisateur);

            if (nouveauCompte == SEUIL_VERROUILLAGE) {
                // Le seul signal disponible ici est le log applicatif
                // (visible via `kubectl logs`)
                log.warn("Compte verrouillé après {} échecs consécutifs : {}", SEUIL_VERROUILLAGE, email);
            }
        });
    }

    public void enregistrerReussite(String email) {
        repository.findByEmail(email).ifPresent(utilisateur -> {
            // Une connexion réussie remet le compteur à zéro - le principe
            // même d'un verrouillage par ÉCHECS CONSÉCUTIFS, pas cumulés
            // sur toute la vie du compte.
            if (utilisateur.getTentativesEchoueesConsecutives() > 0) {
                utilisateur.setTentativesEchoueesConsecutives(0);
                repository.save(utilisateur);
            }
        });
    }

    public boolean estVerrouille(Utilisateur utilisateur) {
        return utilisateur.getTentativesEchoueesConsecutives() >= SEUIL_VERROUILLAGE;
    }
}
