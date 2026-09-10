package com.planifio.scheduler;

import com.planifio.dto.RappelDTO;
import com.planifio.service.RappelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/*
 Job planifié - la pièce que je surveille en continu en
 production : "le job a-t-il bien tourné cette nuit ?". Ici, cette
 surveillance passe uniquement par les logs applicatifs (visibles via
 `kubectl logs`), sans métriques Prometheus pour l'instant.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RappelScheduler {

    private final RappelService rappelService;

    
    //cron = "0 0 8 * * *" : tous les jours à 8h00 précises.
    //(Sur les 6 champs cron de Spring : secondes minutes heures jour-du-mois mois jour-de-semaine)
    @Scheduled(cron = "0 0 8 * * *")
    public void executerJobRappels() {
        try {
            List<RappelDTO> rappels = rappelService.trouverRappelsAEnvoyer(
                    LocalDateTime.now(), Duration.ofHours(24));

            for (RappelDTO rappel : rappels) {
                // L'envoi réel (email/SMTP) est une évolution future documentée -
                // le log structuré ci-dessous est déjà exploitable via `kubectl logs`.
                log.info("Rappel [{}] pour {} : \"{}\" prévu le {}",
                        rappel.type(), rappel.emailUtilisateur(), rappel.titre(), rappel.echeance());
            }

            log.info("Job de rappels terminé : {} rappel(s) traité(s)", rappels.size());

        } catch (Exception exception) {
            // Ne JAMAIS laisser une exception remonter hors du job planifié -
            // Spring désactiverait silencieusement toute exécution future.
            log.error("Échec du job de rappels", exception);
        }
    }
}
