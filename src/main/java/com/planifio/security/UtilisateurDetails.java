package com.planifio.security;

import com.planifio.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


//Adapte l'entité JPA Utilisateur à l'interface UserDetails de Spring Security.

@RequiredArgsConstructor
public class UtilisateurDetails implements UserDetails {

    private final Utilisateur utilisateur;

    public Long getUtilisateurId() {
        return utilisateur.getId();
    }

    public String getNom() {
        return utilisateur.getNom();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Un seul rôle pour l'instant - pas de distinction admin/utilisateur
        // dans cette phase du projet.
        return List.of(new SimpleGrantedAuthority("ROLE_UTILISATEUR"));
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasseHache();
    }

    @Override
    public String getUsername() {
        return utilisateur.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return utilisateur.getTentativesEchoueesConsecutives() < ConnexionSecuriteService.SEUIL_VERROUILLAGE;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
