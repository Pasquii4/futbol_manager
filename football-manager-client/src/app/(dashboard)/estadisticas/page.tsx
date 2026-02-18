import api from "@/lib/api"
import { LigaDTO, JugadorDTO, EquipoDTO } from "@/lib/types"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Trophy, Medal, Goal, Footprints } from "lucide-react"

async function getStatsData() {
    try {
        const res = await api.get<LigaDTO[]>('/ligas');
        if (!res.data || res.data.length === 0) return null;

        // Use first league for now
        const ligaId = res.data[0].id;
        const ligaRes = await api.get<LigaDTO>(`/ligas/${ligaId}`);
        return ligaRes.data;
    } catch (error) {
        console.error("Failed to fetch stats data", error);
        return null;
    }
}

function processTopScorers(equipos: EquipoDTO[]): JugadorDTO[] {
    const allPlayers: JugadorDTO[] = [];
    equipos.forEach(eq => {
        if (eq.jugadores) {
            // Add team name to player for display
            eq.jugadores.forEach(j => {
                // @ts-ignore
                j.equipoNombre = eq.nombre;
                allPlayers.push(j);
            });
        }
    });

    return allPlayers
        .filter(j => (j.estadisticas?.goles || 0) > 0)
        .sort((a, b) => (b.estadisticas?.goles || 0) - (a.estadisticas?.goles || 0))
        .slice(0, 10);
}

function processTopAssisters(equipos: EquipoDTO[]): JugadorDTO[] {
    const allPlayers: JugadorDTO[] = [];
    equipos.forEach(eq => {
        if (eq.jugadores) {
            eq.jugadores.forEach(j => {
                // @ts-ignore
                j.equipoNombre = eq.nombre;
                allPlayers.push(j);
            });
        }
    });

    return allPlayers
        .filter(j => (j.estadisticas?.asistencias || 0) > 0)
        .sort((a, b) => (b.estadisticas?.asistencias || 0) - (a.estadisticas?.asistencias || 0))
        .slice(0, 10);
}

export default async function EstadisticasPage() {
    const liga = await getStatsData();

    if (!liga || !liga.equipos) {
        return <div className="p-8 text-center text-muted-foreground">Cargando datos o no hay liga activa...</div>;
    }

    const topScorers = processTopScorers(liga.equipos);
    const topAssisters = processTopAssisters(liga.equipos);

    // Calculate totals
    const totalGoals = liga.equipos.reduce((acc, eq) => acc + (eq.golesFavor || 0), 0);
    const totalMatches = liga.equipos.reduce((acc, eq) => acc + (eq.partidosJugados || 0), 0) / 2; // Each match counts twice
    const avgGoals = totalMatches > 0 ? (totalGoals / totalMatches).toFixed(2) : "0.00";

    return (
        <div className="space-y-8">
            <div className="flex items-center gap-4">
                <div className="p-3 bg-yellow-100 text-yellow-700 rounded-lg">
                    <Trophy size={32} />
                </div>
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Estadísticas de {liga.nombre}</h1>
                    <p className="text-muted-foreground">Jornada {liga.jornadaActual} de {liga.totalJornadas}</p>
                </div>
            </div>

            {/* Config Cards */}
            <div className="grid gap-4 md:grid-cols-3">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Goles Totales</CardTitle>
                        <Goal className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{totalGoals}</div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Partidos Jugados</CardTitle>
                        <Footprints className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{totalMatches}</div>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Goles por Partido</CardTitle>
                        <Mediachart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{avgGoals}</div>
                    </CardContent>
                </Card>
            </div>

            <div className="grid md:grid-cols-2 gap-8">
                {/* Top Scorers */}
                <Card className="col-span-1">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Goal className="h-5 w-5 text-blue-500" />
                            Máximos Goleadores (Pichichi)
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Pos</TableHead>
                                    <TableHead>Jugador</TableHead>
                                    <TableHead>Equipo</TableHead>
                                    <TableHead className="text-right">Goles</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {topScorers.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                                            Aún no hay goles registrados
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    topScorers.map((player, index) => (
                                        <TableRow key={player.id}>
                                            <TableCell className="font-medium">#{index + 1}</TableCell>
                                            <TableCell>{player.nombre} {player.apellido}</TableCell>
                                            {/* @ts-ignore */}
                                            <TableCell className="text-muted-foreground">{player.equipoNombre}</TableCell>
                                            <TableCell className="text-right font-bold">{player.estadisticas?.goles}</TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>

                {/* Top Assists */}
                <Card className="col-span-1">
                    <CardHeader>
                        <CardTitle className="flex items-center gap-2">
                            <Medal className="h-5 w-5 text-green-500" />
                            Máximos Asistentes
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <Table>
                            <TableHeader>
                                <TableRow>
                                    <TableHead>Pos</TableHead>
                                    <TableHead>Jugador</TableHead>
                                    <TableHead>Equipo</TableHead>
                                    <TableHead className="text-right">Asist</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {topAssisters.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                                            Aún no hay asistencias registradas
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    topAssisters.map((player, index) => (
                                        <TableRow key={player.id}>
                                            <TableCell className="font-medium">#{index + 1}</TableCell>
                                            <TableCell>{player.nombre} {player.apellido}</TableCell>
                                            {/* @ts-ignore */}
                                            <TableCell className="text-muted-foreground">{player.equipoNombre}</TableCell>
                                            <TableCell className="text-right font-bold">{player.estadisticas?.asistencias}</TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}

function Mediachart(props: any) {
    return (
        <svg
            {...props}
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            <path d="M3 3v18h18" />
            <path d="M18 17V9" />
            <path d="M13 17V5" />
            <path d="M8 17v-3" />
        </svg>
    )
}
