package com.planifio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Routes publiques : accessibles sans être connecté.
                // "/style.css" en toutes lettres, pas "/css/**" — le fichier
                // est servi à la racine (src/main/resources/static/style.css),
                // jamais sous un sous-dossier /css/.
                .requestMatchers("/connexion", "/inscription", "/style.css", "/actuator/health").permitAll()
                // Toutes les autres routes exigent une session authentifiée
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/connexion")           // notre propre page, pas celle générée par défaut
                .loginProcessingUrl("/connexion")   // URL que le formulaire soumet en POST
                .defaultSuccessUrl("/taches", true)
                // Même message générique pour "mauvais mot de passe" ET "compte
                // verrouillé" (voir UtilisateurDetails.isAccountNonLocked) - un
                // choix de sécurité délibéré : révéler qu'un compte est
                // verrouillé confirmerait à un attaquant que cet email existe
                // et qu'il a atteint le seuil, une information qu'il n'a pas
                // besoin d'obtenir.
                .failureUrl("/connexion?erreur")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/connexion?deconnecte")
                .permitAll()
            );

        return http.build();
    }
}
