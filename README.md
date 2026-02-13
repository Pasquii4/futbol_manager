# ⚽ Politècnics Football Manager

Aplicació Java completa de gestió de futbol desenvolupada amb paradigma OOP (Object Oriented Programming). Simula un petit manager de futbol amb equips, jugadors, entrenadors, mercat de fitxatges i lligues.

## 📋 Característiques

- **Dos rols d'usuari**: Admin i Gestor d'Equip
- **Gestió completa d'equips**: Alta, baixa, modificació
- **Mercat de fitxatges**: 30 jugadors i 10 entrenadors professionals
- **Sistema de lligues**: Simulació de partits amb algoritme ponderat
- **Persistència de dades**: Fitxers de text
- **Entrenaments**: Millora de qualitat i motivació
- **Transferències**: Moviment de jugadors entre equips
- **Classificació dinàmica**: Ordenada per punts i diferència de gols

## 🏗️ Estructura del Projecte

```
futbol_manager/
├── src/
│   ├── model/
│   │   ├── Persona.java          # Classe base abstracta
│   │   ├── Jugador.java          # Classe jugador (hereta de Persona)
│   │   ├── Entrenador.java       # Classe entrenador (hereta de Persona)
│   │   ├── Equip.java            # Gestió d'equips
│   │   ├── Lliga.java            # Sistema de lligues
│   │   ├── Partit.java           # Simulació de partits
│   │   └── DadesClassificacio.java  # Dades de classificació
│   ├── comparators/
│   │   ├── ComparadorJugadorQualitat.java  # Ordenació per qualitat
│   │   └── ComparadorJugadorPosicio.java   # Ordenació per posició
│   ├── utils/
│   │   └── GestorFitxers.java    # Gestió de fitxers I/O
│   └── Main.java                 # Classe principal amb menús
├── data/
│   ├── mercat_fitxatges.txt      # Jugadors i entrenadors disponibles
│   └── equips.txt                # Equips guardats
└── bin/                          # Classes compilades
```

## 🚀 Com Executar l'Aplicació

### Prerequisits

- Java JDK 8 o superior instal·lat
- Configurar la variable d'entorn JAVA_HOME (opcional però recomanat)

### Compilar

Obre un terminal a la carpeta del projecte i executa:

```powershell
javac -d bin -sourcepath src -encoding UTF-8 src/Main.java
```

### Executar

```powershell
java -cp bin Main
```

## 📖 Guia d'Ús

### Menú de Login

En iniciar l'aplicació, tria el teu rol:
- **Admin**: Accés complet a totes les funcionalitats
- **Gestor d'Equip**: Gestió d'equips específics

### Menú Admin (8 opcions)

1. **Veure classificació lliga actual** 🏆
   - Mostra la taula de classificació ordenada

2. **Donar d'alta equip**
   - Crea nous equips amb dades completes

3. **Donar d'alta jugador/a o entrenador/a**
   - Afegeix persones al mercat de fitxatges

4. **Consultar dades equip**
   - Vista completa d'un equip

5. **Consultar dades jugador/a equip**
   - Informació detallada d'un jugador

6. **Disputar nova lliga**
   - Crea i simula una lliga completa

7. **Realitzar sessió entrenament (mercat)**
   - Entrena tots els jugadors/entrenadors del mercat

8. **Desar dades equips**
   - Guarda tots els equips en fitxer

### Menú Gestor d'Equip (6 opcions)

1. **Veure classificació lliga actual** 🏆
2. **Gestionar el meu equip** ⚽ (submenu)
3. **Consultar dades equip**
4. **Consultar dades jugador/a equip**
5. **Transferir jugador/a**
6. **Desar dades equips**

### Submenu Gestió d'Equip (4 opcions)

1. **Donar de baixa l'equip**
2. **Modificar president/a**
3. **Destituir entrenador/a**
4. **Fitxar jugador/a o entrenador/a**

## 🎓 Conceptes OOP Implementats

### Herència
- `Persona` (classe base abstracta)
  - `Jugador` (classe filla)
  - `Entrenador` (classe filla)

### Encapsulació
- Tots els camps privats amb getters/setters
- Camps finals (immutables): nom, cognom, dataNaixement

### Polimorfisme
- Sobreescriptura del mètode `entrenament()`
- Sobreescriptura de `toString()`, `equals()`, `hashCode()`

### Abstracció
- Classe `Persona` abstracta
- Interfície `Comparator` per als comparadors

## 📊 Característiques Especials

### Sistema de Punts
- ✅ Victòria: 3 punts
- 🤝 Empat: 1 punt
- ❌ Derrota: 0 punts

### Algoritme de Simulació de Partits

```
golsEquip = random(0-5) × (qualitat/100) × (motivació/10) × factor_aleatori(0.7-1.3)
```

### Entrenament de Jugadors
- 70% probabilitat: +0.1 qualitat
- 20% probabilitat: +0.2 qualitat
- 10% probabilitat: +0.3 qualitat
- Sempre: +0.2 motivació

### Canvi de Posició (Jugadors)
- 5% probabilitat per entrenament
- Si canvia: +1.0 qualitat

### Increment de Sou (Entrenadors)
- +0.5% per entrenament

## 📝 Format dels Fitxers de Dades

### mercat_fitxatges.txt

```
JUGADOR|nom|cognom|dataNaixement|motivacio|sou|dorsal|posicio|qualitat
ENTRENADOR|nom|cognom|dataNaixement|motivacio|sou|tornejos|seleccionador
```

### equips.txt

```
EQUIP|nom|any|ciutat|estadi|president
ENTRENADOR|...
JUGADOR|...
---
```

## 📚 Generar Documentació JavaDoc

```powershell
javadoc -d docs -sourcepath src -subpackages model:comparators:utils -encoding UTF-8
```

Això crearà la documentació HTML a la carpeta `docs/`.

## 🎯 Funcionalitats Destacades

- ✅ Comptador estàtic de jugadors totals
- ✅ Validació completa d'entrades d'usuari
- ✅ Gestió d'errors robusta
- ✅ Interfície amb emojis i taules formatades
- ✅ Ordenació múltiple de jugadors
- ✅ Persistència de dades entre sessions

## 👨‍💻 Tecnologies Utilitzades

- **Java 8+**
- **Collections Framework** (ArrayList, HashMap)
- **Java Time API** (LocalDate)
- **File I/O** (BufferedReader, PrintWriter)
- **Comparators** (interfície Comparator)

## 📄 Llicència

Projecte educatiu - Politècnics (Primer any de Desenvolupament d'Aplicacions)

## 🤝 Contribució

Aquest és un projecte educatiu. Per a millores o suggeriments, contacta amb l'equip docent.

---

**Versió**: 1.0  
**Data**: Febrer 2026  
**Autor**: Politècnics Football Manager Team
