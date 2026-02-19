package com.politecnics.football.config;

import com.politecnics.football.entity.*;
import com.politecnics.football.repository.EquipoRepository;
import com.politecnics.football.repository.JugadorRepository;
import com.politecnics.football.repository.LigaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSeeder {

    private final LigaRepository ligaRepository;
    private final EquipoRepository equipoRepository;
    private final JugadorRepository jugadorRepository;
    private final com.politecnics.football.repository.PartidoRepository partidoRepository;

    //@Bean
    public CommandLineRunner initData() {
        return args -> {
            if (ligaRepository.count() == 0) {
                log.info("Sembrando datos iniciales en H2...");
                seedData();
            } else {
                log.info("La base de datos ya contiene datos.");
            }
        };
    }

    private void seedData() {
        // 1. Crear Liga
        Liga liga = Liga.builder()
                .nombre("La Liga")
                .jornadaActual(1)
                .totalJornadas(38)
                .finalizada(false)
                .build();
        
        liga = ligaRepository.save(liga);

        // 2. Crear Equipos
        createEquipo(liga, "FC Barcelona", "Barcelona", "Camp Nou", "Laporta", 80000000);
        createEquipo(liga, "Real Madrid", "Madrid", "Santiago Bernabeu", "Florentino", 85000000);
        createEquipo(liga, "Atletico Madrid", "Madrid", "Metropolitano", "Cerezo", 50000000);
        createEquipo(liga, "Valencia CF", "Valencia", "Mestalla", "Peter Lim", 20000000);
        createEquipo(liga, "Sevilla FC", "Sevilla", "Pizjuan", "Del Nido", 25000000);
        createEquipo(liga, "Real Betis", "Sevilla", "Villamarin", "Haro", 22000000);
        createEquipo(liga, "Athletic Club", "Bilbao", "San Mames", "Uriarte", 30000000);
        createEquipo(liga, "Real Sociedad", "San Sebastian", "Reale Arena", "Aperribay", 28000000);
        createEquipo(liga, "Villarreal CF", "Villarreal", "La Ceramica", "Roig", 35000000);
        createEquipo(liga, "Celta de Vigo", "Vigo", "Balaidos", "Mouriño", 18000000);
        createEquipo(liga, "Rayo Vallecano", "Madrid", "Vallecas", "Presa", 15000000);
        createEquipo(liga, "Girona FC", "Girona", "Montilivi", "Delfi", 22000000);
        createEquipo(liga, "RCD Mallorca", "Palma", "Son Moix", "Kohlberg", 17000000);
        createEquipo(liga, "CA Osasuna", "Pamplona", "El Sadar", "Sabalza", 19000000);
        createEquipo(liga, "Getafe CF", "Getafe", "Coliseum", "Torres", 16000000);
        createEquipo(liga, "UD Almeria", "Almeria", "Power Horse", "Turki", 25000000);
        createEquipo(liga, "Cadiz CF", "Cadiz", "Nuevo Mirandilla", "Vizcaino", 14000000);
        createEquipo(liga, "Granada CF", "Granada", "Los Carmenes", "Sophia", 15000000);
        createEquipo(liga, "UD Las Palmas", "Las Palmas", "Gran Canaria", "Ramirez", 16000000);
        createEquipo(liga, "Deportivo Alaves", "Vitoria", "Mendizorroza", "Fernandez", 13000000);
        
        log.info("Equipos creados. Generando calendario...");
        generateCalendario(liga);
        
        log.info("Generando Agentes Libres (Mercado)...");
        createAgentesLibres();
        
        log.info("Datos sembrados correctamente!");
    }
    
    // --- Fixture Generation (Round Robin) ---
    @lombok.RequiredArgsConstructor
    @lombok.AllArgsConstructor
    private static class Pair {
        Equipo home;
        Equipo away;
    }

    private void generateCalendario(Liga liga) {
        List<Equipo> equipos = equipoRepository.findByLigaId(liga.getId());
        int numEquipos = equipos.size();
        int numJornadas = (numEquipos - 1) * 2;
        int partidosPorJornada = numEquipos / 2;
        
        // Prepare rotation list
        List<Equipo> rotation = new ArrayList<>(equipos);
        rotation.remove(0); // Keep first team fixed
        
        for (int jornada = 1; jornada <= numJornadas; jornada++) {
            boolean isSecondRound = jornada > (numEquipos - 1);
            List<Pair> pairs = new ArrayList<>();
            
            // Fixed team
            int fixedIndex = 0;
            Equipo fixedTeam = equipos.get(0);
            Equipo opponent = rotation.get(rotation.size() - 1); // Last in rotation
            
            if (jornada % 2 != 0) {
                 pairs.add(new Pair(fixedTeam, opponent));
            } else {
                 pairs.add(new Pair(opponent, fixedTeam));
            }
            
            // Rest of pairs
            for (int i = 0; i < partidosPorJornada - 1; i++) {
                Equipo home = rotation.get(i);
                Equipo away = rotation.get(rotation.size() - 2 - i);
                
                if (jornada % 2 != 0) {
                     pairs.add(new Pair(home, away));
                } else {
                     pairs.add(new Pair(away, home));
                }
            }
            
            // Save matches
            for (Pair p : pairs) {
                // Swap for second round
                Equipo local = isSecondRound ? p.away : p.home;
                Equipo visit = isSecondRound ? p.home : p.away;
                
                Partido partido = Partido.builder()
                        .jornada(jornada)
                        .jugado(false)
                        .liga(liga)
                        .equipoLocal(local)
                        .equipoVisitante(visit)
                        .golesLocal(0)
                        .golesVisitante(0)
                        .build();
                        
                partidoRepository.save(partido);
            }
            
            // Rotate
            rotation.add(0, rotation.remove(rotation.size() - 1));
        }
    }

    private void createAgentesLibres() {
        String[] posiciones = {"POR", "DEF", "MIG", "DAV"};
        Random rand = new Random();
        
        for (int i = 0; i < 20; i++) {
            EstadisticasJugador stats = new EstadisticasJugador();
            
            Jugador jugador = Jugador.builder()
                    .nombre("Free Agent " + (i + 1))
                    .apellido("Libre")
                    .dorsal(0)
                    .posicion(Posicion.valueOf(posiciones[rand.nextInt(posiciones.length)]))
                    .calidad(65.0 + rand.nextDouble() * 20.0)
                    .fechaNacimiento(LocalDate.of(1998 + rand.nextInt(8), 1, 1))
                    .sueldo(500000.0 + rand.nextDouble() * 1000000.0)
                    .motivacion(50.0)
                    .forma(5.0)
                    .fatiga(0.0)
                    .equipo(null) // No team
                    .estadisticas(stats)
                    .build();
            
            jugadorRepository.save(jugador);
        }
    }

    private void createEquipo(Liga liga, String nombre, String ciudad, String estadio, String presidente, double presupuesto) {
        Equipo equipo = Equipo.builder()
                .nombre(nombre)
                .ciudad(ciudad)
                .estadio(estadio)
                .presidente(presidente)
                .anyFundacion(1900)
                .liga(liga)
                .presupuesto(new Presupuesto(presupuesto, presupuesto, 500000.0, 200000.0))
                .build();

        equipo = equipoRepository.save(equipo);
        
        // Crear 11 Jugadores
        createJugadores(equipo);
    }

    private void createJugadores(Equipo equipo) {
        // Corrected distribution for 4-4-2: 1 GK, 4 DEF, 4 MIG, 2 DAV
        String[] posiciones = {"POR", "DEF", "DEF", "DEF", "DEF", "MIG", "MIG", "MIG", "MIG", "DAV", "DAV"};
        Random rand = new Random();

        for (int i = 0; i < 11; i++) {
            EstadisticasJugador stats = new EstadisticasJugador();
            
            Jugador jugador = Jugador.builder()
                    .nombre("Jugador " + (i + 1))
                    .apellido(equipo.getNombre()) 
                    .dorsal(i + 1)
                    .posicion(Posicion.valueOf(posiciones[i]))
                    .calidad(60.0 + rand.nextDouble() * 30.0) 
                    .fechaNacimiento(LocalDate.of(1995 + rand.nextInt(10), 1, 1))
                    .sueldo(100000.0 * (i + 1))
                    .motivacion(80.0)
                    .forma(7.0)
                    .fatiga(0.0)
                    .equipo(equipo)
                    .estadisticas(stats)
                    .build();
            
            jugadorRepository.save(jugador);
        }
    }
}
