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
                // Routes publiques : accessibles sans être connecté
                .requestMatchers("/connexion", "/inscription", "/css/**", "/actuator/health").permitAll()
                // Toutes les autres routes exigent une session authentifiée
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/connexion")           // notre propre page, pas celle générée par défaut
                .loginProcessingUrl("/connexion")   // URL que le formulaire soumet en POST
                .defaultSuccessUrl("/taches", true)
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
