import { getLeagueStatus, getTopScorers, getTopAssists, getLeagueStatus as getStatusAPI, PlayerStatsDTO } from "@/lib/api"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Trophy, Medal, Goal, Footprints } from "lucide-react"

export default async function EstadisticasPage() {
    // Parallel data fetching
    const [leagueStatus, topScorers, topAssisters] = await Promise.all([
        getLeagueStatus(),
        getTopScorers(),
        getTopAssists()
    ]);

    if (!leagueStatus) {
        return <div className="p-8 text-center text-muted-foreground">Cargando datos...</div>;
    }

    // Goals per game - we need total goals for this. 
    // Since we don't have a specific "LeagueStats" endpoint yet beyond status, we might need a small one or accept we don't show total goals yet or fetch matches.
    // For now, let's remove strict dependency on 'all teams' to speed this up, or keep it simple.
    // Actually, league status doesn't have total goals. 
    // Let's hide the top cards for now or use placeholders until we make a proper /stats/league endpoint.
    // Or we can quickly fetch all matches? No, that's heavy.
    // Let's display what we have: Scorers and Assists.

    return (
        <div className="space-y-8">
            <div className="flex items-center gap-4">
                <div className="p-3 bg-yellow-100 text-yellow-700 rounded-lg">
                    <Trophy size={32} />
                </div>
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Estadísticas de La Liga</h1>
                    <p className="text-muted-foreground">Jornada {leagueStatus.currentMatchday} de {leagueStatus.totalMatchdays}</p>
                </div>
            </div>

            {/* Config Cards */}
            {/* 
            <div className="grid gap-4 md:grid-cols-3">
                 Configuration Cards Placeholder 
                 We can re-enable these when we have a specialized endpoint 
            </div> 
            */}

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
                                            <TableCell>{player.name}</TableCell>
                                            <TableCell className="text-muted-foreground">{player.teamName}</TableCell>
                                            <TableCell className="text-right font-bold">{player.goalsScored}</TableCell>
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
                                            <TableCell>{player.name}</TableCell>
                                            <TableCell className="text-muted-foreground">{player.teamName}</TableCell>
                                            <TableCell className="text-right font-bold">{player.assists}</TableCell>
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
