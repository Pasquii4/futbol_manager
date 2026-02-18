# Roadmap: LaLiga Manager (Backend + Frontend)

Este documento define el plan de trabajo para construir el simulador "LaLiga Manager" utilizando Spring Boot para el backend y una aplicación cliente moderna.

## Fase 0: Inspección y Planificación (Completada)
- [x] Analizar estructura de `football-manager-api`.
- [x] Analizar estructura de `football-manager-client`.
- [x] Definir este roadmap detallado.

## Fase 1: Batalla de Datos y Modelo de Dominio (Backend)
**Objetivo:** Establecer la base de datos de LaLiga y el modelo de entidades.
- [ ] Definir Entidades JPA: `League`, `Team`, `Player`, `Match`, `SeasonStats`.
- [ ] Crear `data/laliga_db.json` con datos coherentes de 20 equipos y plantillas.
- [ ] Implementar `DataLoadService` para importar el JSON al arrancar.
- [ ] Tests unitarios para verificar la carga de datos.

## Fase 2: API REST Core
**Objetivo:** Exponer los datos al frontend.
- [ ] Endpoint `GET /api/teams` y `GET /api/teams/{id}`.
- [ ] Endpoint `GET /api/players/{id}`.
- [ ] Endpoint `GET /api/matches` (por jornada/matchday).
- [ ] Endpoint `GET /api/standings` (clasificación calculada).
- [ ] Configurar Swagger/OpenAPI para documentación automática.

## Fase 3: Motor de Simulación
**Objetivo:** Dar vida a la liga con resultados realistas.
- [ ] Implementar algoritmo de "Fuerza de Equipo" (Rating 0-100).
- [ ] Implementar `MatchSimulationService` (probabilístico).
- [ ] Implementar endpoints `POST /api/simulate/matchday`.
- [ ] Implementar endpoints `POST /api/simulate/season` (fast-forward).
- [ ] Actualizar estadísticas y clasificación tras cada simulación.

## Fase 4: Desarrollo del Cliente (Frontend)
**Objetivo:** Interfaz de usuario para gestionar la liga.
- [ ] Vista **Dashboard**: Resumen, próximo partido, botón simular.
- [ ] Vista **Clasificación**: Tabla completa.
- [ ] Vista **Equipos**: Listado y detalle de plantilla.
- [ ] Vista **Jugador**: Stats y perfil.
- [ ] Vista **Calendario/Jornada**: Resultados y partidos pendientes.

## Fase 5: Refinamiento y Tests
**Objetivo:** Asegurar calidad y coherencia.
- [ ] Tests de integración backend (simulaciones completas).
- [ ] Smoke tests frontend.
- [ ] Ajuste fino de probabilidades de simulación (evitar resultados absurdos).
- [ ] Documentación final en README.

## Estado Actual
- Fase 0 completada.
- Iniciando Fase 1.
