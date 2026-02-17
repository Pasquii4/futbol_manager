import api from "@/lib/api"
import { EquipoDTO, Formacion } from "@/lib/types"
import { StatCard } from "@/components/shared/StatCard"
import { PlayerList } from "@/components/features/PlayerList"
import { TacticsBoard } from "@/components/features/TacticsBoard"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Users, Shield, TrendingUp, DollarSign } from "lucide-react"

interface PageProps {
    params: {
        id: string
    }
}

async function getEquipo(id: string): Promise<EquipoDTO | null> {
    try {
        const res = await api.get(`/equipos/${id}`);
        return res.data;
    } catch (error) {
        console.error(`Failed to fetch team ${id}`, error);
        return null;
    }
}

export default async function EquipoPage({ params }: PageProps) {
    const equipo = await getEquipo(params.id);

    if (!equipo) {
        return <div>Equipo no encontrado</div>
    }

    const jugadores = equipo.jugadores || [];
    // Calculate stats from players or use team stats
    const valorTotal = jugadores.reduce((sum, j) => sum + (j.calidad * 1000000), 0); // Mock value calculation
    const calidadPromedio = jugadores.length > 0
        ? jugadores.reduce((sum, j) => sum + j.calidad, 0) / jugadores.length
        : 0;

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex flex-col gap-2">
                <h1 className="text-3xl font-bold tracking-tight">{equipo.nombre}</h1>
                <p className="text-muted-foreground">
                    Estadio: {equipo.estadio} | Ciudad: {equipo.ciudad} | Presidente: {equipo.presidente}
                </p>
            </div>

            {/* Stats Overview */}
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
                <StatCard
                    title="Clasificación"
                    value={`${equipo.puntos} pts`}
                    icon={TrendingUp}
                    description={`Posición estim: ?`}
                />
                <StatCard
                    title="Plantilla"
                    value={jugadores.length}
                    icon={Users}
                    description="Jugadores en ficha"
                />
                <StatCard
                    title="Media Calidad"
                    value={calidadPromedio.toFixed(1)}
                    icon={Shield}
                    description="Nivel general"
                />
                <StatCard
                    title="Valor Estimado"
                    value={`${(valorTotal / 1000000).toFixed(1)}M €`}
                    icon={DollarSign}
                    description="Valor de mercado"
                />
            </div>

            {/* Tabs Content */}
            <Tabs defaultValue="plantilla" className="space-y-4">
                <TabsList>
                    <TabsTrigger value="plantilla">Plantilla</TabsTrigger>
                    <TabsTrigger value="tactica">Táctica</TabsTrigger>
                    <TabsTrigger value="historia">Historia</TabsTrigger>
                </TabsList>

                <TabsContent value="plantilla" className="space-y-4">
                    <div className="rounded-xl border bg-card text-card-foreground shadow">
                        <div className="p-6">
                            <h3 className="font-semibold mb-4">Jugadores del Primer Equipo</h3>
                            <PlayerList jugadores={jugadores} />
                        </div>
                    </div>
                </TabsContent>

                <TabsContent value="tactica" className="space-y-4">
                    <div className="grid gap-4 md:grid-cols-2">
                        <div className="rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Pizarra Táctica</h3>
                            <TacticsBoard
                                formation={equipo.tactica?.formacion || Formacion.F_4_4_2}
                                jugadores={jugadores}
                            />
                        </div>
                        <div className="rounded-xl border bg-card text-card-foreground shadow p-6">
                            <h3 className="font-semibold mb-4">Configuración</h3>
                            <div className="space-y-4">
                                <div className="grid grid-cols-2 gap-4">
                                    <div className="p-4 border rounded-lg">
                                        <span className="text-sm text-muted-foreground block">Formación</span>
                                        <span className="font-bold text-lg">{equipo.tactica?.formacion || '4-4-2'}</span>
                                    </div>
                                    <div className="p-4 border rounded-lg">
                                        <span className="text-sm text-muted-foreground block">Estilo</span>
                                        <span className="font-bold text-lg">{equipo.tactica?.estiloJuego || 'Equilibrado'}</span>
                                    </div>
                                </div>
                                {/* Add controls here later */}
                                <div className="p-4 bg-yellow-50 dark:bg-yellow-900/20 text-yellow-800 dark:text-yellow-200 rounded-md text-sm">
                                    La edición de tácticas estará disponible próximamente.
                                </div>
                            </div>
                        </div>
                    </div>
                </TabsContent>

                <TabsContent value="historia">
                    <div className="p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                        <p className="text-muted-foreground">Historial de partidos no disponible.</p>
                    </div>
                </TabsContent>
            </Tabs>
        </div>
    )
}
