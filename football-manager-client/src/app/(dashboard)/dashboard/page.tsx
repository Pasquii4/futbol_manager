'use client';

import { StatCard } from "@/components/shared/StatCard"
import { LeagueTable } from "@/components/features/LeagueTable"
import { getLeagueStatus, getStandings, StandingsItem, LeagueStatus, resetLeague } from "@/lib/api"
import { Trophy, Users, Activity, Calendar } from "lucide-react"
import { useRouter } from 'next/navigation';
import { Button } from "@/components/ui/button"
import { useState, useEffect } from 'react';

export default function DashboardPage() {
    const router = useRouter();
    const [leagueStatus, setLeagueStatus] = useState<LeagueStatus | null>(null);
    const [standings, setStandings] = useState<StandingsItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const status = await getLeagueStatus();
                setLeagueStatus(status);

                // Redirect to Select Team if not yet selected
                if (status.managedTeamId === null && status.isStarted) {
                    router.push('/select-team');
                    return;
                }

                if (status && status.isStarted) {
                    const table = await getStandings();
                    setStandings(table);
                }
            } catch (error) {
                console.error("Failed to fetch dashboard data", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [router]);

    if (loading) {
        return (
            <div className="space-y-6">
                <div className="flex items-center justify-between">
                    <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
                </div>
                <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                    <p className="text-muted-foreground">Cargando datos de la temporada...</p>
                </div>
            </div>
        );
    }

    const teamsCount = standings.length > 0 ? standings.length : 20;

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
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
                        } catch (error) {
                            console.error(error);
                            alert("Error al inicializar la liga");
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
