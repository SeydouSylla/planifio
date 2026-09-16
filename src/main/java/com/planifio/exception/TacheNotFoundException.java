package com.planifio.exception;

/* Levée aussi bien quand la tâche n'existe pas DU TOUT, que lorsqu'elle
existe mais appartient à un autre utilisateur. Ce choix est volontaire :
renvoyer une erreur différente ("403 Interdit") dans le second cas
révélerait à un attaquant qu'un identifiant précis existe bel et bien
en base, juste pour un autre compte - une fuite d'information à éviter.
Du point de vue de l'utilisateur connecté, les deux cas doivent être
strictement indiscernables.
*/

public class TacheNotFoundException extends RuntimeException {
    public TacheNotFoundException(Long id) {
        super("Tâche introuvable : " + id);
    }
}
