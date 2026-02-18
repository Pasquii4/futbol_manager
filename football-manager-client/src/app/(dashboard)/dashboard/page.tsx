import { StatCard } from "@/components/shared/StatCard"
import { LeagueTable } from "@/components/features/LeagueTable"
import { getLeagueStatus, getStandings, StandingsItem } from "@/lib/api"
import { Trophy, Users, Activity, Calendar } from "lucide-react"

export default async function DashboardPage() {
    let status = null;
    let standings: StandingsItem[] = [];
    let teamsCount = 20; // Default

    try {
        status = await getLeagueStatus();
        if (status && status.isStarted) {
            standings = await getStandings();
            teamsCount = standings.length > 0 ? standings.length : 20;
        }
    } catch (e) {
        console.error("Failed to load dashboard data", e);
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
            </div>

            {status && status.isStarted ? (
                <>
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                        <StatCard
                            title="Temporada"
                            value={status.seasonYear.toString()}
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
                            value={`${status.currentMatchday} / ${status.totalMatchdays}`}
                            icon={Calendar}
                            description="Progreso actual"
                        />
                        <StatCard
                            title="Estado"
                            value={status.isFinished ? "Finalizada" : "En Curso"}
                            icon={Activity}
                            trend={!status.isFinished ? "Activo" : "Terminado"}
                            trendUp={!status.isFinished}
                        />
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                        {/* League Table */}
                        <div className="col-span-4 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Clasificación</h3>
                            {/* Passing standings directly if LeagueTable supports it, else verify adapter */}
                            <LeagueTable data={standings} />
                        </div>

                        {/* Recent Games Placeholder */}
                        <div className="col-span-3 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Próximos Partidos</h3>
                            <div className="flex items-center justify-center h-40 text-muted-foreground">
                                (Jornada {status.currentMatchday})
                            </div>
                        </div>
                    </div>
                </>
            ) : (
                <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                    <h3 className="text-lg font-medium">No se encontró ninguna liga activa</h3>
                    <p className="text-muted-foreground">La temporada no ha comenzado o no hay datos cargados.</p>
                </div>
            )}
        </div>
    )
}
