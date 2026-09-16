# ADR-001 : Héberger PostgreSQL dans le cluster Kubernetes

## Contexte
Planifio a besoin d'une base de données PostgreSQL qui conserve ses données
dans le temps (comptes, tâches, événements). Il faut choisir où et comment
faire tourner cette base. Deux grandes options se présentent : utiliser une
base de données managée fournie par un hébergeur (comme OVH Managed Database),
ou installer PostgreSQL directement dans le cluster Kubernetes, avec un disque
persistant pour garder les données.

## Décision
PostgreSQL tourne à l'intérieur du cluster k3s, via un `StatefulSet` d'un seul
réplica, avec un `PersistentVolumeClaim` de 2 Gi en production et 1 Gi en
staging pour stocker les données.

## Justification
- **Simplicité et coût.** Le projet tourne sur un seul VPS. Ajouter une base
  managée externe voudrait dire une facture supplémentaire chaque mois, pour un
  bénéfice qui n'est pas justifié à l'échelle d'un projet de cette taille.
- **Tout reste dans le cluster.** En déployant la base dans Kubernetes, toute
  l'application est décrite au même endroit, avec les mêmes outils (`kubectl`,
  Helm). C'est plus cohérent et plus facile à comprendre.
- **Pourquoi un `StatefulSet` et pas un `Deployment`.** Un `Deployment` considère
  ses pods comme interchangeables : il peut les recréer sans garder le même
  disque, ce qui ferait perdre les données. Un `StatefulSet` donne au pod une
  identité stable et lui réattache toujours le même volume, même après un
  redémarrage. C'est exactement ce qu'il faut pour une base de données.

## Conséquences
- La base n'a pas de haute disponibilité : un seul réplica, sur un seul nœud.
  Si le VPS tombe, la base tombe avec. C'est un choix assumé, cohérent avec
  l'idée d'un seul serveur.
- Les sauvegardes ne sont pas automatiques. Il faudrait les gérer à la main
  (un `pg_dump` régulier, ou un snapshot du disque chez l'hébergeur). C'est une
  limite connue et documentée, pas un oubli.

## Alternatives envisagées
| Option | Pourquoi elle n'a pas été retenue |
|---|---|
| Base de données managée (OVH Managed Database) | Coût mensuel supplémentaire non justifié à cette échelle |
| PostgreSQL installé sur le VPS, en dehors de Kubernetes | Casse la cohérence « tout est déclaré dans le cluster » et complique le déploiement |
