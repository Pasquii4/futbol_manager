'use client';

import { StatCard } from "@/components/shared/StatCard"
import { LeagueTable } from "@/components/features/LeagueTable"
import { SimulationControl } from "@/components/features/SimulationControl"
import { getLeagueStatus, getStandings, StandingsItem, LeagueStatus, resetLeague } from "@/lib/api"
import { Trophy, Users, Activity, Calendar, AlertCircle } from "lucide-react"
import { useRouter } from 'next/navigation';
import { Button } from "@/components/ui/button"
import { useState, useEffect } from 'react';

export default function DashboardPage() {
    const router = useRouter();
    const [leagueStatus, setLeagueStatus] = useState<LeagueStatus | null>(null);
    const [standings, setStandings] = useState<StandingsItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);
            const status = await getLeagueStatus();
            setLeagueStatus(status);

            if (status.managedTeamId === null && status.isStarted) {
                router.push('/select-team');
                return;
            }

            if (status && status.isStarted) {
                const table = await getStandings();
                setStandings(table);
            }
        } catch (err) {
            console.error("Failed to fetch dashboard data", err);
            setError('No se pudieron cargar los datos. Verifica que el backend esté ejecutándose en http://localhost:8080');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [router]);

    if (loading) {
        return (
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
                </div>
                <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mb-3"></div>
                    <p className="text-muted-foreground">Cargando datos de la temporada...</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
                </div>
                <div className="p-8 text-center border rounded-lg border-red-200 bg-red-50 dark:bg-red-900/20 dark:border-red-800">
                    <AlertCircle className="h-10 w-10 text-red-500 mx-auto mb-3" />
                    <h3 className="text-lg font-semibold text-red-700 dark:text-red-400 mb-2">Error de conexión</h3>
                    <p className="text-red-600 dark:text-red-300 mb-4 max-w-md mx-auto">{error}</p>
                    <Button onClick={fetchData} variant="outline" className="border-red-300 text-red-700 hover:bg-red-100">
                        Reintentar
                    </Button>
                </div>
            </div>
        );
    }

    const teamsCount = standings.length > 0 ? standings.length : 20;

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
                {leagueStatus && leagueStatus.isStarted && (
                    <SimulationControl 
                        ligaId={1} 
                        jornadaActual={leagueStatus.currentMatchday} 
                        finalizada={leagueStatus.isFinished} 
                        onSimulate={fetchData} 
                    />
                )}
            </div>

            {leagueStatus && leagueStatus.isStarted ? (
                <>
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                        <StatCard
                            title="Temporada"
                            value={leagueStatus.seasonYear.toString()}
                            icon={Trophy}
                            description="LaLiga EA Sports"
                        />
                        <StatCard
                            title="Equipos"
                            value={teamsCount.toString()}
                            icon={Users}
                            description="Total en competición"
                        />
                        <StatCard
                            title="Jornada"
                            value={`${leagueStatus.currentMatchday} / ${leagueStatus.totalMatchdays}`}
                            icon={Calendar}
                            description="Progreso actual"
                        />
                        <StatCard
                            title="Estado"
                            value={leagueStatus.isFinished ? "Finalizada" : "En Curso"}
                            icon={Activity}
                            trend={!leagueStatus.isFinished ? "Activo" : "Terminado"}
                            trendUp={!leagueStatus.isFinished}
                        />
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                        <div className="col-span-4 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Clasificación</h3>
                            <LeagueTable data={standings} />
                        </div>

                        <div className="col-span-3 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Próximos Partidos</h3>
                            <div className="flex items-center justify-center h-40 text-muted-foreground">
                                (Jornada {leagueStatus.currentMatchday})
                            </div>
                        </div>
                    </div>
                </>
            ) : (
                <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                    <h3 className="text-lg font-medium">No se encontró ninguna liga activa</h3>
                    <p className="text-muted-foreground mb-6">La temporada no ha comenzado o no hay datos cargados.</p>
                    <Button onClick={async () => {
                        setLoading(true);
                        try {
                            await resetLeague();
                            router.push('/select-team');
                        } catch (err) {
                            console.error(err);
                            setError("Error al inicializar la liga. Verifica que el backend esté ejecutándose.");
                            setLoading(false);
                        }
                    }}>
                        Inicializar Nueva Liga
                    </Button>
                </div>
            )}
        </div>
    )
}
