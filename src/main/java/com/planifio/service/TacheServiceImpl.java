package com.planifio.service;

import com.planifio.dto.TacheDTO;
import com.planifio.exception.TacheNotFoundException;
import com.planifio.mapper.TacheMapper;
import com.planifio.model.Priorite;
import com.planifio.model.Tache;
import com.planifio.model.Utilisateur;
import com.planifio.repository.TacheRepository;
import com.planifio.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TacheServiceImpl implements TacheService {
    private final TacheRepository repository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TacheDTO> listerToutes(Long utilisateurId) {
        // Filtre systématique par propriétaire - jamais un simple findAll().
        return repository.findByUtilisateurId(utilisateurId).stream()
                .map(TacheMapper::versDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TacheDTO> tachesDuJour(Long utilisateurId) {
        // Logique métier déplacée ici depuis AccueilController - un
        // contrôleur ne doit jamais porter de règle métier ("qu'est-ce
        // qu'une tâche du jour ?"), seulement orchestrer l'appel au service
        // et préparer la vue.
        java.time.LocalDate aujourdHui = java.time.LocalDate.now();
        return repository.findByUtilisateurId(utilisateurId).stream()
                .filter(t -> !t.isTerminee())
                .filter(t -> t.getDateEcheance() != null && t.getDateEcheance().toLocalDate().equals(aujourdHui))
                .map(TacheMapper::versDto)
                .toList();
    }

    @Override
    public TacheDTO creer(String titre, LocalDateTime dateEcheance, Priorite priorite, String categorie, Long utilisateurId) {
        // getReferenceById : récupère une référence "légère" (proxy) sans
        // charger l'utilisateur entier depuis la base - on a seulement
        // besoin de son identifiant pour établir la relation, pas de son
        // email ni de son mot de passe haché à cet instant précis.
        Utilisateur utilisateur = utilisateurRepository.getReferenceById(utilisateurId);

        Tache tache = new Tache();
        tache.setTitre(titre);
        tache.setDateEcheance(dateEcheance);
        tache.setPriorite(priorite);
        tache.setCategorie(categorie);
        tache.setUtilisateur(utilisateur);
        return TacheMapper.versDto(repository.save(tache));
    }

    @Override
    public TacheDTO modifier(Long id, String titre, LocalDateTime dateEcheance, Priorite priorite, String categorie, Long utilisateurId) {
        Tache tache = repository.findByIdAndUtilisateurId(id, utilisateurId)
                .orElseThrow(() -> new TacheNotFoundException(id));
        tache.setTitre(titre);
        tache.setDateEcheance(dateEcheance);
        tache.setPriorite(priorite);
        tache.setCategorie(categorie);
        return TacheMapper.versDto(repository.save(tache));
    }

    @Override
    public TacheDTO terminer(Long id, Long utilisateurId) {
        Tache tache = repository.findByIdAndUtilisateurId(id, utilisateurId)
                .orElseThrow(() -> new TacheNotFoundException(id));
        tache.setTerminee(true);
        return TacheMapper.versDto(repository.save(tache));
    }

    @Override
    public void supprimer(Long id, Long utilisateurId) {
        if (!repository.existsByIdAndUtilisateurId(id, utilisateurId)) {
            throw new TacheNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TacheDTO trouverParId(Long id, Long utilisateurId) {
        return repository.findByIdAndUtilisateurId(id, utilisateurId)
                .map(TacheMapper::versDto)
                .orElseThrow(() -> new TacheNotFoundException(id));
    }
}
