# Changelog

Todos los cambios notables del proyecto se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es/)
y este proyecto sigue [Semantic Versioning](https://semver.org/).

## [1.0.0] - 2026-02-09

### Added

#### Autenticación
- Registro de usuarios con validación de datos
- Inicio de sesión con generación de token JWT
- Seguridad con OAuth2 Resource Server

#### Catálogo Musical
- Búsqueda de canciones, álbumes y artistas
- Detalle de canciones con metadata de YouTube Music
- Detalle de álbumes con listado de canciones
- Detalle de artistas con tabs de videos y álbumes

#### Biblioteca del Usuario
- Sistema de likes para canciones, álbumes y artistas
- CRUD completo de playlists
- Gestión de canciones dentro de playlists
- Historial de reproducciones con detección automática de álbum

#### Usuario
- Obtener perfil del usuario autenticado
- Información del usuario actual

#### Infraestructura
- Integración con NewPipeExtractor para extracción de metadata
- Base de datos PostgreSQL con migraciones Flyway
- Mapeo de DTOs con MapStruct
- Procesamiento asíncrono con CompletableFuture
