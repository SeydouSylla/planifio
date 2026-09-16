# 📅 Planifio - Gestion de tâches, agenda et rappels multi-utilisateurs

Application complète avec comptes utilisateurs, tâches, agenda, rappels automatiques, statistiques personnelles et détection de connexions suspectes — construite comme démonstration DevOps de bout en bout : Docker, Kubernetes, Helm, CI/CD, sans GitOps, sans stack de monitoring dédiée (voir `docs/ADR-002`).

## 🏗️ Fonctionnalités

| Fonctionnalité | Description |
|---|---|
| Comptes & authentification | Inscription, connexion, verrouillage après 5 échecs consécutifs |
| Tâches | CRUD complet, échéance, isolation stricte par utilisateur |
| Agenda | Vue calendrier mensuelle calculée côté serveur |
| Rappels automatiques | Job planifié quotidien, observable via les logs applicatifs |
| Mes statistiques | Page personnelle : tâches de la semaine, retard, taux de complétion |
| Détection de connexions suspectes | Verrouillage réel du compte, jamais un simple affichage |

## 🚀 Installation locale

```bash
git clone https://github.com/ton-username/planifio.git
cd planifio
chmod +x install.sh
./install.sh
```

Une seule commande : génère le secret local, construit et démarre tout.

- Application : http://localhost:8080

## ⚠️ Étape manuelle requise avant le premier `docker compose up` ou `./mvnw`

Ce projet n'a **pas encore de Maven Wrapper** (`mvnw`, `.mvn/`) - à générer une fois, sur une machine avec un accès réel à Maven Central :

```bash
mvn wrapper:wrapper
git add mvnw mvnw.cmd .mvn/
git commit -m "chore: ajoute le Maven Wrapper"
```

Sans cette étape, `./mvnw test` (utilisé par le pipeline CI) échouera - utiliser `mvn test` en attendant si Maven est installé globalement.

## ☸️ Déploiement Kubernetes — bootstrap initial

```bash
kubectl apply -f k8s/base/namespaces.yaml

kubectl create secret generic planifio-secret \
  --from-literal=dbPassword="$(openssl rand -hex 16)" -n planifio-staging
kubectl create secret generic planifio-secret \
  --from-literal=dbPassword="$(openssl rand -hex 16)" -n planifio-production
```

Chaque déploiement applicatif suivant est automatisé par `.github/workflows/ci-cd.yml`.

## 🩺 Observer l'application en production

Sans stack de monitoring dédiée, la supervision passe par les outils Kubernetes natifs :

```bash
kubectl get pods -n planifio-production
kubectl logs -l app=planifio -n planifio-production --tail=100 -f
curl https://planifio.ssylla.com/actuator/health
```

## 🔐 Secrets GitHub requis

| Secret | Contenu |
|---|---|
| `KUBE_CONFIG` | kubeconfig du cluster, encodé en base64 |

## 📁 Structure

```
planifio/
├── src/main/java/com/planifio/    # code de l'application
├── src/main/resources/            # config, migrations Flyway, templates
├── Dockerfile, docker-compose.yml
├── k8s/base/                       # manifestes Kubernetes statiques
├── helm/planifio/                  # chart applicatif
├── .github/workflows/ci-cd.yml
└── docs/                            # ADR
```

## 📖 Décisions d'architecture

Voir `docs/` - chaque choix structurant (verrouillage de compte, StatefulSet PostgreSQL plutôt que managé, absence de stack de monitoring, absence de GitOps) y est justifié.

## 📄 Licence

MIT.
