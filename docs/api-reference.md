# 📖 API Reference

**Base URL:** `http://localhost:8099/api/v1`

**Versión:** 1.0.0

---

## Autenticación

Todos los endpoints que requieren autenticación necesitan el header:

```
Authorization: Bearer <token>
```

El token se obtiene al iniciar sesión con `POST /login`.

---

## 🔐 Auth

### POST `/register`

Registrar un nuevo usuario.

**Auth:** No requiere

**Request Body:**

```json
{
  "username": "walter",
  "email": "walter@email.com",
  "password": "miContraseña123"
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| username | string | Sí | Nombre de usuario |
| email | string | Sí | Email válido |
| password | string | Sí | Contraseña |

**Response `201`:**

```json
{
  "message": "Usuario creado exitosamente!",
  "data": {
    "username": "walter",
    "email": "walter@email.com"
  }
}
```

---

### GET `/register?username=`

Verificar si un nombre de usuario está disponible.

**Auth:** No requiere

**Parámetros:**

| Param | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| username | string | Sí | Username a verificar |

**Response `200`:**

```json
{
  "message": "Usuario disponible",
  "data": true
}
```

---

### POST `/login`

Iniciar sesión y obtener token JWT.

**Auth:** No requiere

**Request Body:**

```json
{
  "email": "walter@email.com",
  "password": "miContraseña123"
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| email | string | Sí | Email registrado |
| password | string | Sí | Contraseña |

**Response `200`:**

```json
{
  "message": "Inicio de sesión exitoso!",
  "data": {
    "token": "eyJhbGciOiJSUzI1NiJ9..."
  }
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 401 | Credenciales inválidas |
| 400 | Datos faltantes o email inválido |

---

## 👤 Usuario

### GET `/user/me`

Obtener información del usuario autenticado.

**Auth:** Requiere token

**Response `200`:**

```json
{
  "message": "Usuario encontrado exitosamente!",
  "data": {
    "username": "walter",
    "email": "walter@email.com"
  }
}
```

---

### GET `/user/profile`

Obtener perfil completo del usuario.

**Auth:** Requiere token

**Response `200`:**

```json
{
  "message": "Perfil obtenido exitosamente!",
  "data": {
    "username": "walter",
    "email": "walter@email.com"
  }
}
```

---

## 🏠 Home

### GET `/home`

Obtener contenido trending (canciones, álbumes, artistas).

**Auth:** No requiere

**Response `200`:**

```json
{
  "songs": [
    {
      "id": 1,
      "title": "Canción trending",
      "durationMs": 200000,
      "hlsMasterKey": "abc123",
      "imageKey": "https://i.ytimg.com/vi/...",
      "songType": "ORIGINAL",
      "totalPlays": 0,
      "artistName": "Artista",
      "albumName": "Álbum",
      "releaseDate": null
    }
  ],
  "albums": [
    {
      "id": 1,
      "title": "Álbum trending",
      "imageKey": "https://..."
    }
  ],
  "artists": [
    {
      "id": 1,
      "nameArtist": "Artista trending",
      "imageKey": "https://..."
    }
  ],
  "status": {
    "songsOk": true,
    "albumsOk": true,
    "artistsOk": true,
    "message": null
  }
}
```

---

## 🔍 Búsqueda

### GET `/search?query=&type=`

Buscar canciones, álbumes y artistas.

**Auth:** No requiere

**Parámetros:**

| Param | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| query | string | Sí | Término de búsqueda |
| type | string | No | Filtro: `songs`, `albums`, `artists` |

**Ejemplo:** `GET /search?query=Taylor Swift&type=songs`

**Response `200`:**

```json
{
  "songs": [
    {
      "id": 5,
      "title": "Taylor Swift - Mine",
      "durationMs": 235000,
      "hlsMasterKey": "XPBwXKgDTdE",
      "imageKey": "https://i.ytimg.com/vi/...",
      "songType": "ORIGINAL",
      "totalPlays": 0,
      "artistName": "Taylor Swift",
      "albumName": "Album Name",
      "releaseDate": null
    }
  ],
  "albums": [],
  "artists": []
}
```

---

### GET `/search/categories`

Obtener categorías de búsqueda disponibles.

**Auth:** No requiere

**Response `200`:**

```json
[
  { "id": "songs", "name": "Canciones" },
  { "id": "albums", "name": "Álbumes" },
  { "id": "artists", "name": "Artistas" }
]
```

---

## 🎵 Canciones

### GET `/song/{songId}`

Obtener detalles de una canción. Si la canción no existe en la base de datos, se extrae la metadata de YouTube Music y se guarda automáticamente.

**Auth:** No requiere

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| songId | string | ID de la canción o URL de YouTube |

**Response `200`:**

```json
{
  "id": 5,
  "title": "Taylor Swift - Mine",
  "durationMs": 235000,
  "hlsMasterKey": "XPBwXKgDTdE",
  "imageKey": "https://i.ytimg.com/vi/XPBwXKgDTdE/hqdefault.jpg",
  "songType": "ORIGINAL",
  "totalPlays": 0,
  "artistName": "Taylor Swift",
  "albumName": "Taylor Swift VEVO Playlist",
  "releaseDate": null
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Canción no encontrada |
| 503 | Servicio de extracción no disponible |

---

### POST `/song/{songId}/like`

Dar like a una canción.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| songId | Long | ID numérico de la canción |

**Response `200`:** Sin contenido

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Canción no encontrada |
| 409 | Ya tiene like |

---

### DELETE `/song/{songId}/like`

Quitar like de una canción.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| songId | Long | ID numérico de la canción |

**Response `200`:** Sin contenido

---

## 💿 Álbumes

### GET `/album/{albumId}`

Obtener detalles de un álbum con sus canciones.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| albumId | string | ID del álbum o URL de YouTube |

**Response `200`:**

```json
{
  "id": 1,
  "title": "Taylor Swift VEVO Playlist",
  "imageKey": "https://i.ytimg.com/vi/...",
  "albumUrl": "https://www.youtube.com/playlist?list=...",
  "songs": [
    {
      "id": 5,
      "title": "Taylor Swift - Mine",
      "durationMs": 235000,
      "hlsMasterKey": "XPBwXKgDTdE",
      "imageKey": "https://...",
      "songType": "ORIGINAL",
      "totalPlays": 0,
      "artistName": "Taylor Swift",
      "albumName": "Taylor Swift VEVO Playlist",
      "releaseDate": null
    }
  ]
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Álbum no encontrado |
| 503 | Servicio de extracción no disponible |

---

### POST `/album/{albumId}/like`

Dar like a un álbum.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| albumId | Long | ID numérico del álbum |

**Response `200`:** Sin contenido

---

### DELETE `/album/{albumId}/like`

Quitar like de un álbum.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| albumId | Long | ID numérico del álbum |

**Response `200`:** Sin contenido

---

## 🎤 Artistas

### GET `/artist/{artistId}`

Obtener detalles de un artista con sus videos y álbumes.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| artistId | string | ID del artista o URL de YouTube |

**Response `200`:**

```json
{
  "id": 1,
  "nameArtist": "Taylor Swift",
  "description": "Artista musical...",
  "imageKey": "https://yt3.googleusercontent.com/...",
  "artistUrl": "https://www.youtube.com/channel/...",
  "country": null,
  "albums": [
    {
      "id": 1,
      "title": "Taylor Swift VEVO Playlist",
      "imageKey": "https://..."
    }
  ],
  "songs": [
    {
      "id": 5,
      "title": "Taylor Swift - Mine",
      "durationMs": 235000,
      "artistName": "Taylor Swift"
    }
  ]
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Artista no encontrado |
| 503 | Servicio de extracción no disponible |

---

### POST `/artist/{artistId}/like`

Dar like a un artista.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| artistId | Long | ID numérico del artista |

**Response `200`:** Sin contenido

---

### DELETE `/artist/{artistId}/like`

Quitar like de un artista.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| artistId | Long | ID numérico del artista |

**Response `200`:** Sin contenido

---

## 📝 Playlists

### POST `/playlist`

Crear una nueva playlist.

**Auth:** Requiere token

**Request Body:**

```json
{
  "title": "Mi playlist favorita",
  "description": "Canciones top",
  "isPublic": true
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| title | string | Sí | Título de la playlist (máx 255) |
| description | string | No | Descripción |
| isPublic | boolean | No | Visibilidad pública |

**Response `201`:**

```json
{
  "message": "Se a creado exitosamente la Playlist",
  "data": {
    "id": 1,
    "title": "Mi playlist favorita",
    "description": "Canciones top",
    "isPublic": true
  }
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 400 | Título vacío |
| 409 | Ya existe una playlist con ese nombre |

---

### GET `/playlist/{id}`

Obtener una playlist con sus canciones.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| id | Long | ID de la playlist |

**Response `200`:**

```json
{
  "id": 1,
  "title": "Mi playlist favorita",
  "description": "Canciones top",
  "isPublic": true,
  "songs": [
    {
      "id": 5,
      "title": "Taylor Swift - Mine",
      "durationMs": 235000,
      "artistName": "Taylor Swift"
    }
  ]
}
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Playlist no encontrada |

---

### GET `/playlist/{id}/edit`

Obtener datos de una playlist para edición (sin canciones).

**Auth:** Requiere token

**Response `200`:**

```json
{
  "id": 1,
  "title": "Mi playlist favorita",
  "description": "Canciones top",
  "isPublic": true
}
```

---

### PUT `/playlist/{id}`

Actualizar una playlist.

**Auth:** Requiere token

**Request Body:**

```json
{
  "title": "Nuevo nombre",
  "description": "Nueva descripción",
  "isPublic": false
}
```

**Response `200`:**

```json
{
  "message": "Se a actualizado correctamente la Playlist",
  "data": {
    "id": 1,
    "title": "Nuevo nombre",
    "description": "Nueva descripción",
    "isPublic": false
  }
}
```

---

### DELETE `/playlist/{id}`

Eliminar una playlist.

**Auth:** Requiere token

**Response `204`:** Sin contenido

---

### GET `/playlist/{id}/songs`

Obtener canciones de una playlist.

**Auth:** Requiere token

**Response `200`:**

```json
[
  {
    "id": 5,
    "title": "Taylor Swift - Mine",
    "durationMs": 235000,
    "hlsMasterKey": "XPBwXKgDTdE",
    "imageKey": "https://i.ytimg.com/vi/...",
    "songType": "ORIGINAL",
    "totalPlays": 0,
    "artistName": "Taylor Swift",
    "albumName": "Taylor Swift VEVO Playlist",
    "releaseDate": null
  }
]
```

---

### POST `/playlist/{id}/songs/{songId}`

Agregar una canción a una playlist.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| id | Long | ID de la playlist |
| songId | Long | ID de la canción |

**Response `200`:**

```json
true
```

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Playlist o canción no encontrada |
| 409 | La canción ya está en la playlist |

---

### DELETE `/playlist/{id}/songs/{songId}`

Eliminar una canción de una playlist.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| id | Long | ID de la playlist |
| songId | Long | ID de la canción |

**Response `204`:** Sin contenido

---

## 📊 Historial

### POST `/history/songs/{songId}`

Registrar una reproducción en el historial. Si no se envía `albumId`, se detecta automáticamente el álbum asociado a la canción.

**Auth:** Requiere token

**Parámetros de ruta:**

| Param | Tipo | Descripción |
|-------|------|-------------|
| songId | Long | ID de la canción |

**Request Body:**

```json
{
  "playlistId": null,
  "albumId": null,
  "durationListenedMs": 180000,
  "completed": true,
  "skipped": false,
  "skipPositionMs": null,
  "deviceType": "MOBILE"
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| playlistId | Long | No | ID de la playlist (si se reprodujo desde una) |
| albumId | Long | No | ID del álbum (auto-detectado si es null) |
| durationListenedMs | Integer | No | Milisegundos escuchados |
| completed | Boolean | No | Si escuchó la canción completa |
| skipped | Boolean | No | Si la saltó |
| skipPositionMs | Integer | No | Posición donde saltó (ms) |
| deviceType | String | No | Tipo de dispositivo: `MOBILE`, `WEB`, `DESKTOP` |

**Response `200`:** Sin contenido

**Errores:**

| Código | Descripción |
|--------|-------------|
| 404 | Canción no encontrada |

---

### GET `/history`

Obtener historial de reproducciones del usuario (canciones únicas recientes).

**Auth:** Requiere token

**Response `200`:**

```json
[
  {
    "id": 5,
    "title": "Taylor Swift - Mine",
    "durationMs": 235000,
    "hlsMasterKey": "XPBwXKgDTdE",
    "imageKey": "https://i.ytimg.com/vi/XPBwXKgDTdE/hqdefault.jpg",
    "songType": "ORIGINAL",
    "totalPlays": 0,
    "artistName": "Taylor Swift",
    "albumName": "Taylor Swift VEVO Playlist",
    "releaseDate": null
  }
]
```
