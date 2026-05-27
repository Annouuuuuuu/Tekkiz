# Tekizz — Frontend

SPA React pour la plateforme de formation tech gamifiée Tekizz.

---

## Stack

| Technologie | Version |
|-------------|---------|
| React | 19 |
| Vite | 7 |
| Tailwind CSS | 4 |
| React Router | 7 |
| Framer Motion | 12 |
| Radix UI | — |
| Recharts | 3 |
| Lucide React | — |

---

## Structure

```
src/
├── pages/
│   ├── dashboard/       # Play, QcmGame*, SmatchGame*, Performance, Leaderboard, Contribute, Settings
│   ├── admin/
│   │   ├── qcm/         # Categories, Questions, Tags, Sessions, Config, Contributions
│   │   └── smatch/      # Decks, DeckEditor, Sessions, Config
│   ├── Home.jsx
│   ├── Login.jsx / Signup.jsx
│   └── OAuthCallback.jsx
├── components/
│   ├── ui/              # Composants Radix UI (Button, Card, Select, Tabs…)
│   ├── layout/          # Navbar, Sidebar, Footer
│   ├── dashboard/       # Composants Play et Performance
│   ├── admin/           # Composants admin réutilisables
│   └── auth/            # Guards et formulaires
├── contexts/            # AuthContext, ThemeContext
├── services/
│   ├── api/             # apiClient.js, endpoints.js, errorHandler.js
│   ├── auth.service.js
│   ├── qcmGame.service.js
│   ├── contribution.service.js
│   └── admin.service.js
├── layouts/             # DashboardLayout, AdminLayout
└── lib/                 # Utilitaires (cn, …)
```

---

## Lancer le projet

```bash
pnpm install
pnpm dev          # http://localhost:5173
pnpm build        # Build production dans dist/
pnpm preview      # Prévisualiser le build
```

Créer un `.env` à la racine du dossier `frontend/` :

```env
VITE_API_BASE_URL=http://localhost:8080
```

En production cette variable pointe vers `https://api.tekizz.lescracks.com`.

---

## Docker

L'image de production utilise un build multi-stage : Vite build → Nginx.

```bash
docker build -t brandoniscoding/frontend-tekizz:latest .
```

La configuration Nginx (`nginx.conf`) gère le routing SPA (fallback sur `index.html`) et le proxy `/api` vers le backend.

---

Développé par **Brandon Kamga**
