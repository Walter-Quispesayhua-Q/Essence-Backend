# Contribuir a Essence Backend

¡Gracias por tu interés en contribuir! 🎉

## 🐛 Reportar Bugs

Abre un [Issue](https://github.com/Walter-Quispesayhua-Q/Essence-Backend/issues)
describiendo el problema con el mayor detalle posible: pasos para reproducir,
comportamiento esperado vs. observado, y entorno (versión Java, OS, etc.).

## �️ Roadmap

### 🔴 Alta prioridad

- [ ] **Descargas offline** — Soporte para que el cliente móvil descargue canciones
  y reproduzca sin conexión. Requiere coordinación con el equipo del cliente
  (gestión de tokens de descarga, expiración, almacenamiento local en el dispositivo).

### 🟡 Media prioridad

- [ ] **Tests unitarios** — Cobertura mínima del 70% en servicios y controladores críticos
- [ ] **Tests de integración** — Probar flujos end-to-end con `@SpringBootTest` y Testcontainers
- [ ] **Corregir textos y labels de tabs** — Revisión y traducción consistente de los textos visibles

### 🟢 Baja prioridad

- [ ] Documentación OpenAPI/Swagger en línea
- [ ] Métricas con Micrometer + dashboard en Application Insights
- [ ] Caching con Redis para endpoints de búsqueda

### ✅ Completado

- [x] Implementar paginación en endpoints de búsqueda
- [x] Mejorar manejo de errores del NewPipeExtractor
- [x] Implementar solución para obtener tabs de artistas y albums para el feature home
- [x] Optimización del Dockerfile con layered JAR y JVM tuning para contenedores
- [x] Health probes separados (`liveness` / `readiness`) para Azure Container Apps
- [x] Rate limiting en endpoints públicos críticos
- [x] CI/CD con GitHub Actions: build automático y push a GHCR
- [x] Configuración de producción con separación de privilegios Flyway/runtime
- [x] Despliegue en Azure Container Apps con PostgreSQL Flexible Server

## 🚀 ¿Cómo contribuir?

1. Fork del repositorio
2. Crea una rama descriptiva (`git checkout -b feat/descargas-offline`)
3. Haz tus cambios siguiendo el estilo existente
4. Commit con mensaje [Conventional Commits](https://www.conventionalcommits.org/)
   (`git commit -m "feat(downloads): agregar endpoint de descarga offline"`)
5. Push (`git push origin feat/descargas-offline`)
6. Abre un Pull Request describiendo el cambio

## 📝 Convenciones de commits

| Prefijo | Cuándo usar |
|---------|-------------|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de bug |
| `refactor` | Reorganizar código sin cambiar comportamiento |
| `docs` | Solo documentación |
| `chore` | Tareas de mantenimiento, configs, releases |
| `test` | Agregar o modificar tests |
| `perf` | Mejoras de rendimiento |

Ejemplo: `feat(library): agregar endpoint para exportar playlist a JSON`