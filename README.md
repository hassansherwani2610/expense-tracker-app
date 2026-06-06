<p align="center">
  <h1 align="center">💸 Expense Tracker App</h1>
  <p align="center">
    A production-ready, full-stack <strong>Expense Management System</strong> built on a <strong>Microservices Architecture</strong> with an AI-powered SMS parsing engine.
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java" />
    <img src="https://img.shields.io/badge/Spring_Boot-3.5.10-brightgreen?style=flat-square&logo=spring" />
    <img src="https://img.shields.io/badge/Python-3.11-blue?style=flat-square&logo=python" />
    <img src="https://img.shields.io/badge/React-19-61DAFB?style=flat-square&logo=react" />
    <img src="https://img.shields.io/badge/Apache_Kafka-7.5.0-black?style=flat-square&logo=apachekafka" />
    <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql" />
    <img src="https://img.shields.io/badge/Kong_Gateway-3.0-003459?style=flat-square&logo=kong" />
    <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker" />
  </p>
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Services Deep Dive](#-services-deep-dive)
  - [Auth Service](#1-auth-service-port-9898)
  - [User Service](#2-user-service-port-9810)
  - [Expense Service](#3-expense-service-port-9820)
  - [DS Service (AI Engine)](#4-ds-service-ai-engine-port-8010)
  - [Kong API Gateway](#5-kong-api-gateway-port-8000)
  - [React Frontend](#6-react-frontend-port-5173)
- [File & Folder Reference](#-file--folder-reference)
- [API Endpoints](#-api-endpoints)
- [Kafka Event Flow](#-kafka-event-flow)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)

---

## 🔍 Overview

Expense Tracker App is a **microservices-based web application** that allows users to:

- Register & log in with **JWT-based authentication** (access token + HttpOnly refresh token cookie)
- **Manually add, update, and view** their expenses through a secure dashboard
- **Automatically parse bank SMS messages** using an AI/LLM engine (Mistral AI via LangChain) — the app extracts transaction amount, merchant, and currency from a raw SMS and logs it as an expense, entirely without manual input
- All services communicate **asynchronously through Apache Kafka topics**, ensuring loose coupling and high scalability

The entire backend is containerized with **Docker Compose**, fronted by a **Kong API Gateway** for centralized routing, rate-limiting, and CORS enforcement.

---

## 🏗 System Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        React Frontend (Vite)                             │
│                       http://localhost:5173                              │
└────────────────────────────┬─────────────────────────────────────────────┘
                             │ HTTP (Axios)
                             ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                  Kong API Gateway  :8000                                 │
│   Routes: /auth  →  AuthService                                          │
│           /user  →  UserService       (+ Rate Limiting, CORS)           │
│           /expense → ExpenseService   (+ Rate Limiting)                  │
│           /v1/ds → DS Service         (+ Rate Limiting)                  │
└────┬──────────────┬──────────────────┬──────────────────┬────────────────┘
     │              │                  │                  │
     ▼              ▼                  ▼                  ▼
 AuthService    UserService       ExpenseService      DS Service
  :9898          :9810               :9820              :8010
  (Java)         (Java)              (Java)             (Python/Flask)
     │              │                  │                  │
     │              │                  │                  │ Mistral AI API
     │              │                  │                  │ (LangChain)
     │              ▼                  ▼                  │
     │          MySQL DB          MySQL DB                │
     │        (user_service)    (expense_service)         │
     │                                                    │
     ▼                                                    │
  MySQL DB                                                │
 (auth_service)                                           │
                                                          │
┌─────────────────────────────────────────────────────┐  │
│                  Apache Kafka (Confluent)            │◄─┘
│   Topics:                                           │
│     user_service  → AuthService → UserService       │
│     expense_service → DSService → ExpenseService    │
└─────────────────────────────────────────────────────┘
```

**Request Flow:**
1. All client HTTP requests hit **Kong Gateway (:8000)** first
2. Kong routes to the appropriate backend microservice
3. User/Expense services validate JWTs by calling the **Auth Service's `/auth/v1/validate`** endpoint (service-to-service)
4. **Kafka** handles all async event-driven communication between services
5. The **DS Service** uses **Mistral AI** (via LangChain) to parse SMS messages into structured expense data, then publishes to Kafka

---

## 🛠 Tech Stack

| Layer | Technology | Purpose |
|---|---|---|
| Frontend | React 19, Vite, Tailwind CSS, Framer Motion | SPA UI with animated page transitions |
| API Gateway | Kong Gateway 3.0 (DB-less) | Centralized routing, rate-limiting, CORS |
| Auth | Spring Boot 3.5, Spring Security, JWT (jjwt), BCrypt | Stateless auth with access + refresh tokens |
| User | Spring Boot 3.5, Spring Data JPA | User profile management |
| Expense | Spring Boot 3.5, Spring Data JPA | Expense CRUD operations |
| AI Engine | Python 3.11, Flask, LangChain, Mistral AI | LLM-powered SMS parsing for auto expense logging |
| Messaging | Apache Kafka (Confluent 7.5.0) + Zookeeper | Async event streaming between services |
| Database | MySQL 8.0 | Persistent storage (3 separate databases) |
| Containerization | Docker Compose | Full-stack orchestration |
| Build Tool | Gradle (Java), pip/setuptools (Python), npm (React) | Dependency management & packaging |

---

## 📁 Project Structure

```
Expense Tracker App/
│
├── backend/
│   ├── docker-compose.yml            # Full stack container orchestration
│   ├── init.sql                      # MySQL database initializer
│   │
│   ├── expense-tracker-app/
│   │   └── authservice/              # Auth Microservice (Spring Boot)
│   │       ├── src/main/java/com/eta/authservice/
│   │       │   ├── auth/             # Security filters & config
│   │       │   ├── controller/       # REST controllers
│   │       │   ├── entities/         # JPA entities (DB models)
│   │       │   ├── eventProducer/    # Kafka producers
│   │       │   ├── model/            # DTOs (data transfer objects)
│   │       │   ├── repository/       # Spring Data JPA interfaces
│   │       │   ├── request/          # Request payload POJOs
│   │       │   ├── response/         # Response payload POJOs
│   │       │   ├── serializer/       # Custom Kafka serializers
│   │       │   ├── service/          # Business logic layer
│   │       │   └── util/             # Utility/helper classes
│   │       ├── src/main/resources/
│   │       │   └── application.properties
│   │       ├── build.gradle
│   │       └── Dockerfile
│   │
│   ├── userservice/
│   │   └── userservice/              # User Microservice (Spring Boot)
│   │       ├── src/main/java/com/eta/userservice/
│   │       │   ├── auth/             # Auth validation filter
│   │       │   ├── config/           # App & BCrypt config
│   │       │   ├── controller/       # REST controllers
│   │       │   ├── deserializer/     # Custom Kafka deserializers
│   │       │   ├── entities/         # JPA entities
│   │       │   ├── eventConsumer/    # Kafka consumers
│   │       │   ├── model/            # DTOs
│   │       │   ├── repository/       # JPA repos
│   │       │   ├── response/         # Response POJOs
│   │       │   └── service/          # Business logic
│   │       ├── src/main/resources/
│   │       │   └── application.properties
│   │       ├── build.gradle
│   │       └── Dockerfile
│   │
│   ├── expenseservice/
│   │   └── expenseservice/           # Expense Microservice (Spring Boot)
│   │       ├── src/main/java/com/eta/expenseservice/
│   │       │   ├── auth/             # Auth validation filter
│   │       │   ├── config/           # App & security config
│   │       │   ├── controller/       # REST controllers
│   │       │   ├── deserializer/     # Custom Kafka deserializers
│   │       │   ├── entities/         # JPA entities
│   │       │   ├── eventConsumer/    # Kafka consumers
│   │       │   ├── model/            # DTOs
│   │       │   ├── repository/       # JPA repos
│   │       │   ├── response/         # Response POJOs
│   │       │   └── service/          # Business logic
│   │       ├── src/main/resources/
│   │       │   └── application.properties
│   │       ├── build.gradle
│   │       └── Dockerfile
│   │
│   ├── dsService/                    # AI/DS Microservice (Python/Flask)
│   │   ├── src/app/
│   │   │   ├── __init__.py           # Flask app entry point & Kafka producer
│   │   │   ├── config.py             # Flask configuration
│   │   │   ├── schema/
│   │   │   │   └── expense.py        # Pydantic schema for LLM structured output
│   │   │   ├── services/
│   │   │   │   ├── auth_service.py   # Token validation against Auth Service
│   │   │   │   ├── llm_service.py    # LangChain + Mistral AI integration
│   │   │   │   └── message_service.py# SMS routing & LLM invocation
│   │   │   └── utils/
│   │   │       └── message_util.py   # Bank SMS keyword detection
│   │   ├── setup.py                  # Package setup for pip installation
│   │   ├── requirements.txt          # Python dependencies
│   │   └── Dockerfile
│   │
│   └── kong/
│       ├── config/
│       │   └── kong.yml              # Kong declarative (DB-less) config
│       └── Dockerfile
│
└── Frontend/
    └── expense-tracker-app/          # React SPA
        ├── src/
        │   ├── App.jsx               # Root component: router + layout
        │   ├── main.jsx              # ReactDOM entry point
        │   ├── index.css             # Global styles
        │   ├── api/
        │   │   ├── axiosInstance.js  # Base Axios client (baseURL: Kong :8000)
        │   │   ├── axiosPrivate.js   # Auth interceptor (token inject + refresh)
        │   │   ├── authApi.js        # Auth-specific API calls
        │   │   ├── loginApi.js       # Login API call
        │   │   └── signupApi.js      # Signup API call
        │   ├── components/
        │   │   ├── background/
        │   │   │   └── Background.jsx    # Animated background component
        │   │   └── footer/
        │   │       └── Footer.jsx        # App footer
        │   ├── context/
        │   │   └── AuthProvider.jsx  # Global auth state (React Context)
        │   ├── pages/
        │   │   ├── login_page/
        │   │   │   └── Login.jsx     # Login form page
        │   │   ├── signup_page/
        │   │   │   └── Signup.jsx    # Registration form page
        │   │   ├── dashboard_page/
        │   │   │   └── Dashboard.jsx # Protected user dashboard
        │   │   ├── privacy_page/
        │   │   │   └── Privacy.jsx   # Privacy policy page
        │   │   ├── terms_page/
        │   │   │   └── Terms.jsx     # Terms of service page
        │   │   └── support_page/
        │   │       └── Support.jsx   # Support page
        │   └── utils/
        │       └── ProtectedRoute.jsx # Route guard for authenticated pages
        ├── index.html                # Vite HTML entry point
        ├── package.json              # Node.js dependencies
        ├── vite.config.js            # Vite + Tailwind + React plugin config
        └── eslint.config.js          # ESLint rules
```

---

## 🔬 Services Deep Dive

### 1. Auth Service (Port: 9898)

**Technology:** Spring Boot 3.5 · Spring Security · JWT (jjwt 0.11.5) · Kafka · MySQL

The authentication gateway of the entire system. Handles user registration, login, JWT issuance, token refresh via HttpOnly cookies, and token validation for all other services.

#### Key Files

| File | Responsibility |
|---|---|
| `AuthController.java` | `POST /auth/v1/signup` — registers user, issues JWT + refresh token. `GET /auth/v1/validate` — validates Bearer token; used by all downstream services |
| `TokenController.java` | `POST /auth/v1/login` — authenticates credentials, returns access token + sets `refreshToken` HttpOnly cookie. `POST /auth/v1/refreshToken` — reads cookie, verifies refresh token, issues new access token |
| `JwtService.java` | Handles all JWT operations: token generation (with `userId` claim), signature validation, claims extraction, expiry checking. Uses HMAC-SHA256 with configurable secret |
| `JwtAuthFilter.java` | `OncePerRequestFilter` — intercepts every request, extracts Bearer token, validates it, and sets `SecurityContextHolder` |
| `SecurityConfig.java` | Configures CORS (allows `localhost:5173`, `localhost:8000`), stateless sessions, public/protected routes, and chains `JwtAuthFilter` |
| `RefreshTokenService.java` | Creates and verifies DB-persisted refresh tokens with a 7-day expiry window |
| `UserDetailsServiceImpl.java` | Loads user from DB, handles signup logic (BCrypt password encoding), and user lookups |
| `UserInfoProducer.java` | Kafka producer — on successful signup, publishes a `UserInfoEvent` to the `user_service` topic so the User Service can create a profile |
| `UserInfoDtoSerializer.java` | Custom Kafka JSON serializer for `UserInfoDto` events |
| `ValidationUtil.java` | Input validation logic for username, password strength, email format, etc. |
| `UserInfo.java` / `RefreshToken.java` | JPA entities mapping to the `auth_service` MySQL database |

#### Token Flow
```
Signup/Login → AccessToken (15min, in-memory) + RefreshToken (7d, HttpOnly Cookie)
                       ↓
           Auto-refresh via /auth/v1/refreshToken
                       ↓
           Token validated by downstream services via /auth/v1/validate
```

---

### 2. User Service (Port: 9810)

**Technology:** Spring Boot 3.5 · Spring Data JPA · Kafka Consumer · MySQL

Manages user profile data (name, phone, email). Operates in two modes: via REST API (authenticated requests) and via Kafka events (triggered on signup).

#### Key Files

| File | Responsibility |
|---|---|
| `UserController.java` | `POST /user/v1/createUpdateUser` — upserts user profile. `GET /user/v1/getUser` — fetches profile by `userId` extracted from validated token |
| `UserService.java` | Business logic: creates or updates a user using Java functional operators (`Supplier`, `UnaryOperator`) for clean upsert logic |
| `AuthServiceConsumer.java` | Kafka consumer listening on `user_service` topic — receives `UserInfoDto` events from Auth Service on signup. Validates email/phone/userId before persisting |
| `AuthValidationFilter.java` | Calls `http://authservice:9898/auth/v1/validate` with the request's Bearer token. On success, sets `userId` and `username` as request attributes |
| `UserInfoDtoDeserializer.java` | Custom Kafka JSON deserializer for incoming `UserInfoDto` Kafka messages |
| `UserInfo.java` | JPA entity for the `user_service` MySQL database |

---

### 3. Expense Service (Port: 9820)

**Technology:** Spring Boot 3.5 · Spring Data JPA · Kafka Consumer · MySQL

Handles all expense CRUD operations. Receives expenses either from REST calls (manual entry via frontend) or from Kafka events (auto-parsed via DS Service).

#### Key Files

| File | Responsibility |
|---|---|
| `ExpenseController.java` | `GET /expense/v1/getExpense` — returns all expenses for the authenticated user. `POST /expense/v1/addExpense` — creates a new expense. `PUT /expense/v1/updateExpense` — updates an existing expense |
| `ExpenseService.java` | Business logic: CRUD operations on expenses. Defaults currency to `PKR` if not provided. Maps between `Expense` entity and `ExpenseDto` |
| `ExpenseConsumer.java` | Kafka listener on `expense_service` topic — consumes `ExpenseDto` events published by the DS Service and saves them as expenses |
| `AuthValidationFilter.java` | Same pattern as User Service — validates token with Auth Service and injects `userId` into request |
| `ExpenseDeserializer.java` | Custom Kafka JSON deserializer for incoming `ExpenseDto` events |
| `Expense.java` | JPA entity with fields: `externalId`, `userId`, `amount`, `merchant`, `currency`, `createdAt`, `updatedAt` |
| `ExpenseRepository.java` | Custom JPA queries: `findByUserId()`, `findByUserIdAndExternalId()` |

---

### 4. DS Service — AI Engine (Port: 8010)

**Technology:** Python 3.11 · Flask · LangChain · Mistral AI (`mistral-large-latest`) · Kafka Producer · Pydantic

The most technically unique service. Accepts raw bank SMS messages, uses an LLM to extract structured financial data, and publishes it to Kafka for automatic expense logging — zero manual input required.

#### Key Files

| File | Responsibility |
|---|---|
| `src/app/__init__.py` | Flask app entry point. Exposes `POST /v1/ds/message` — validates Bearer token by calling Auth Service, passes SMS to `MessageService`, serializes the result, adds `user_id`, and publishes to `expense_service` Kafka topic |
| `services/llm_service.py` | Core AI logic. Initializes `ChatMistralAI` with `mistral-large-latest` (temperature=0 for deterministic extraction). Builds a LangChain prompt chain with `ChatPromptTemplate`. Uses `.with_structured_output(schema=Expense)` to force the LLM to return a typed Pydantic object |
| `services/message_service.py` | Orchestrator — checks if the message is a bank SMS via `MessageUtil`, then invokes `LLMService` |
| `services/auth_service.py` | Makes an HTTP GET to `http://authservice:9898/auth/v1/validate` to verify the Bearer token before processing |
| `schema/expense.py` | Pydantic `BaseModel` defining the LLM's output structure: `amount`, `merchant`, `currency` — all optional strings |
| `utils/message_util.py` | `is_bank_sms()` — heuristic keyword detection to filter out non-financial messages before sending to the (paid) LLM API |
| `requirements.txt` | `Flask`, `kafka-python`, `langchain-core`, `langchain-mistralai`, `pydantic`, `python-dotenv`, `requests` |
| `setup.py` | Packages the Flask app as a Python distribution (`ds_service-1.0.tar.gz`) for Docker installation |

#### AI Processing Pipeline
```
Raw SMS → is_bank_sms() → LangChain Prompt → Mistral AI (mistral-large-latest)
                                                      ↓
                            Structured Output: { amount, merchant, currency }
                                                      ↓
                              + userId injected → Kafka topic: expense_service
                                                      ↓
                                          ExpenseService saves to MySQL
```

---

### 5. Kong API Gateway (Port: 8000)

**Technology:** Kong Gateway 3.0 · DB-less (declarative config via `kong.yml`)

The single entry point for all client requests. Configured entirely through `kong/config/kong.yml` with no database required.

#### `kong.yml` Configuration

| Route | Upstream | Plugins |
|---|---|---|
| `/auth` | `http://authservice:9898` | None (public) |
| `/user` | `http://userservice:9810` | `rate-limiting` (100 req/min), `cors` (allows `localhost:5173`) |
| `/expense` | `http://expenseservice:9820` | `rate-limiting` (100 req/min) |
| `/v1/ds` | `http://dsservice:8010` | `rate-limiting` (50 req/min) |

`strip_path: false` is set on all routes — Kong forwards the full path to the upstream service.

---

### 6. React Frontend (Port: 5173)

**Technology:** React 19 · Vite 7 · Tailwind CSS 4 · Framer Motion · React Router DOM 7 · Axios · React Icons

A polished single-page application with smooth page transitions and a glassmorphism design system.

#### Key Files

| File | Responsibility |
|---|---|
| `App.jsx` | Root component. Sets up `BrowserRouter`, defines all routes wrapped in `AnimatePresence`/`motion.div` for animated transitions. Uses `ProtectedRoute` to guard `/dashboard` |
| `main.jsx` | ReactDOM entry point — wraps `<App>` in `<AuthProvider>` for global auth context |
| `api/axiosInstance.js` | Base Axios instance with `baseURL: http://localhost:8000` (Kong) and `withCredentials: true` (for cookie-based refresh tokens) |
| `api/axiosPrivate.js` | Wraps `axiosInstance` with request interceptors: injects `Authorization: Bearer <token>` on every request. Response interceptors handle `401` errors by calling `refreshAccessToken()` and retrying the original request automatically |
| `context/AuthProvider.jsx` | Global auth state via React Context. On mount, attempts a silent token refresh to restore session. Exposes `login(token)`, `logout()`, `accessToken`, and `loading` state |
| `utils/ProtectedRoute.jsx` | Route guard — redirects to `/login` if `accessToken` is null (waits for `loading` to resolve first) |
| `pages/login_page/Login.jsx` | Login form with focus animations, show/hide password toggle, and redirect to `/dashboard` on success |
| `pages/signup_page/Signup.jsx` | Signup form with phone number input (using `react-phone-input-2`) |
| `pages/dashboard_page/Dashboard.jsx` | Protected user dashboard (expense management UI) |
| `components/background/Background.jsx` | Animated background rendered behind all pages |
| `vite.config.js` | Configures Vite with `@vitejs/plugin-react` and `@tailwindcss/vite` |

---

## 📖 File & Folder Reference

### Backend — Common Patterns

Every Java microservice follows the same layered package structure:

| Layer | Package | What lives here |
|---|---|---|
| **Controller** | `controller/` | `@RestController` classes mapping HTTP endpoints |
| **Service** | `service/` | Business logic, `@Service` annotated classes |
| **Repository** | `repository/` | `@Repository` interfaces extending `JpaRepository` |
| **Entity** | `entities/` | `@Entity` JPA classes mapped to database tables |
| **DTO / Model** | `model/` | Data Transfer Objects (request/response shapes) |
| **Auth Filter** | `auth/` | `OncePerRequestFilter` for JWT/token validation |
| **Config** | `config/` | Spring Beans: `ObjectMapper`, `RestTemplate`, `PasswordEncoder` |
| **Kafka** | `eventProducer/` or `eventConsumer/` | Kafka producers and `@KafkaListener` consumers |
| **Serializer** | `serializer/` or `deserializer/` | Custom Kafka JSON serializers/deserializers |
| **Request/Response** | `request/`, `response/` | Typed POJO wrappers for API payloads |
| **Utility** | `util/` | Helper classes (validation, formatting, etc.) |

### Infrastructure Files

| File | Purpose |
|---|---|
| `backend/docker-compose.yml` | Orchestrates 8 containers: Zookeeper, Kafka, MySQL, AuthService, UserService, ExpenseService, DSService, Kong. Handles startup ordering with `depends_on` and health checks |
| `backend/init.sql` | Bootstraps 3 MySQL databases: `auth_service`, `user_service`, `expense_service` — executed on first container start |
| `backend/kong/config/kong.yml` | Declarative Kong configuration (DB-less mode) — all route definitions, upstreams, and plugins |
| `backend/kong/Dockerfile` | Builds Kong image with the declarative config mounted |
| `*/Dockerfile` | Each service has its own Dockerfile using either `eclipse-temurin:21-jdk` (Java) or `python:3.11-slim` (DS Service) |
| `*/build.gradle` | Gradle build scripts defining Java 17 toolchain, Spring Boot version (3.5.10), and all runtime dependencies |
| `*/application.properties` | Spring Boot configuration: datasource URLs (with environment variable fallbacks), Kafka bootstrap servers, JWT secret/expiry, server port |
| `dsService/requirements.txt` | Python dependencies for the DS Service |
| `Frontend/package.json` | npm dependency manifest for the React app |

---

## 📡 API Endpoints

All requests from the client go through **Kong Gateway (`localhost:8000`)**:

### Auth Service (`/auth/v1`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/v1/signup` | Public | Register new user. Returns `{ accessToken }`. Sets `refreshToken` HttpOnly cookie |
| `POST` | `/auth/v1/login` | Public | Authenticate user. Returns `{ accessToken }`. Sets `refreshToken` HttpOnly cookie |
| `POST` | `/auth/v1/refreshToken` | Cookie | Reads `refreshToken` cookie, returns new `{ accessToken }` |
| `GET` | `/auth/v1/validate` | Bearer | Validates token. Returns `{ userId, username }`. Used by downstream services |
| `GET` | `/auth/v1/ping` | Bearer | Health check — returns `userId` if authenticated |
| `GET` | `/auth/v1/health` | Public | Basic health check — returns `true` |

### User Service (`/user/v1`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/user/v1/createUpdateUser` | Bearer | Create or update user profile (`{ firstName, lastName, phoneNumber, email }`) |
| `GET` | `/user/v1/getUser` | Bearer | Get authenticated user's profile |

### Expense Service (`/expense/v1`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/expense/v1/getExpense` | Bearer | Get all expenses for the authenticated user |
| `POST` | `/expense/v1/addExpense` | Bearer | Manually add an expense `{ amount, merchant, currency }` |
| `PUT` | `/expense/v1/updateExpense` | Bearer | Update an existing expense by `externalId` |

### DS Service (`/v1/ds`)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/v1/ds/message` | Bearer | Submit raw bank SMS. AI parses it and auto-logs as expense via Kafka |
| `GET` | `/v1/ds/ping` | Public | Health check — returns `"Pong"` |

---

## 🔄 Kafka Event Flow

### Topic: `user_service`
**Publisher:** Auth Service → **Consumer:** User Service

Triggered on every new user signup. Ensures User Service stays in sync with Auth Service without direct HTTP coupling.

```
AuthService (UserInfoProducer)
  → Topic: user_service
    → UserService (AuthServiceConsumer)
      → Creates UserInfo record in user_service DB
```

**Event Payload (`UserInfoDto`):**
```json
{
  "userId": "uuid",
  "username": "john_doe",
  "email": "john@example.com",
  "phoneNumber": "03001234567",
  "firstName": null,
  "lastName": null
}
```

---

### Topic: `expense_service`
**Publisher:** DS Service → **Consumer:** Expense Service

Triggered when an SMS is parsed by the AI engine. Automatically creates an expense record without any user action.

```
DSService (KafkaProducer)
  → Topic: expense_service
    → ExpenseService (ExpenseConsumer)
      → Creates Expense record in expense_service DB
```

**Event Payload (`ExpenseDto`):**
```json
{
  "user_id": "uuid",
  "amount": "5000",
  "merchant": "IMTIAZ SUPER MARKET",
  "currency": "PKR"
}
```

---

## 🗄 Database Schema

Three isolated MySQL databases, each owned by its respective service.

### `auth_service`
**Table: `user_info`**
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT PK | Auto-increment |
| `user_id` | VARCHAR | UUID, unique identifier |
| `username` | VARCHAR | Unique |
| `password` | VARCHAR | BCrypt-hashed |
| `role` | ENUM | `USER`, `ADMIN` |

**Table: `refresh_token`**
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT PK | |
| `token` | VARCHAR | UUID token value |
| `expiry_date` | DATETIME | 7-day expiry |
| `user_info_id` | FK | References `user_info` |

### `user_service`
**Table: `user_info`**
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT PK | |
| `user_id` | VARCHAR | UUID from Auth Service |
| `username` | VARCHAR | |
| `first_name` | VARCHAR | |
| `last_name` | VARCHAR | |
| `phone_number` | VARCHAR | |
| `email` | VARCHAR | |

### `expense_service`
**Table: `expense`**
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT PK | |
| `external_id` | VARCHAR | UUID, exposed to clients |
| `user_id` | VARCHAR | UUID from Auth Service |
| `amount` | VARCHAR | Transaction amount |
| `merchant` | VARCHAR | Merchant name |
| `currency` | VARCHAR | Defaults to `PKR` |
| `created_at` | DATETIME | Auto-set on insert |
| `updated_at` | DATETIME | Auto-set on update |

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Node.js 18+
- Java 17+ (for local development without Docker)
- Python 3.11+ (for DS Service local dev)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/expense-tracker-app.git
cd expense-tracker-app
```

### 2. Build Java Services (Required before Docker Compose)

```bash
# Auth Service
cd backend/expense-tracker-app/authservice
./gradlew bootJar
docker build -t auth-service .
cd ../../..

# User Service
cd backend/userservice/userservice
./gradlew bootJar
docker build -t user-service .
cd ../../..

# Expense Service
cd backend/expenseservice/expenseservice
./gradlew bootJar
docker build -t expense-service .
cd ../../..
```

### 3. Build & Package the DS Service

```bash
cd backend/dsService
python setup.py sdist     # Creates dist/ds_service-1.0.tar.gz
docker build -t ds_service .
cd ../..
```

### 4. Configure Environment

Create `backend/dsService/.env`:
```env
MISTRAL_API_KEY=your_mistral_api_key_here
```

> **Note:** Get your free API key from [console.mistral.ai](https://console.mistral.ai)

### 5. Start the Full Stack

```bash
cd backend
docker-compose up -d
```

This starts all 8 containers in the correct order.

### 6. Start the Frontend

```bash
cd Frontend/expense-tracker-app
npm install
npm run dev
```

App is now running at **http://localhost:5173**

---

### Service Health Check URLs

| Service | URL |
|---|---|
| Kong Gateway | http://localhost:8000 |
| Kong Admin API | http://localhost:8001 |
| Auth Service | http://localhost:9898/auth/v1/health |
| User Service | http://localhost:9810 |
| Expense Service | http://localhost:9820 |
| DS Service | http://localhost:8010/v1/ds/ping |

---

## ⚙ Environment Variables

### Docker Compose (all Java services)

| Variable | Default | Used By |
|---|---|---|
| `KAFKA_HOST` | `localhost` | All services |
| `KAFKA_PORT` | `9092` | All services |
| `MYSQL_HOST` | `localhost` | Auth, User, Expense |
| `MYSQL_PORT` | `3306` | Auth, User, Expense |
| `MYSQL_DB` | Service-specific | Auth, User, Expense |
| `MISTRAL_API_KEY` | — | DS Service (**required**) |

### Auth Service (`application.properties`)

| Property | Default | Description |
|---|---|---|
| `jwt.secret` | (hardcoded in props) | HMAC-SHA256 signing key |
| `jwt.expiration` | `900000` (15 min) | Access token TTL in ms |
| `server.port` | `9898` | Service port |

---

## 👨‍💻 Author

Built by a final-year Computer Science student at **UBIT, University of Karachi** as a personal portfolio project demonstrating real-world microservices design, event-driven architecture, and AI integration.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
