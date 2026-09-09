package com.planifio.security;

import com.planifio.model.Utilisateur;
import com.planifio.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun compte pour cet email"));
        return new UtilisateurDetails(utilisateur);
    }
}
