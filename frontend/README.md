# Fuel Station - Sistema de Gestão Frontend

A modern React + TypeScript frontend for the Fuel Station management system, built with Material UI, TanStack Query, and Vite.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser / Vite Dev Server                │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                        React App                         │  │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐ │  │
│  │  │  React      │  │  TanStack    │  │   Zustand       │ │  │
│  │  │  Router v6  │  │  Query v5    │  │   UI Store      │ │  │
│  │  │  (Routing)  │  │  (Cache)     │  │   (Sidebar,     │ │  │
│  │  └─────────────┘  └──────────────┘  │    Toasts)      │ │  │
│  │                                     └─────────────────┘ │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │                   Pages / Components                │ │  │
│  │  │  Dashboard  │  FuelTypes  │  FuelPumps  │ Fuelings  │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │             React Hook Form + Zod                   │ │  │
│  │  │               (Form Validation)                     │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │               Axios (HTTP Client)                   │ │  │
│  │  └───────────────────────┬─────────────────────────────┘ │  │
│  └──────────────────────────┼────────────────────────────────┘  │
│                             │ /api/v1 (proxied)                  │
└─────────────────────────────┼───────────────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │  Spring Boot API   │
                    │  localhost:8080    │
                    └────────────────────┘
```

## Tech Stack

| Technology | Version | Rationale |
|---|---|---|
| **React** | 18.x | Industry-standard UI library with concurrent features |
| **TypeScript** | 5.x | Type safety, better DX, fewer runtime errors |
| **Vite** | 5.x | Fast HMR, optimized builds, modern tooling |
| **Material UI** | 6.x | Comprehensive component library, consistent design |
| **TanStack Query** | 5.x | Server state caching, background refetch, stale-while-revalidate |
| **Zustand** | 5.x | Minimal, scalable client state (UI state: sidebar, toasts) |
| **React Router** | 6.x | Declarative routing with nested layouts |
| **React Hook Form** | 7.x | Performant forms with minimal re-renders |
| **Zod** | 3.x | Schema-based runtime validation with TypeScript inference |
| **Axios** | 1.x | HTTP client with interceptors for error handling |
| **Recharts** | 2.x | Composable chart library built on D3 |
| **date-fns** | 4.x | Lightweight date utility library |

## Getting Started

### Prerequisites

- Node.js 18+ (LTS recommended)
- npm 9+
- Backend API running at `http://localhost:8080`

### Installation

```bash
cd frontend
npm install
```

### Development

```bash
npm run dev
```

The app will be available at `http://localhost:3000`. API requests to `/api/*` are proxied to `http://localhost:8080`.

### Build

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

### Lint

```bash
npm run lint
```

## Available Scripts

| Script | Description |
|---|---|
| `npm run dev` | Start Vite dev server on port 3000 with HMR |
| `npm run build` | TypeScript check + production build to `dist/` |
| `npm run preview` | Preview the production build locally |
| `npm run lint` | ESLint check with TypeScript rules |

## API Integration

The Vite dev server proxies `/api` requests to `http://localhost:8080`, so no CORS configuration is needed during development.

### Endpoints Used

| Resource | Endpoints |
|---|---|
| Fuel Types | `GET/POST /api/v1/fuel-types`, `GET/PUT/DELETE /api/v1/fuel-types/{id}` |
| Fuel Pumps | `GET/POST /api/v1/fuel-pumps`, `GET/PUT/DELETE /api/v1/fuel-pumps/{id}` |
| Fuelings | `GET/POST /api/v1/fuelings`, `GET/PUT/DELETE /api/v1/fuelings/{id}` |
| Fuelings (filtered) | `GET /api/v1/fuelings?pumpId=&startDate=&endDate=` |

### Error Handling

Axios interceptors catch all API errors and extract the `message` field from the response. Errors are displayed as toast notifications via the Zustand UI store.

## Folder Structure

```
src/
├── api/              # Axios config + per-resource API modules
├── components/
│   ├── common/       # Reusable: ConfirmDialog, LoadingOverlay, ErrorAlert
│   └── layout/       # AppLayout, TopBar, Sidebar, Footer
├── hooks/            # TanStack Query hooks (CRUD per resource)
├── pages/            # Route-level page components + form modals
│   ├── Dashboard/    # Stats cards + Recharts visualizations
│   ├── FuelTypes/    # DataGrid + form modal
│   ├── FuelPumps/    # DataGrid + form modal
│   └── Fuelings/     # DataGrid + filters + form modal
├── router/           # React Router configuration
├── stores/           # Zustand stores (UI state)
├── theme/            # MUI theme customization
├── types/            # TypeScript interfaces matching backend DTOs
└── utils/            # Formatters (currency, date, liters)
```

## Features

- **Dashboard**: Summary stats cards, bar chart (fuelings per pump), pie chart (pumps by fuel type), recent fuelings list, fuel price chips
- **Fuel Types**: CRUD with DataGrid, price per liter display
- **Fuel Pumps**: CRUD with multi-select fuel type association
- **Fuelings**: CRUD with filters (pump, date range), paginated DataGrid
- **Responsive**: Mobile-friendly with temporary drawer on small screens
- **Notifications**: Auto-dismissing toast snackbars for success/error feedback
- **Form Validation**: Zod schemas with real-time error messages

## Screenshots

> _Dashboard showing stats cards and charts_
> ![Dashboard](docs/screenshots/dashboard.png)

> _Fuel Types management with DataGrid_
> ![Fuel Types](docs/screenshots/fuel-types.png)

> _Fuelings page with date/pump filters_
> ![Fuelings](docs/screenshots/fuelings.png)

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes following [Conventional Commits](https://www.conventionalcommits.org/)
4. Push and open a Pull Request

### Code Style

- Prettier is configured (`.prettierrc`) — run format before committing
- ESLint enforces TypeScript + React Hooks rules
- All components use `React.FC` with explicit prop interfaces
- TanStack Query for all server state; Zustand only for UI state
