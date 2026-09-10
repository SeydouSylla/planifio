package com.planifio.service;

import com.planifio.dto.StatistiquesDTO;
import com.planifio.model.Evenement;
import com.planifio.model.Tache;
import com.planifio.repository.EvenementRepository;
import com.planifio.repository.TacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiquesServiceImpl implements StatistiquesService {

    private final TacheRepository tacheRepository;
    private final EvenementRepository evenementRepository;

    @Override
    public StatistiquesDTO calculerPour(Long utilisateurId) {
        List<Tache> toutesLesTaches = tacheRepository.findByUtilisateurId(utilisateurId);
        LocalDateTime maintenant = LocalDateTime.now();

        // Semaine courante : lundi à dimanche, même convention que la grille
        // calendrier de l'agenda.
        LocalDate aujourdHui = maintenant.toLocalDate();
        LocalDate debutSemaine = aujourdHui.minusDays(aujourdHui.getDayOfWeek().getValue() - 1);
        LocalDate finSemaine = debutSemaine.plusDays(6);

        List<Tache> tachesCetteSemaine = toutesLesTaches.stream()
                .filter(t -> t.getDateEcheance() != null)
                .filter(t -> {
                    LocalDate d = t.getDateEcheance().toLocalDate();
                    return !d.isBefore(debutSemaine) && !d.isAfter(finSemaine);
                })
                .toList();

        int tachesTotalSemaine = tachesCetteSemaine.size();
        int tachesTermineesSemaine = (int) tachesCetteSemaine.stream().filter(Tache::isTerminee).count();

        int tachesEnRetard = (int) toutesLesTaches.stream()
                .filter(t -> !t.isTerminee())
                .filter(t -> t.getDateEcheance() != null)
                .filter(t -> t.getDateEcheance().isBefore(maintenant))
                .count();

        // Taux global : sur toutes les tâches, avec ou sans échéance -
        // exclure les tâches sans échéance aurait été arbitraire, une tâche
        // reste "terminée" ou non indépendamment du fait qu'elle ait une
        // date limite précise.
        long total = toutesLesTaches.size();
        long terminees = toutesLesTaches.stream().filter(Tache::isTerminee).count();
        double tauxCompletionGlobal = (total == 0) ? 0.0 : (double) terminees / total;

        YearMonth moisCourant = YearMonth.from(aujourdHui);
        List<Evenement> evenementsCeMois = evenementRepository.findByUtilisateurIdAndDateDebutBetween(
                utilisateurId,
                moisCourant.atDay(1).atStartOfDay(),
                moisCourant.atEndOfMonth().plusDays(1).atStartOfDay()
        );

        return new StatistiquesDTO(
                tachesTermineesSemaine,
                tachesTotalSemaine,
                tachesEnRetard,
                tauxCompletionGlobal,
                evenementsCeMois.size()
        );
    }
}
