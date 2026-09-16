package com.planifio.service;

import com.planifio.dto.EvenementDTO;
import com.planifio.dto.JourCalendrier;
import com.planifio.exception.EvenementNotFoundException;
import com.planifio.mapper.EvenementMapper;
import com.planifio.model.Evenement;
import com.planifio.model.Utilisateur;
import com.planifio.repository.EvenementRepository;
import com.planifio.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public List<List<JourCalendrier>> grilleDuMois(YearMonth mois, Long utilisateurId) {
        LocalDate premierJourDuMois = mois.atDay(1);
        LocalDate dernierJourDuMois = mois.atEndOfMonth();

        // Recule jusqu'au lundi qui précède (ou égale) le 1er du mois -
        // DayOfWeek.getValue() vaut 1 pour lundi, 7 pour dimanche.
        int decalageDebut = premierJourDuMois.getDayOfWeek().getValue() - 1;
        LocalDate debutGrille = premierJourDuMois.minusDays(decalageDebut);

        // Avance jusqu'au dimanche qui suit (ou égale) le dernier jour du mois.
        int decalageFin = DayOfWeek.SUNDAY.getValue() - dernierJourDuMois.getDayOfWeek().getValue();
        LocalDate finGrille = dernierJourDuMois.plusDays(decalageFin);

        // Une seule requête pour TOUTE la grille - jamais une requête par jour.
        List<Evenement> evenements = repository.findByUtilisateurIdAndDateDebutBetween(
                utilisateurId,
                debutGrille.atStartOfDay(),
                finGrille.plusDays(1).atStartOfDay()
        );

        // Regroupe les événements par jour, pour un accès direct O(1) lors
        // de la construction de chaque case du calendrier ci-dessous.
        Map<LocalDate, List<EvenementDTO>> parJour = evenements.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDateDebut().toLocalDate(),
                        Collectors.mapping(EvenementMapper::versDto, Collectors.toList())
                ));

        List<List<JourCalendrier>> semaines = new ArrayList<>();
        List<JourCalendrier> semaineCourante = new ArrayList<>();
        LocalDate aujourdHui = LocalDate.now();

        for (LocalDate jour = debutGrille; !jour.isAfter(finGrille); jour = jour.plusDays(1)) {
            boolean dansLeMoisCourant = YearMonth.from(jour).equals(mois);
            List<EvenementDTO> evenementsDuJour = parJour.getOrDefault(jour, List.of());

            semaineCourante.add(new JourCalendrier(jour, dansLeMoisCourant, jour.equals(aujourdHui), evenementsDuJour));

            if (semaineCourante.size() == 7) {
                semaines.add(semaineCourante);
                semaineCourante = new ArrayList<>();
            }
        }

        return semaines;
    }

    @Override
    public EvenementDTO creer(String titre, String description, LocalDateTime debut,
                               LocalDateTime fin, Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.getReferenceById(utilisateurId);

        Evenement evenement = new Evenement();
        evenement.setTitre(titre);
        evenement.setDescription(description);
        evenement.setDateDebut(debut);
        evenement.setDateFin(fin);
        evenement.setUtilisateur(utilisateur);

        return EvenementMapper.versDto(repository.save(evenement));
    }

    @Override
    public EvenementDTO modifier(Long id, String titre, String description,
                                  LocalDateTime debut, LocalDateTime fin, Long utilisateurId) {
        Evenement evenement = repository.findByIdAndUtilisateurId(id, utilisateurId)
                .orElseThrow(() -> new EvenementNotFoundException(id));

        evenement.setTitre(titre);
        evenement.setDescription(description);
        evenement.setDateDebut(debut);
        evenement.setDateFin(fin);

        return EvenementMapper.versDto(repository.save(evenement));
    }

    @Override
    public void supprimer(Long id, Long utilisateurId) {
        if (!repository.existsByIdAndUtilisateurId(id, utilisateurId)) {
            throw new EvenementNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EvenementDTO trouverParId(Long id, Long utilisateurId) {
        return repository.findByIdAndUtilisateurId(id, utilisateurId)
                .map(EvenementMapper::versDto)
                .orElseThrow(() -> new EvenementNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvenementDTO> evenementsAVenir(Long utilisateurId, int nombreDeJours) {
        LocalDateTime maintenant = LocalDateTime.now();
        return repository.findByUtilisateurIdAndDateDebutBetween(
                        utilisateurId, maintenant, maintenant.plusDays(nombreDeJours))
                .stream()
                .sorted((a, b) -> a.getDateDebut().compareTo(b.getDateDebut()))
                .map(EvenementMapper::versDto)
                .toList();
    }
}
