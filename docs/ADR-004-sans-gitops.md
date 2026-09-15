# ADR-004 : Déploiement continu piloté par le pipeline, sans GitOps

## Contexte
Une fois l'image applicative construite, il faut la déployer sur le cluster. Deux
approches sont possibles. La première consiste à laisser le pipeline
d'intégration continue exécuter lui-même le déploiement sur le cluster. La
seconde, dite GitOps, introduit un opérateur installé dans le cluster (par
exemple ArgoCD ou Flux) qui surveille en continu le dépôt Git et réconcilie
automatiquement l'état réel du cluster avec l'état déclaré. Il faut choisir
l'approche adaptée à la version actuelle de Planifio.

## Décision
Le déploiement s'effectue par un `helm upgrade --install` exécuté directement
depuis le pipeline GitHub Actions, sans opérateur GitOps surveillant le cluster.

## Justification
- **Proportionnalité au contexte.** Le principal apport de GitOps — réconciliation
  continue et détection de dérive — prend surtout son sens sur des clusters
  importants, multi-équipes, avec des déploiements fréquents. Sur un cluster
  mono-nœud à faible trafic, administré par une seule personne, ce bénéfice reste
  marginal au regard de la complexité ajoutée.
- **Simplicité opérationnelle.** Piloter le déploiement depuis le pipeline évite
  d'installer et de maintenir un composant supplémentaire dans le cluster, ce qui
  réduit la surface d'exploitation pour la version actuelle.
- **Reproductibilité préservée.** La propriété essentielle recherchée est conservée
  indépendamment de l'outillage : chaque déploiement est épinglé à une image
  immuable (`image.tag=production-<sha du commit>`) plutôt qu'à un tag mutable
  comme `latest`, ce qui garantit qu'un déploiement donné correspond toujours
  exactement au même artefact.

## Conséquences
- Absence de correction automatique de dérive : une modification manuelle du
  cluster (`kubectl edit`) n'est pas rétablie automatiquement et ne serait
  reprise qu'au déploiement suivant passant par le pipeline.
- Le pipeline dispose d'un accès direct au cluster via un secret (`KUBE_CONFIG`).
  Cela définit un périmètre de confiance différent de celui d'un opérateur GitOps,
  qui agit depuis l'intérieur du cluster en lisant le dépôt. Cet accès doit être
  restreint et protégé en conséquence.

## Évolution envisagée
L'adoption d'une approche GitOps constitue une évolution pertinente si le
contexte d'exploitation change. Elle est écartée de la version actuelle pour en
limiter la complexité, et documentée comme piste future.

| Évolution possible | Apport attendu | Condition de déclenchement |
|---|---|---|
| ArgoCD ou Flux (GitOps) | Réconciliation continue, détection et correction de dérive | Passage à un cluster multi-nœuds ou multi-équipes, déploiements fréquents |
| Restriction renforcée de l'accès du pipeline | Réduction de la surface de confiance de `KUBE_CONFIG` | Ouverture du dépôt à d'autres contributeurs |
