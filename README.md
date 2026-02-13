# ⚽ Football Manager - Advanced Edition

> Sistema de gestión de ligas de fútbol con motor de simulación avanzado, tácticas dinámicas y estadísticas exhaustivas.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Status](https://img.shields.io/badge/Status-Ready-success.svg)]()
[![Version](https://img.shields.io/badge/Version-2.0-blue.svg)]()

---

## 🚀 Inicio Rápido

```bash
# 1. Compilar
cd Java
javac -d bin src/**/*.java

# 2. Ejecutar
java -cp bin Main
```

---

## 📋 Características Principales

✅ **Sistema Táctico Completo**
- 6 formaciones (4-4-2, 4-3-3, 3-5-2, 4-5-1, 5-3-2, 3-4-3)
- 5 estilos de juego (Ultra Ofensivo → Ultra Defensivo)
- Alineación automática con filtrado de lesionados y fatigados
- Intensidad de presión configurable (1-10)

✅ **Motor de Simulación Avanzado**
- Distribución de **Poisson** para generación realista de goles
- Asignación inteligente de goleadores (delanteros ×2.0 probabilidad)
- 70% de goles con asistencia
- Simulación de tarjetas (5% amarillas, 1% rojas)
- Generación automática de eventos del partido

✅ **Gestión de Fatiga y Lesiones**
- Fatiga 0-100 que afecta calidad efectiva
- 4 tipos de lesiones (muscular, ósea, articular, leve)
- Recuperación automática entre jornadas

✅ **Estadísticas Exhaustivas**
- Goles, asistencias, tarjetas por jugador
- Rating dinámico calculado automáticamente
- Estadísticas específicas de porteros
- Mapa de goles por jornada

✅ **Rankings Automáticos**
- Top 10 goleadores, asistentes, mejor rating
- Mejor portero (menos goles/partido)
- Jugadores más/menos disciplinados

✅ **Historial de Partidos**
- Registro completo con eventos detallados
- Consultas por equipo, jornada o enfrentamiento
- Eventos con emojis (⚽🟨🟥🎯)

✅ **Persistencia**
- Guardado/carga con serialización Java
- Sistema de backups automático

---

## 📦 Estructura del Proyecto

```
Java/
├── src/
│   ├── model/                      # 20 clases del modelo
│   │   ├── Jugador.java           # Con fatiga, lesiones, stats
│   │   ├── Equip.java             # Con presupuesto y táctica
│   │   ├── Partit.java            # Motor Poisson
│   │   ├── Lliga.java             # Gestión completa
│   │   ├── Formacion.java         # 6 formaciones
│   │   ├── EstiloJoc.java         # 5 estilos
│   │   ├── TacticaEquip.java      # Motor táctico
│   │   ├── EstadisticasJugador.java
│   │   ├── RankingsLliga.java
│   │   └── ... (11 clases más)
│   │
│   ├── persistence/               # Persistencia
│   │   └── SimpleDatabaseManager.java
│   │
│   └── Main.java
│
└── test/                          # Tests JUnit
```

---

## 🎯 Uso Básico

```java
// 1. Crear liga
Lliga laLiga = new Lliga("LaLiga Santander", 4);

// 2. Crear y configurar equipos
Equip barcelona = new Equip("FC Barcelona", 1899, "Barcelona");
barcelona.getTactica().setFormacion(Formacion.F_4_3_3);
barcelona.getTactica().setEstiloJoc(EstiloJoc.OFENSIVO);
barcelona.getTactica().setIntensidadPresion(8);

// 3. Añadir jugadores
Jugador messi = new Jugador("Lionel", "Messi", 
    LocalDate.of(1987, 6, 24), 9.5, 500000, 10, "DAV", 95.0);
barcelona.afegirJugador(messi);

// 4. Añadir a liga y generar calendario
laLiga.afegirEquip(barcelona);
laLiga.generarCalendari();

// 5. Simular temporada
while (laLiga.getJornadaActual() < laLiga.getNumJornades()) {
    laLiga.simularJornada();
    laLiga.mostrarClassificacio();
}

// 6. Ver rankings
System.out.println(laLiga.getRankings().generarTablasRankings());

// 7. Guardar
SimpleDatabaseManager.saveObject(laLiga, "partida.ser");
```

---

## ⚙️ Sistema Táctico

### Formaciones

| Formación | DEF | MED | DAV | Bonus ATQ | Bonus DEF |
|-----------|-----|-----|-----|-----------|-----------|
| 4-4-2 | 4 | 4 | 2 | 1.10 | 1.20 |
| 4-3-3 | 4 | 3 | 3 | 1.15 | 1.20 |
| 3-5-2 | 3 | 5 | 2 | 1.10 | 1.15 |
| 4-5-1 | 4 | 5 | 1 | 1.05 | 1.20 |
| 5-3-2 | 5 | 3 | 2 | 1.10 | 1.25 |
| 3-4-3 | 3 | 4 | 3 | 1.15 | 1.15 |

### Estilos de Juego

| Estilo | ATQ | DEF |
|--------|-----|-----|
| ULTRA_OFENSIVO | 1.30 | 0.70 |
| OFENSIVO | 1.15 | 0.85 |
| EQUILIBRADO | 1.00 | 1.00 |
| DEFENSIVO | 0.85 | 1.15 |
| ULTRA_DEFENSIVO | 0.70 | 1.30 |

---

## 🎲 Motor de Simulación

**Proceso de 10 Pasos:**

1. Preparación (alineaciones automáticas)
2. Cálculo de fuerzas (tácticas + entrenador)
3. **Poisson** para goles
4. Asignar goleadores (DAV ×2.0)
5. Asignar asistencias (70%)
6. Simular tarjetas (5%/1%)
7. Calcular ratings (0-10)
8. Registrar estadísticas
9. Aplicar fatiga
10. Ordenar eventos

### Distribución de Poisson

```java
double ratio = fuerzaAtacante / fuerzaDefensor;
double lambda = max(0, ratio × 1.5 - 0.5);

double L = exp(-lambda);
double p = 1.0;
int k = 0;
while (p > L) {
    k++;
    p *= random();
}
goles = max(0, k - 1);
```

**Ventaja**: Equipos fuertes marcan más consistentemente.

### Probabilidades de Goleador

```java
DAV: probabilidad × 2.0
MIG: probabilidad × 1.2
DEF: probabilidad × 0.3
POR: probabilidad × 0.05
```

---

## 📊 Estadísticas

### Cálculo de Rating

```java
rating = 6.0 
       + (goles × 1.5) / partidos
       + (asistencias × 1.0) / partidos
       - (amarillas × 0.2) / partidos
       - (rojas × 0.5) / partidos
// Límite: [0.0, 10.0]
```

### Rankings Disponibles

- Top 10 goleadores
- Top 10 asistentes
- Top 10 mejor rating (mínimo 5 partidos)
- Mejor portero (mínimo 3 partidos)
- Más/menos disciplinado

---

## 💾 Persistencia

```java
// Guardar
SimpleDatabaseManager.saveObject(liga, "partida.ser");

// Cargar
Lliga liga = (Lliga) SimpleDatabaseManager.loadObject("partida.ser");

// Backup
SimpleDatabaseManager.createBackup("partida.ser");
```

---

## 📈 Métricas del Proyecto

| Métrica | Valor |
|---------|-------|
| Versión | 2.0 |
| Clases | 24 |
| Líneas de código | ~3,500+ |
| Fases completadas | 6/6 (100%) |

---

## 🛣️ Roadmap

### ✅ v2.0 (Actual)
- Sistema táctico completo
- Motor Poisson
- Estadísticas exhaustivas
- Rankings automáticos
- Persistencia

### 🔄 v2.1 (Próximamente)
- Interfaz gráfica (JavaFX)
- Sistema de transferencias
- Mercado de fichajes
- Progresión de jugadores

### 🚀 v3.0 (Futuro)
- Multijugador
- Editor de equipos
- Gráficos estadísticos
- Modo carrera

---

## 👥 Autor

**Politècnics Football Manager Team**  
Versión 2.0 - Febrero 2026

---

## 📝 Notas de Versión v2.0

**Nuevas Funcionalidades:**
- ✨ Sistema táctico (6 formaciones, 5 estilos)
- ✨ Motor Poisson para simulación
- ✨ Estadísticas con rating dinámico
- ✨ Rankings automáticos
- ✨ Sistema de fatiga y lesiones
- ✨ Historial con eventos

**Mejoras:**
- Goleadores inteligentes (×2.0 delanteros)
- 70% de goles con asistencia
- Tarjetas realistas (5%/1%)

---

<div align="center">

**⚽ Football Manager v2.0 ⚽**

*Desarrollado con ❤️ para Politècnics*

</div>
