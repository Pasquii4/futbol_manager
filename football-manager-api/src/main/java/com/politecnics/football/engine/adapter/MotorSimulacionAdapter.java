package com.politecnics.football.engine.adapter;

import com.politecnics.football.engine.model.*;
import com.politecnics.football.entity.Equipo;
import com.politecnics.football.entity.EstadisticasJugador;
import com.politecnics.football.entity.EventoPartido;
import com.politecnics.football.entity.Jugador;
import com.politecnics.football.entity.Partido;
import com.politecnics.football.entity.Tactica;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MotorSimulacionAdapter {

    public void simularPartido(Partido partidoEntity) {
        // 1. Convert JPA Entities to Engine Model
        Equip equip1 = mapToEngineEquip(partidoEntity.getEquipoLocal());
        Equip equip2 = mapToEngineEquip(partidoEntity.getEquipoVisitante());
        
        // 2. Create Engine Match
        Partit partitEngine = new Partit(equip1, equip2, partidoEntity.getJornada());
        
        // 3. Run Simulation
        partitEngine.disputar();
        
        // 4. Update JPA Entity with results
        partidoEntity.setGolesLocal(partitEngine.getGolsEquip1());
        partidoEntity.setGolesVisitante(partitEngine.getGolsEquip2());
        partidoEntity.setJugado(true);
        partidoEntity.setFechaPartido(LocalDateTime.now());
        partidoEntity.setRatingLocal(partitEngine.getRatingEquip1());
        partidoEntity.setRatingVisitante(partitEngine.getRatingEquip2());
        
        // 5. Map Events
        List<EventoPartido> eventosEntities = partitEngine.getEventos().stream()
            .map(evt -> mapToEntityEvento(evt, partidoEntity))
            .collect(Collectors.toList());
        
        if (partidoEntity.getEventos() == null) {
            partidoEntity.setEventos(new ArrayList<>());
        }
        partidoEntity.getEventos().clear();
        partidoEntity.getEventos().addAll(eventosEntities);
        
        // 6. Update Players Stats (Engine updated its own objects, we need to sync back)
        syncJugadoresStats(equip1, partidoEntity.getEquipoLocal());
        syncJugadoresStats(equip2, partidoEntity.getEquipoVisitante());
        
        // 7. Update Team Stats
        updateEquipoStats(partidoEntity.getEquipoLocal(), partitEngine.getGolsEquip1(), partitEngine.getGolsEquip2());
        updateEquipoStats(partidoEntity.getEquipoVisitante(), partitEngine.getGolsEquip2(), partitEngine.getGolsEquip1());
    }
    
    private void updateEquipoStats(Equipo equipo, int golesFavor, int golesContra) {
        equipo.setPartidosJugados(equipo.getPartidosJugados() + 1);
        equipo.setGolesFavor(equipo.getGolesFavor() + golesFavor);
        equipo.setGolesContra(equipo.getGolesContra() + golesContra);
        
        if (golesFavor > golesContra) {
            equipo.setPuntos(equipo.getPuntos() + 3);
            equipo.setVictorias(equipo.getVictorias() + 1);
        } else if (golesFavor == golesContra) {
            equipo.setPuntos(equipo.getPuntos() + 1);
            equipo.setEmpates(equipo.getEmpates() + 1);
        } else {
            equipo.setDerrotas(equipo.getDerrotas() + 1);
        }
    }
    
    // --- Mapping Methods ---
    
    private Equip mapToEngineEquip(Equipo entity) {
        Equip equip = new Equip(entity.getNombre());
        
        // Map Coach
        if (entity.getEntrenador() != null) {
            Entrenador entrenador = new Entrenador(
                entity.getEntrenador().getNombre(),
                entity.getEntrenador().getApellido(),
                entity.getEntrenador().getFechaNacimiento(),
                entity.getEntrenador().getMotivacion(),
                entity.getEntrenador().getSueldoAnual()
            );
            equip.setEntrenador(entrenador);
        }
        
        // Map Tactics
        Tactica tacticaEntity = entity.getTactica();
        if (tacticaEntity != null) {
            TacticaEquip tactica = new TacticaEquip(
                Formacion.valueOf(tacticaEntity.getFormacion().name()),
                EstiloJoc.valueOf(tacticaEntity.getEstiloJuego().name()),
                tacticaEntity.getIntensidadPresion()
            );
            equip.setTactica(tactica);
        }
        
        // Map Players
        for (Jugador jEntity : entity.getJugadores()) {
            com.politecnics.football.engine.model.Jugador jEngine = mapToEngineJugador(jEntity);
            equip.afegirJugador(jEngine);
        }
        
        return equip;
    }
    
    private com.politecnics.football.engine.model.Jugador mapToEngineJugador(Jugador entity) {
        com.politecnics.football.engine.model.Jugador j = new com.politecnics.football.engine.model.Jugador(
            entity.getNombre(),
            entity.getApellido(),
            entity.getFechaNacimiento(),
            entity.getMotivacion(),
            entity.getSueldo(),
            entity.getDorsal(),
            entity.getPosicion().name(),
            entity.getCalidad()
        );
        
        j.setFatiga(entity.getFatiga());
        j.setForma(entity.getForma());
        
        // Stats mapping needed for engine logic?
        // Engine restarts stats mostly, or accumulates? 
        // Engine uses EstadisticasJugador internally. We should prepopulate if we want persistency across games in simulation context?
        // For simulation of ONE match, we generally start with current state.
        
        return j;
    }
    
    private EventoPartido mapToEntityEvento(com.politecnics.football.engine.model.EventoPartido evtEngine, Partido partido) {
        EventoPartido evt = new EventoPartido();
        evt.setMinuto(evtEngine.getMinuto());
        evt.setDescripcion(evtEngine.getDescripcion());
        evt.setTipo(EventoPartido.TipoEvento.valueOf(evtEngine.getTipo().name()));
        evt.setPartido(partido);
        
        // Find player entity
        Equipo equipo = null;
        // Optimization: search in local or visitor
        // We assume we can find the player by dorsal/name in the passed entities
        Jugador jugadorEntity = findJugadorInTeam(evtEngine.getJugador(), partido.getEquipoLocal());
        if (jugadorEntity == null) {
            jugadorEntity = findJugadorInTeam(evtEngine.getJugador(), partido.getEquipoVisitante());
        }
        evt.setJugador(jugadorEntity);
        
        return evt;
    }
    
    private Jugador findJugadorInTeam(com.politecnics.football.engine.model.Jugador jEngine, Equipo equipo) {
        return equipo.getJugadores().stream()
            .filter(j -> j.getDorsal().equals(jEngine.getDorsal()) && j.getNombre().equals(jEngine.getNom()))
            .findFirst()
            .orElse(null);
    }
    
    private void syncJugadoresStats(Equip engineEquip, Equipo entityEquip) {
        for (com.politecnics.football.engine.model.Jugador jEngine : engineEquip.getJugadors()) {
            Jugador jEntity = findJugadorInTeam(jEngine, entityEquip);
            if (jEntity != null) {
                // Update stats
                EstadisticasJugador statsEntity = jEntity.getEstadisticas();
                if (statsEntity == null) {
                    statsEntity = new EstadisticasJugador();
                    jEntity.setEstadisticas(statsEntity);
                }
                
                com.politecnics.football.engine.model.EstadisticasJugador statsEngine = jEngine.getEstadisticas();
                
                // Add new stats to existing (accumulation happens here)
                statsEntity.setGoles(statsEntity.getGoles() + statsEngine.getGoles());
                statsEntity.setAsistencias(statsEntity.getAsistencias() + statsEngine.getAsistencias());
                statsEntity.setTarjetasAmarillas(statsEntity.getTarjetasAmarillas() + statsEngine.getTarjetasAmarillas());
                statsEntity.setTarjetasRojas(statsEntity.getTarjetasRojas() + statsEngine.getTarjetasRojas());
                statsEntity.setPartidosJugados(statsEntity.getPartidosJugados() + statsEngine.getPartidosJugados());
                statsEntity.setMinutosJugados(statsEntity.getMinutosJugados() + statsEngine.getMinutosJugados());
                
                // Rating average update logic could be complex. Simplified:
                statsEntity.setRatingPromedio((statsEntity.getRatingPromedio() + statsEngine.getRatingPromedio()) / 2.0); // Rough average
                
                // Update dynamic attributes
                jEntity.setFatiga(jEngine.getFatiga());
                jEntity.setForma(jEngine.getForma());
                jEntity.setMotivacion(jEngine.getMotivacio());
                
                // Map Injury
                if (jEngine.getLesionActual() != null && jEntity.getLesion() == null) {
                    com.politecnics.football.entity.Lesion lesionEntity = new com.politecnics.football.entity.Lesion();
                    lesionEntity.setTipoLesion(jEngine.getLesionActual().getTipoLesion());
                    lesionEntity.setDiasRestantes(jEngine.getLesionActual().getDiasRecuperacion());
                    lesionEntity.setJugador(jEntity);
                    jEntity.setLesion(lesionEntity);
                }
            }
        }
    }
}
