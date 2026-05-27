# Tekizz

Plateforme de formation tech gamifiée. Apprends la programmation à travers deux modes de jeu interactifs : des QCM chronométrés et du Speed Matching (Smatch). Le tout avec un système de contribution communautaire, des classements globaux et un espace d'administration complet.

---

## Stack

| Couche | Technologie |
|--------|-------------|
| Frontend | React 19, Vite 7, Tailwind CSS 4, Framer Motion |
| Backend | Spring Boot 4, Java 21, JPA / Hibernate |
| Base de données | PostgreSQL 16 |
| Authentification | JWT + OAuth2 (Google, GitHub) |
| Infra | Docker, Docker Compose |

---

## Structure du projet

```
Tekizz/
├── backend/           # API REST — architecture DDD hexagonale (Java / Spring Boot)
├── frontend/          # SPA React
└── docker-compose.yml # Lance l'ensemble de la stack (PostgreSQL + Backend + Frontend)
```

---

## Lancer en local

### Via Docker Compose (recommandé)

La façon la plus simple de démarrer l'intégralité du projet en une seule commande.

**Prérequis** : Docker et Docker Compose installés.

```bash
# 1. Cloner le dépôt
git clone https://github.com/LesCracks-OS/Tekizz.git
cd Tekizz

# 2. Configurer les variables d'environnement
cp backend/.env.example backend/.env
# Ouvrir backend/.env et renseigner les valeurs (DB, JWT, OAuth2…)

# 3. Démarrer toute la stack
docker compose up -d
```

Docker Compose démarre automatiquement PostgreSQL, le backend Spring Boot et le frontend React/Nginx. Aucune installation de Java, Node ou PostgreSQL requise sur la machine.

### Développement séparé (frontend / backend indépendants)

Si tu travailles uniquement sur une partie du projet, consulte :
- [backend/README.md](backend/README.md) pour lancer le backend seul
- [frontend/README.md](frontend/README.md) pour lancer le frontend seul

---

Développé par [LesCracks-OS](https://github.com/LesCracks-OS)
