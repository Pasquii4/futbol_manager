import api from "@/lib/api"
import { EquipoDTO } from "@/lib/types"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Users, MapPin, Trophy } from "lucide-react"
import Link from "next/link"

async function getEquipos(): Promise<EquipoDTO[]> {
    try {
        const res = await api.get('/equipos');
        return res.data;
    } catch (error) {
        console.error("Failed to fetch teams", error);
        return [];
    }
}

export default async function EquiposPage() {
    const equipos = await getEquipos();

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Mis Equipos</h1>
                <span className="text-muted-foreground">{equipos.length} Equipos registrados</span>
            </div>

            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                {equipos.map((equipo) => (
                    <Link key={equipo.id} href={`/equipos/${equipo.id}`} className="block group">
                        <Card className="h-full transition-shadow hover:shadow-md border-neutral-200 dark:border-neutral-800">
                            <CardHeader className="flex flex-row items-center justify-between pb-2">
                                <CardTitle className="text-lg font-bold group-hover:text-blue-600 transition-colors">
                                    {equipo.nombre}
                                </CardTitle>
                                <Users className="h-4 w-4 text-muted-foreground" />
                            </CardHeader>
                            <CardContent>
                                <div className="space-y-2 text-sm text-neutral-600 dark:text-neutral-400">
                                    <div className="flex items-center gap-2">
                                        <MapPin size={14} />
                                        <span>{equipo.ciudad}</span>
                                    </div>
                                    <div className="flex items-center gap-2">
                                        <Trophy size={14} />
                                        <span>{equipo.puntos} pts</span>
                                    </div>
                                    <div className="pt-2 border-t mt-2 flex justify-between text-xs">
                                        <span>Fundado: {equipo.anyFundacion || 'N/A'}</span>
                                        <span>Estadio: {equipo.estadio}</span>
                                    </div>
                                </div>
                            </CardContent>
                        </Card>
                    </Link>
                ))}
                {equipos.length === 0 && (
                    <div className="col-span-full p-8 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                        <p className="text-muted-foreground">No se encontraron equipos.</p>
                    </div>
                )}
            </div>
        </div>
    )
}
