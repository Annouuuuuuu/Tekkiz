# Tekizz

Plateforme de formation tech gamifiée. Les utilisateurs apprennent la programmation à travers deux modes de jeu — QCM et Speed Matching (Smatch) — avec système de contribution communautaire, classements et espace admin complet.

---

## Stack

| Couche | Technologie |
|--------|-------------|
| Frontend | React 19, Vite 7, Tailwind CSS 4, Framer Motion |
| Backend | Spring Boot 4, Java 21, JPA/Hibernate |
| Base de données | PostgreSQL 16 |
| Auth | JWT + OAuth2 (Google, GitHub) |
| Infra | Docker, Docker Compose |

---

## Structure du projet

```
Tekizz/
├── backend/      # API REST — architecture DDD hexagonale
├── frontend/     # SPA React
└── docker-compose.yml
```

---

## Lancer en local

### Prérequis
- Docker et Docker Compose installés

### Démarrage

```bash
cp backend/.env.example backend/.env
# Renseigner les variables dans backend/.env

docker compose up -d
```

Docker Compose démarre automatiquement PostgreSQL, le backend et le frontend. Aucune installation manuelle de Java ou Node n'est nécessaire.

---

Développé par [LesCracks-OS](https://github.com/LesCracks-OS)
