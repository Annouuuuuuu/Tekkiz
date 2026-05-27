# Tekizz

Plateforme de formation tech gamifiée. Les utilisateurs apprennent la programmation à travers deux modes de jeu — QCM et Speed Matching (Smatch) — avec système de contribution communautaire, classements et espace admin complet.

**Production** : [tekizz.lescracks.com](https://tekizz.lescracks.com) · API : [api.tekizz.lescracks.com](https://api.tekizz.lescracks.com)

---

## Stack

| Couche | Technologie |
|--------|-------------|
| Frontend | React 19, Vite 7, Tailwind CSS 4, Framer Motion |
| Backend | Spring Boot 4, Java 21, JPA/Hibernate |
| Base de données | PostgreSQL 16 |
| Auth | JWT + OAuth2 (Google, GitHub) |
| Déploiement | Docker, Traefik, VPS (CI/CD GitHub Actions) |

---

## Structure du projet

```
Tekizz/
├── backend/          # API REST — architecture DDD hexagonale
├── frontend/         # SPA React
├── docker-compose.prod.yml   # Stack production
└── .github/workflows/deploy.yml  # Pipeline CI/CD
```

---

## Lancer en local

### Prérequis
- Java 21, Maven wrapper (`./mvnw`)
- Node.js 20+, pnpm
- PostgreSQL (ou Docker)

### Backend

```bash
cd backend
cp .env.example .env   # renseigner les variables
./mvnw spring-boot:run
# API disponible sur http://localhost:8080
```

### Frontend

```bash
cd frontend
pnpm install
pnpm dev
# App disponible sur http://localhost:5173
```

---

## Déploiement

Un push sur `main` déclenche automatiquement le pipeline GitHub Actions :

1. Build + tests backend (Maven)
2. Build frontend (Vite)
3. Push des images Docker sur Docker Hub
4. SSH sur le VPS → `docker compose pull && docker compose up -d`

Pour un déploiement manuel via GitHub Actions : `Actions → CI/CD — Tekizz → Run workflow`.

**Variables GitHub Secrets requises** : `DOCKER_USERNAME`, `DOCKER_PASSWORD`, `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY`.

---

Développé par **Brandon Kamga**
