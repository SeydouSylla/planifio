package com.planifio.dto;

import com.planifio.model.Priorite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/*
 Contrairement à InscriptionForm, ce formulaire n'a pas besoin de
 dissocier ses champs de l'entité pour des raisons de sécurité (aucun
 mot de passe ici) - la séparation existe malgré tout pour la même
 raison structurelle que partout ailleurs dans ce projet : jamais lier
 directement une entité JPA à un formulaire web.
 */
@Getter
@Setter
@NoArgsConstructor
public class TacheFormulaire {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    private LocalDateTime dateEcheance;

    private Priorite priorite;

    @Size(max = 100, message = "La catégorie ne peut pas dépasser 100 caractères")
    private String categorie;
}
