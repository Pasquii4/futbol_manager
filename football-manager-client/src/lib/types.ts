// Enums
export enum Formacion {
    F_4_4_2 = "F_4_4_2",
    F_4_3_3 = "F_4_3_3",
    F_3_5_2 = "F_3_5_2",
    F_5_3_2 = "F_5_3_2"
}

export enum EstiloJuego {
    OFENSIVO = "OFENSIVO",
    DEFENSIVO = "DEFENSIVO",
    EQUILIBRADO = "EQUILIBRADO",
    POSESION = "POSESION",
    CONTRAATAQUE = "CONTRAATAQUE"
}

export enum Posicion {
    POR = "POR",
    DEF = "DEF",
    MIG = "MIG",
    DAV = "DAV"
}

// DTOs
export interface LigaDTO {
    id: number;
    nombre: string;
    numEquipos: number;
    jornadaActual: number;
    totalJornadas: number;
    finalizada: boolean;
    equipos?: EquipoDTO[];
}

export interface EquipoDTO {
    id: number;
    nombre: string;
    anyFundacion: number;
    ciudad: string;
    estadio: string;
    presidente: string;
    presupuesto: number;
    totalSalarios: number;
    puntos: number;
    golesFavor: number;
    golesContra: number;
    partidosJugados: number;
    victorias: number;
    empates: number;
    derrotas: number;
    entrenador?: EntrenadorDTO;
    tactica?: TacticaDTO;
    jugadores?: JugadorDTO[];
}

export interface EntrenadorDTO {
    id: number;
    nombre: string;
    apellido: string;
    experiencia: number;
    nivelMotivacion: number;
    sueldoAnual: number;
}

export interface TacticaDTO {
    id: number;
    formacion: Formacion;
    estiloJuego: EstiloJuego;
    intensidadPresion: number;
}

export interface JugadorDTO {
    id: number;
    nombre: string;
    apellido: string;
    dorsal: number;
    posicion: Posicion;
    calidad: number;
    fechaNacimiento: string;
    nacionalidad: string;
    sueldo: number;
    motivacion: number;
    forma: number;
    fatiga: number;
    lesionado: boolean;
    equipoId?: number;
    estadisticas?: EstadisticasDTO;
}

export interface EstadisticasDTO {
    goles: number;
    asistencias: number;
    tarjetasAmarillas: number;
    tarjetasRojas: number;
    partidosJugados: number;
    minutosJugados: number;
    ratingPromedio: number;
}

export interface PartidoDTO {
    id: number;
    jornada: number;
    equipoLocalId: number;
    equipoLocalNombre: string;
    equipoVisitanteId: number;
    equipoVisitanteNombre: string;
    golesLocal: number;
    golesVisitante: number;
    jugado: boolean;
    fechaPartido?: string;
    ratingLocal?: number;
    ratingVisitante?: number;
    eventos: EventoPartidoDTO[];
}

export interface EventoPartidoDTO {
    id: number;
    minuto: number;
    tipo: string;
    descripcion: string;
    jugadorNombre: string;
}
