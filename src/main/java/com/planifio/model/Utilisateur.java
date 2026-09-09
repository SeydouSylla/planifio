package com.planifio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Adresse email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(nullable = false, unique = true)
    private String email;

    // Contient le hash BCrypt, jamais le mot de passe en clair.
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasseHache;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Compteur d'échecs de connexion consécutifs - base de la détection
    // de connexions suspectes
    @Column(nullable = false)
    private int tentativesEchoueesConsecutives = 0;
}
