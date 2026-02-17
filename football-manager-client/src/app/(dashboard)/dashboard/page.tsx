import { StatCard } from "@/components/shared/StatCard"
import { LeagueTable } from "@/components/features/LeagueTable"
import { SimulationControl } from "@/components/features/SimulationControl"
import api from "@/lib/api"
import { LigaDTO, EquipoDTO } from "@/lib/types"
import { Trophy, Users, Activity, Calendar } from "lucide-react"

// Fetch data on the server
async function getData(): Promise<{ ligas: LigaDTO[], equipos: EquipoDTO[] }> {
    try {
        const [ligasRes, equiposRes] = await Promise.all([
            api.get('/ligas'),
            api.get('/equipos') // TODO: Filter by current league if multiple
        ]);
        return {
            ligas: ligasRes.data,
            equipos: equiposRes.data
        };
    } catch (error) {
        console.error("Failed to fetch data", error);
        return { ligas: [], equipos: [] };
    }
}

export default async function DashboardPage() {
    const { ligas, equipos } = await getData();
    const ligaActual = ligas[0]; // Assuming one active league for now

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Resumen de Temporada</h1>
                {ligaActual && (
                    <SimulationControl
                        ligaId={ligaActual.id}
                        jornadaActual={ligaActual.jornadaActual}
                        finalizada={ligaActual.finalizada}
                    />
                )}
            </div>

            {ligaActual ? (
                <>
                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                        <StatCard
                            title="Liga Actual"
                            value={ligaActual.nombre}
                            icon={Trophy}
                            description={`Temporada en curso`}
                        />
                        <StatCard
                            title="Equipos"
                            value={ligaActual.numEquipos}
                            icon={Users}
                            description="Total en competición"
                        />
                        <StatCard
                            title="Jornada"
                            value={`${ligaActual.jornadaActual} / ${ligaActual.totalJornadas}`}
                            icon={Calendar}
                            description="Progreso de la temporada"
                        />
                        <StatCard
                            title="Estado"
                            value={ligaActual.finalizada ? "Finalizada" : "En Curso"}
                            icon={Activity}
                            trend={!ligaActual.finalizada ? "Activo" : "Terminado"}
                            trendUp={!ligaActual.finalizada}
                        />
                    </div>

                    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
                        {/* League Table */}
                        <div className="col-span-4 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Clasificación</h3>
                            <LeagueTable equipos={equipos} />
                        </div>

                        {/* Placeholder for Recent Games */}
                        <div className="col-span-3 rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Últimos Resultados</h3>
                            <div className="flex items-center justify-center h-40 text-muted-foreground">
                                (Resultados de Jornada {ligaActual.jornadaActual > 1 ? ligaActual.jornadaActual - 1 : 'N/A'})
                            </div>
                        </div>
                    </div>
                </>
            ) : (
                <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                    <h3 className="text-lg font-medium">No se encontró ninguna liga activa</h3>
                    <p className="text-muted-foreground">Inicializa la base de datos para comenzar.</p>
                </div>
            )}
        </div>
    )
}
