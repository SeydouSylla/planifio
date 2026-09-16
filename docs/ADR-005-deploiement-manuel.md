# ADR-005 : Déploiement manuel via Helm, sans automatisation du CD

## Contexte
Le pipeline d'intégration continue teste le code et construit l'image applicative
à chaque fusion, puis la publie sur le registre d'images. Reste à décider comment
cette image est déployée sur le cluster : de façon automatique par le pipeline, ou
manuellement par un opérateur. Deux contraintes encadrent ce choix. D'une part,
l'API du cluster (port 6443) est volontairement fermée au réseau extérieur par le
pare-feu, pour ne pas exposer l'organe de commande du cluster. D'autre part, le
dépôt du projet a vocation à être rendu public, ce qui écarte certaines solutions
d'automatisation reposant sur un agent exécuté localement.

## Décision
La version actuelle sépare l'intégration continue de la livraison. L'intégration
continue reste entièrement automatique : tests et construction de l'image à chaque
fusion. Le déploiement, lui, est manuel : un opérateur, connecté au cluster,
exécute `helm upgrade --install` pour mettre en ligne la version voulue, dans le
namespace correspondant à l'environnement (`planifio-staging` ou
`planifio-production`).

## Justification
- **Le pare-feu reste fermé.** Le déploiement manuel s'effectue depuis l'intérieur
  du cluster, via l'accès local à l'API. Aucun port sensible n'est ouvert vers
  l'extérieur, ce qui préserve la posture de sécurité établie pour le serveur.
- **Compatibilité avec un dépôt public.** Un runner auto-hébergé, qui exécuterait
  les workflows sur la machine hôte, présente un risque sur un dépôt public : une
  contribution externe malveillante pourrait s'exécuter sur le serveur. Le
  déploiement manuel élimine ce risque.
- **Maîtrise du moment de déploiement.** Déclencher la livraison manuellement
  permet de choisir quand mettre en production, de valider en staging au préalable,
  et d'éviter des déploiements non souhaités.
- **Simplicité.** Aucune brique supplémentaire (secret d'accès au cluster, agent,
  opérateur GitOps) n'est nécessaire, ce qui réduit la surface de maintenance.

## Conséquences
- La dernière étape de mise en ligne n'est pas automatisée : elle requiert une
  action explicite de l'opérateur après chaque fusion à déployer.
- Aucun secret d'accès au cluster (type `KUBE_CONFIG`) n'est stocké côté plateforme
  d'intégration, ce qui supprime cette surface de confiance.
- L'intégration continue demeure visible et automatisée : l'état des tests et des
  images publiées reste consultable publiquement, ce qui documente la qualité du
  projet indépendamment du mode de déploiement.

## Évolution envisagée
Une automatisation complète du déploiement pourra être étudiée si le contexte
évolue, en privilégiant les approches qui n'exposent pas l'API du cluster.

| Évolution possible | Apport attendu | Condition de déclenchement |
|---|---|---|
| Runner auto-hébergé sur le serveur | Déploiement automatique sans ouvrir de port | Retour à un dépôt privé, ou restriction stricte des workflows aux branches protégées |
| GitOps (ArgoCD ou Flux) | Réconciliation continue, API du cluster non exposée | Besoin d'automatisation robuste, éventuellement multi-environnements |
