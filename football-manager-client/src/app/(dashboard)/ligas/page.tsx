import api from "@/lib/api"
import { LigaDTO } from "@/lib/types"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Trophy, Calendar, CheckCircle2, Activity } from "lucide-react"

async function getLigas(): Promise<LigaDTO[]> {
    try {
        const res = await api.get('/ligas');
        return res.data;
    } catch (error) {
        console.error("Failed to fetch leagues", error);
        return [];
    }
}

export default async function LigasPage() {
    const ligas = await getLigas();

    return (
        <div className="space-y-6">
            <h1 className="text-3xl font-bold tracking-tight">Competiciones</h1>

            <div className="grid gap-6 md:grid-cols-2">
                {ligas.map((liga) => (
                    <Card key={liga.id} className="border-neutral-200 dark:border-neutral-800">
                        <CardHeader>
                            <div className="flex items-start justify-between">
                                <div>
                                    <CardTitle className="text-xl text-blue-700 dark:text-blue-400">{liga.nombre}</CardTitle>
                                    <CardDescription>Liga Profesional</CardDescription>
                                </div>
                                <Trophy className="h-8 w-8 text-yellow-500" />
                            </div>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div className="flex items-center gap-3 p-3 bg-neutral-50 dark:bg-neutral-900 rounded-lg">
                                    <Activity className="h-5 w-5 text-blue-500" />
                                    <div>
                                        <p className="text-xs text-muted-foreground">Estado</p>
                                        <p className="font-medium">{liga.finalizada ? "Finalizada" : "En Curso"}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 p-3 bg-neutral-50 dark:bg-neutral-900 rounded-lg">
                                    <Calendar className="h-5 w-5 text-green-500" />
                                    <div>
                                        <p className="text-xs text-muted-foreground">Jornada</p>
                                        <p className="font-medium">{liga.jornadaActual} / {liga.totalJornadas}</p>
                                    </div>
                                </div>
                            </div>

                            <div className="flex items-center justify-between text-sm pt-2 border-t">
                                <span className="text-muted-foreground">Equipos participantes</span>
                                <span className="font-bold">{liga.numEquipos}</span>
                            </div>
                        </CardContent>
                    </Card>
                ))}
                {ligas.length === 0 && (
                    <div className="col-span-full p-12 text-center border rounded-lg bg-neutral-50 dark:bg-neutral-900">
                        <Trophy className="mx-auto h-12 w-12 text-muted-foreground mb-3 opacity-20" />
                        <h3 className="text-lg font-medium">No hay competiciones activas</h3>
                        <p className="text-muted-foreground">La base de datos de ligas está vacía.</p>
                    </div>
                )}
            </div>
        </div>
    )
}
