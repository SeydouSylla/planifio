package com.planifio.repository;

import com.planifio.model.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TacheRepository extends JpaRepository<Tache, Long> {

    List<Tache> findByUtilisateurId(Long utilisateurId);

    Optional<Tache> findByIdAndUtilisateurId(Long id, Long utilisateurId);

    boolean existsByIdAndUtilisateurId(Long id, Long utilisateurId);

    @Query("SELECT t FROM Tache t JOIN FETCH t.utilisateur " +
           "WHERE t.dateEcheance BETWEEN :debut AND :fin AND t.terminee = false")
    List<Tache> trouverTachesAEcheanceProche(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
