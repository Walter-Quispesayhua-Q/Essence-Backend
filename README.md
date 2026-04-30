# 🎵 Essence Music Backend

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Azure](https://img.shields.io/badge/Azure-Container%20Apps-0078D4?logo=microsoftazure&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## 📖 Descripción

Essence Backend es una API REST para una plataforma de streaming de música.
Permite buscar canciones, álbumes y artistas, crear playlists personalizadas,
dar likes y llevar un historial de reproducción.

Utiliza [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor)
para obtener metadata musical de YouTube Music.

## ✨ Features

- 🔐 **Autenticación** — Registro e inicio de sesión con JWT
- 🔍 **Búsqueda** — Canciones, álbumes y artistas
- 🎵 **Catálogo Musical** — Detalles de canciones, álbumes y artistas con sus tabs
- ❤️ **Likes** — Sistema de favoritos para playlist creados, canciones, álbumes y artistas
- 📝 **Playlists** — Crear, editar, eliminar y gestionar canciones
- 📊 **Historial** — Registro automático de reproducciones
- 👤 **Perfil** — Información del usuario autenticado

> 📖 Ver documentación completa en [API Reference](docs/api-reference.md)

## 🛠️ Tech Stack

| Categoría | Tecnología | Versión | Uso |
|-----------|------------|---------|-----|
| Lenguaje | Java | 21 | Lenguaje principal |
| Framework | Spring Boot | 4.0.1 | Framework backend |
| Seguridad | Spring Security | 7.0.2 | Autenticación JWT (OAuth2 Resource Server) |
| Base de datos | PostgreSQL | 18 | Base de datos relacional |
| Migraciones | Flyway | - | Versionado del esquema |
| Mapeo | MapStruct | 1.6.3 | Mapeo de DTOs |
| Boilerplate | Lombok | - | Reducción de boilerplate |
| Extractor | NewPipeExtractor | 0.25.2 | Extracción de metadata musical |
| Contenedor | Docker | - | Imagen multi-stage con layered JAR |
| CI/CD | GitHub Actions | - | Build automático a GHCR |
| Cloud | Azure Container Apps | - | Despliegue serverless en producción |

## 📋 Requisitos Previos

- **Java 21** o superior
- **Maven 3.9+**
- **Docker Desktop** (para entorno de desarrollo local autocontenido)
- **PostgreSQL 18** (alternativa: usar el del compose)

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/Walter-Quispesayhua-Q/Essence-Backend.git
cd Essence-Backend
```

### 2. Configurar variables de entorno

Copiar la plantilla y rellenar con tus valores reales:

```bash
cp .env.example .env
```

Editar `.env` y configurar al menos:

```env
JWT_SECRET_B64=<base64 de 256+ bits>
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

> ⚠️ El archivo `.env` está en `.gitignore` — nunca subas secretos a git.

### 3. Levantar Postgres local con Docker (recomendado)

```bash
docker compose -f compose.dev.yml up -d postgres
```

Esto levanta PostgreSQL 18 en `localhost:5432` con la base `essence_dev` ya creada.

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Flyway aplicará automáticamente las 14 migraciones al primer arranque.
La API quedará disponible en `http://localhost:8099`.

### 5. Probar el health endpoint

```bash
curl http://localhost:8099/actuator/health
```

Respuesta esperada: `{"status":"UP"}`

## 🐳 Desarrollo con Docker

El proyecto incluye un `compose.dev.yml` autocontenido que levanta:

- **postgres** — PostgreSQL 18-alpine con volumen persistente y healthcheck
- **backend** — Build local del backend con perfil `dev`

Para levantar todo junto:

```bash
docker compose -f compose.dev.yml up --build
```

Para levantar solo Postgres (útil si corres la app desde IntelliJ):

```bash
docker compose -f compose.dev.yml up -d postgres
```

## ☁️ Despliegue en Azure (Producción)

La app está diseñada para correr en **Azure Container Apps** con:

- **Imagen Docker** publicada en GitHub Container Registry (GHCR) vía GitHub Actions
- **Azure Database for PostgreSQL** Flexible Server (free tier B1ms)
- **Separación de privilegios**: Flyway corre con usuario admin, la app con usuario limitado
- **Variables de entorno y secretos** gestionados desde Container Apps Secrets
- **Scale to zero** para minimizar costos en periodos sin tráfico

Variables requeridas en producción:

| Variable | Propósito |
|----------|-----------|
| `SPRING_PROFILES_ACTIVE` | Activa el perfil `prod` |
| `DB_URL` | JDBC URL con `?sslmode=require` |
| `DB_USERNAME` / `DB_PASSWORD` | Usuario de runtime (privilegio mínimo) |
| `DB_ADMIN_USERNAME` / `DB_ADMIN_PASSWORD` | Usuario admin (solo Flyway) |
| `JWT_SECRET_B64` | Secreto para firmar tokens JWT |

## 🏗️ Arquitectura

El proyecto sigue una arquitectura modular organizada por dominio:

| Módulo | Descripción |
|--------|-------------|
| `autentication` | Login, registro y gestión de JWT |
| `music` | Canciones, álbumes y artistas |
| `library` | Playlists, likes e historial |
| `search` | Motor de búsqueda |
| `extractor` | Integración con NewPipeExtractor |
| `security` | Spring Security, rate limiting y filtros |

## ⚡ Optimizaciones aplicadas

- **Layered JAR** + Docker multi-stage: capas inmutables cacheables
- **JVM tuning para contenedores**: `MaxRAMPercentage=75.0`, `UseG1GC`, `TieredStopAtLevel=1`, `UseStringDeduplication`
- **Hibernate startup**: dialect explícito y sin acceso a metadata JDBC en boot
- **Health probes**: `liveness` y `readiness` separados para Container Apps
- **Rate limiting**: protección de endpoints públicos críticos (login, registro, búsqueda)
- **GitHub Actions cache**: build incremental con `setup-buildx-action` + capa Docker

## 🤝 Contribuir

¿Quieres contribuir? Lee la [guía de contribución](CONTRIBUTING.md).

## 👨‍💻 Autor

- **Walter Quispesayhua** — [GitHub](https://github.com/Walter-Quispesayhua-Q)

## 📄 Licencia

Este proyecto está bajo la licencia Apache 2.0 — ver [LICENSE](LICENSE) para más detalles.

---

⭐ ¡Si te gustó el proyecto, dale una estrella!