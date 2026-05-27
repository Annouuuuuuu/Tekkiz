# Tekizz — Backend

API REST Spring Boot structurée en **architecture DDD hexagonale** (Ports & Adapters).

---

## Stack

| Technologie | Version |
|-------------|---------|
| Spring Boot | 4.0.2 |
| Java | 21 |
| PostgreSQL | 16 |
| JJWT | 0.12.3 |
| Spring Security OAuth2 | — |
| Lombok | — |

---

## Architecture

Le backend est un **monolithe modulaire** organisé en 6 bounded contexts. Chaque contexte suit la structure hexagonale stricte :

```
<context>/
├── domain/
│   ├── model/        # Agrégats, entités, value objects (pur Java, zéro JPA)
│   ├── event/        # Domain Events
│   ├── repository/   # Ports secondaires (interfaces)
│   └── service/      # Domain Services
├── application/
│   ├── port/in/      # Use Cases (interfaces entrantes)
│   └── service/      # Application Services (implémentent les use cases)
└── infrastructure/
    ├── persistence/
    │   ├── entity/      # @Entity JPA (séparés du domaine)
    │   ├── repository/  # Spring Data JPA + Adapters
    │   └── mapper/      # JPA Entity ↔ Domain Model
    └── web/
        └── controller/  # @RestController (adapters HTTP fins)
```

### Les 6 contextes

| Contexte | Rôle |
|----------|------|
| `shared/` | Shared Kernel : `AggregateRoot`, `DomainEvent`, `DomainEventPublisher` |
| `iam/` | Identité & Accès : User, Auth JWT, OAuth2 Google/GitHub |
| `catalog/` | Catalogue : Category, Tag |
| `gaming/qcm/` | Core Domain — jeu QCM : sessions, questions, scoring, leaderboard |
| `gaming/smatch/` | Core Domain — Speed Matching : decks, paires, sessions |
| `contribution/` | Contributions communautaires (soumission → review → publication) |
| `admin/` | Contexte support : orchestre les autres via leurs use cases |

---

## API — Endpoints principaux

### Auth (`/api/v1/auth`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/register` | Inscription email/password |
| POST | `/login` | Connexion → JWT |
| GET | `/oauth2/google` | OAuth2 Google |
| GET | `/oauth2/github` | OAuth2 GitHub |

### QCM (`/api/v1/qcm`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/sessions/start` | Démarrer une session |
| GET | `/sessions/{id}/question` | Question suivante |
| POST | `/sessions/{id}/answer` | Soumettre une réponse |
| GET | `/sessions/{id}/result` | Résultat final |
| GET | `/leaderboard` | Classement global |
| GET | `/stats` | Stats personnelles |

### Smatch (`/api/v1/smatch`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/decks` | Lister les decks actifs |
| POST | `/sessions/start` | Démarrer une session |
| POST | `/sessions/{id}/attempt` | Soumettre une tentative |
| GET | `/sessions/{id}/result` | Résultat final |

### Contribution (`/api/v1/contribution/questions`)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/` | Soumettre une question |
| GET | `/mine` | Mes contributions |
| DELETE | `/{id}` | Retirer une contribution |

### Admin (`/api/v1/admin/*`) — rôle ADMIN requis
- `GET/POST/PUT/DELETE /admin/qcm/categories` — gestion catégories
- `GET/POST/PUT/DELETE /admin/qcm/questions` — gestion questions
- `GET/POST/PUT/DELETE /admin/smatch/decks` — gestion decks Smatch
- `GET/PUT/DELETE /admin/platform/users` — gestion utilisateurs
- `GET /admin/platform/stats` — statistiques globales
- `GET/PUT /admin/contributions` — review des contributions

---

## Lancer le projet

### Via Docker Compose (recommandé)

```bash
cp .env.example .env
# Renseigner les variables

docker compose up -d
```

### Développement local

```bash
cp .env.example .env
./mvnw spring-boot:run

# Tests
./mvnw test
```

### Variables d'environnement requises

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL PostgreSQL |
| `DB_USERNAME` / `DB_PASSWORD` | Identifiants base de données |
| `APP_JWT_SECRET` | Clé secrète JWT (min 256 bits) |
| `APP_JWT_EXPIRATION` | Durée JWT en ms (ex: `86400000`) |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | OAuth2 Google |
| `GITHUB_CLIENT_ID` / `GITHUB_CLIENT_SECRET` | OAuth2 GitHub |

---

Développé par [LesCracks-OS](https://github.com/LesCracks-OS)
