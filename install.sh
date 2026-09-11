#!/bin/bash
# install.sh - Installation automatisée de Planifio

set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "   INSTALLATION - PLANIFIO"

echo "[1/3] Vérification de Docker..."
if ! command -v docker &> /dev/null || ! docker compose version &> /dev/null; then
  echo "  Erreur: Docker (avec le plugin compose) n'est pas installé."
  echo "    https://docs.docker.com/engine/install/"
  exit 1
fi
echo "  Ok: Docker opérationnel"

echo ""
echo "[2/3] Configuration des secrets locaux..."
if [ -f .env ]; then
  echo "  Ok: Fichier .env déjà présent - conservé tel quel"
else
  cat > .env <<EOF
DB_PASSWORD=$(openssl rand -hex 16)
EOF
  echo "  Ok: Fichier .env généré avec des valeurs aléatoires sécurisées"
fi

echo ""
echo "[3/3] Démarrage de la stack (build + lancement)..."
docker compose up -d --build

echo "  En attente que l'application réponde..."
for i in $(seq 1 40); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
  [ "$CODE" = "200" ] && break
  sleep 2
done

echo ""
echo "   INSTALLATION TERMINÉE"
echo ""
echo "  Application : http://localhost:8080"
echo ""
echo "  Pour arrêter :  docker compose down"
