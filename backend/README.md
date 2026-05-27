# Tekizz — Backend

API REST structurée en **architecture DDD hexagonale** (Ports & Adapters), organisée en monolithe modulaire avec 6 bounded contexts isolés.

---

## Stack

| Technologie | Version | Rôle |
|-------------|---------|------|
| Spring Boot | 4.0.2 | Framework principal |
| Java | 21 | Langage |
| PostgreSQL | 16 | Base de données |
| JJWT | 0.12.3 | Authentification JWT |
| Spring Security OAuth2 | — | OAuth2 Google / GitHub |
| Lombok | — | Réduction du boilerplate |
| H2 | — | Base de données en mémoire (tests) |

---

## Architecture DDD Hexagonale

Chaque bounded context suit une structure stricte Ports & Adapters :

```
<context>/
├── domain/
│   ├── model/        # Agrégats, entités, value objects (pur Java, zéro annotation JPA)
│   │   └── vo/       # Value Objects immuables
│   ├── event/        # Domain Events (records immuables)
│   ├── repository/   # Ports secondaires sortants (interfaces)
│   └── service/      # Domain Services (logique inter-entités)
├── application/
│   ├── port/in/      # Use Cases (interfaces entrantes)
│   └── service/      # Application Services — implémentent les use cases
└── infrastructure/
    ├── persistence/
    │   ├── entity/      # @Entity JPA (séparés du domaine)
    │   ├── repository/  # Spring Data JPA + Adapters (implémentent les ports)
    │   └── mapper/      # JPA Entity ↔ Domain Model
    └── web/
        └── controller/  # @RestController fins — délèguent à l'application service
```

### Les 6 bounded contexts

| Contexte | Rôle |
|----------|------|
| `shared/` | Shared Kernel : `AggregateRoot`, `DomainEvent`, `DomainEventPublisher` |
| `iam/` | Identité & Accès — User, Auth JWT, OAuth2 Google/GitHub, rôles |
| `catalog/` | Catalogue de contenu — Category, Tag |
| `gaming/qcm/` | Core Domain — jeu QCM : sessions, questions, scoring, leaderboard |
| `gaming/smatch/` | Core Domain — Speed Matching : decks, paires, sessions |
| `contribution/` | Contributions communautaires (soumission → review → publication) |
| `admin/` | Contexte support — orchestre les autres contextes via leurs use cases |

---

## API — Endpoints principaux

### Auth (`/api/v1/auth`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/register` | Inscription email / password |
| POST | `/login` | Connexion → JWT |
| GET | `/oauth2/google` | OAuth2 Google |
| GET | `/oauth2/github` | OAuth2 GitHub |

### QCM (`/api/v1/qcm`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/sessions/start` | Démarrer une session de jeu |
| GET | `/sessions/{id}/question` | Récupérer la question suivante |
| POST | `/sessions/{id}/answer` | Soumettre une réponse |
| GET | `/sessions/{id}/result` | Résultat final de la session |
| GET | `/leaderboard` | Classement global |
| GET | `/stats` | Statistiques personnelles |

### Smatch (`/api/v1/smatch`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/decks` | Lister les decks disponibles |
| POST | `/sessions/start` | Démarrer une session Smatch |
| POST | `/sessions/{id}/attempt` | Soumettre une tentative de matching |
| GET | `/sessions/{id}/result` | Résultat final |

### Contribution (`/api/v1/contribution/questions`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/` | Soumettre une nouvelle question |
| POST | `/bulk` | Soumettre plusieurs questions d'un coup |
| GET | `/mine` | Consulter ses propres contributions |
| DELETE | `/{id}` | Retirer une contribution |

### Admin — rôle `ADMIN` requis (`/api/v1/admin`)

| Périmètre | Endpoints |
|-----------|-----------|
| Catégories QCM | `GET/POST/PUT/DELETE /admin/qcm/categories` |
| Questions QCM | `GET/POST/PUT/DELETE /admin/qcm/questions` |
| Tags | `GET/POST/PUT/DELETE /admin/qcm/tags` |
| Decks Smatch | `GET/POST/PUT/DELETE /admin/smatch/decks` |
| Utilisateurs | `GET/PUT/DELETE /admin/platform/users` |
| Statistiques | `GET /admin/platform/stats` |
| Contributions | `GET/PUT /admin/contributions` |

---

## Lancer le projet

### Option 1 — Docker Compose du backend seul

Lance PostgreSQL + le backend Spring Boot sans le frontend.

```bash
# Configurer les variables
cp .env.example .env

# Démarrer
docker compose up -d

# Voir les logs
docker compose logs -f backend

# Arrêter
docker compose down
```

### Option 2 — Docker Compose global (recommandé)

Lance l'ensemble de la stack (PostgreSQL + Backend + Frontend) depuis la racine du projet.

```bash
cd ..   # depuis le dossier backend/
docker compose up -d
```

### Développement local (sans Docker)

```bash
# Prérequis : Java 21 + PostgreSQL local configuré

cp .env.example .env
./mvnw spring-boot:run

# Lancer les tests
./mvnw test

# Build JAR
./mvnw clean package -DskipTests
```

### Variables d'environnement requises

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL PostgreSQL (ex: `jdbc:postgresql://localhost:5432/tekizz`) |
| `DB_USERNAME` | Nom d'utilisateur PostgreSQL |
| `DB_PASSWORD` | Mot de passe PostgreSQL |
| `APP_JWT_SECRET` | Clé secrète JWT (min 256 bits) |
| `APP_JWT_EXPIRATION` | Durée de vie du JWT en ms (ex: `86400000` pour 24h) |
| `GOOGLE_CLIENT_ID` | Client ID OAuth2 Google |
| `GOOGLE_CLIENT_SECRET` | Client Secret OAuth2 Google |
| `GITHUB_CLIENT_ID` | Client ID OAuth2 GitHub |
| `GITHUB_CLIENT_SECRET` | Client Secret OAuth2 GitHub |

---

Développé par [LesCracks-OS](https://github.com/LesCracks-OS)
