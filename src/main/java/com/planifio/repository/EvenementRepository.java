package com.planifio.repository;

import com.planifio.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    /*
     Utilisée pour la vue calendrier : récupère uniquement les événements
     dont la date de début tombe dans l'intervalle affiché - jamais un
     findByUtilisateurId() suivi d'un filtrage en mémoire, qui chargerait
     inutilement TOUT l'historique de l'utilisateur à chaque affichage.
     */
    List<Evenement> findByUtilisateurIdAndDateDebutBetween(
            Long utilisateurId, LocalDateTime debut, LocalDateTime fin);

    Optional<Evenement> findByIdAndUtilisateurId(Long id, Long utilisateurId);

    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);

    /*
     Même principe de JOIN FETCH que TacheRepository - évite une
     LazyInitializationException lors de l'accès à l'email du propriétaire
     depuis le job de rappels, potentiellement exécuté hors transaction.
     */
    @Query("SELECT e FROM Evenement e JOIN FETCH e.utilisateur " +
           "WHERE e.dateDebut BETWEEN :debut AND :fin")
    List<Evenement> trouverEvenementsProches(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
