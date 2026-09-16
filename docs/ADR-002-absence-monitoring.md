# ADR-002 : Se limiter aux logs applicatifs pour la version actuelle

## Contexte
Toute application déployée en production doit être observable : pouvoir vérifier
qu'elle fonctionne correctement, détecter les erreurs, et suivre l'exécution des
traitements planifiés. Cette observabilité peut s'appuyer sur des outils dédiés
comme Prometheus (collecte de métriques) et Grafana (visualisation et alerting),
ou sur des mécanismes plus légers déjà intégrés à l'application. La question est
de déterminer le niveau d'outillage adapté à la version actuelle de Planifio.

## Décision
La version actuelle ne déploie pas de stack de monitoring dédiée. L'observabilité
s'appuie sur les logs applicatifs, consultables via `kubectl logs`, et sur la
sonde `/actuator/health`, déjà utilisée par les probes de Kubernetes pour
surveiller l'état de l'application.

## Justification
- **Proportionnalité au besoin actuel.** Sur un déploiement mono-nœud à faible
  trafic, les logs applicatifs couvrent les besoins d'observabilité réels. Les
  événements significatifs sont déjà tracés : verrouillage d'un compte après un
  nombre d'échecs donné, fin d'exécution du job de rappels avec le volume traité.
- **Maîtrise de la complexité.** Ne pas introduire de bibliothèque de métriques
  maintient les composants applicatifs (`RappelScheduler`, `ConnexionSecuriteService`)
  simples et sans dépendance supplémentaire, ce qui réduit la surface de
  maintenance du projet.
- **Décision réversible.** Ce choix répond au périmètre de la version actuelle.
  L'introduction de Prometheus/Grafana reste possible dans une version ultérieure,
  si le trafic, le nombre de nœuds ou les exigences d'exploitation le justifient.

## Conséquences
- Absence d'alerting automatique : une condition comme un pic de tentatives de
  connexion échouées ne déclenche pas de notification et nécessite une lecture
  manuelle des logs.
- Absence d'historisation des tendances : aucune visualisation dans le temps
  (par exemple le volume de rappels envoyés par jour) ; seule la donnée
  instantanée est disponible dans les logs.
- Observabilité suffisante mais volontairement minimale, à faire évoluer en même
  temps que les besoins d'exploitation de l'application.

## Évolution envisagée
Dans une version future, si le besoin d'exploitation le justifie, la mise en
place d'une stack d'observabilité pourra être étudiée :

| Évolution possible | Apport attendu | Condition de déclenchement |
|---|---|---|
| Prometheus + Grafana | Métriques, tableaux de bord, alerting | Montée en trafic, exigence de supervision active |
| Logs centralisés (Loki, ELK) | Recherche et corrélation des logs multi-sources | Passage à plusieurs services ou plusieurs nœuds |
