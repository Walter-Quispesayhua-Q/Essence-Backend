# 🎵 Essence Music Backend
![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)
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

| Tecnología | Versión | Uso |
|------------|--------|-----|
| Java | 21     | Lenguaje principal |
| Spring Boot | 4.0.1  | Framework backend |
| Spring Security | 7.0.2  | Autenticación JWT (OAuth2 Resource Server) |
| PostgreSQL | 17     | Base de datos |
| Flyway | -      | Migraciones de base de datos |
| MapStruct | 1.6.3  | Mapeo de DTOs |
| Lombok | -      | Reducción de boilerplate |
| NewPipeExtractor | 0.25.2 | Extracción de metadata musical |

## 📋 Requisitos Previos
- **Java 21** o superior
- **Maven 3.9+**
- **PostgreSQL 17+**

## 🚀 Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/Walter-Quispesayhua-Q/Essence-Backend.git
cd Essence-Backend

CREATE DATABASE essence_db;

DB_URL=jdbc:postgresql://localhost:5432/essence_db
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
JWT_SECRET_B64=tu_secreto_base64

mvn spring-boot:run
```

## 🏗️ Arquitectura

El proyecto sigue una arquitectura modular organizada por dominio:

| Módulo | Descripción |
|--------|-------------|
| `autentication` | Login, registro y gestión de JWT |
| `music` | Canciones, álbumes y artistas |
| `library` | Playlists, likes e historial |
| `search` | Motor de búsqueda |
| `extractor` | Integración con NewPipeExtractor |


## 🤝 Contribuir
¿Quieres contribuir? Lee la [guía de contribución](CONTRIBUTING.md).


## 👨‍💻 Autor

- **Walter Quispesayhua** — [GitHub](https://github.com/Walter-Quispesayhua-Q)

## 📄 Licencia

Este proyecto está bajo la licencia Apache 2.0 — ver [LICENSE](LICENSE) para más detalles.
---

---
⭐ ¡Si te gustó el proyecto, dale una estrella!