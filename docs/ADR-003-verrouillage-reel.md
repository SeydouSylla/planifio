# ADR-003 : Verrouillage effectif du compte après un seuil d'échecs

## Contexte
La détection de tentatives de connexion suspectes peut se traiter à deux
niveaux. Le premier consiste à journaliser les échecs, ce qui permet une analyse
a posteriori mais n'empêche pas l'attaque de se poursuivre. Le second consiste à
bloquer réellement l'accès au compte une fois un seuil d'échecs atteint, afin de
stopper activement une tentative d'accès par force brute. Il faut déterminer
lequel de ces deux niveaux la version actuelle doit mettre en œuvre.

## Décision
Après un seuil défini (`SEUIL_VERROUILLAGE`, fixé à 5) d'échecs consécutifs, la
méthode `UtilisateurDetails.isAccountNonLocked()` renvoie `false`. Spring Security
refuse alors toute connexion sur ce compte, y compris avec des identifiants
corrects, jusqu'à la réinitialisation du compteur par une connexion réussie
ultérieure.

## Justification
- **Un blocage effectif plutôt qu'un simple signalement.** Journaliser des échecs
  sans agir dessus documente une attaque sans la freiner. Le verrouillage réel
  interrompt une tentative par force brute au lieu de se contenter de l'observer.
- **Réutilisation du mécanisme natif du framework.** Le blocage s'appuie
  directement sur `isAccountNonLocked()`, un point d'extension déjà prévu par
  Spring Security, ce qui évite de maintenir une logique de verrouillage
  parallèle et réduit le risque d'erreur.
- **Non-divulgation d'information.** Le même message d'erreur est renvoyé pour un
  mot de passe incorrect et pour un compte verrouillé (voir `SecurityConfig`).
  Différencier les deux confirmerait à un attaquant l'existence d'un compte et
  l'atteinte du seuil, une information qu'il n'a pas lieu d'obtenir.

## Conséquences
- Compromis d'expérience utilisateur assumé : un utilisateur légitime qui échoue
  plusieurs fois se retrouve bloqué même après avoir retrouvé son mot de passe,
  la sécurité étant ici privilégiée.
- Le déverrouillage repose actuellement sur une connexion réussie ou une
  intervention manuelle en base. Aucun mécanisme de déverrouillage temporisé
  n'est prévu dans la version actuelle.

## Évolution envisagée
Un déverrouillage automatique après un délai constitue une amélioration
pertinente, écartée de la version actuelle pour en limiter la complexité, et
identifiée comme piste pour une version ultérieure.

| Évolution possible | Apport attendu | Condition de déclenchement |
|---|---|---|
| Déverrouillage temporisé (ex. après 15 minutes) | Rétablit l'accès légitime sans intervention manuelle | Besoin d'améliorer l'expérience utilisateur sans dégrader la sécurité |
| Notification à l'utilisateur lors d'un verrouillage | Informe le titulaire d'une tentative d'accès suspecte | Mise en place d'un canal d'envoi d'e-mails applicatif |
