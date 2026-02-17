import api from "@/lib/api"
import { JugadorDTO } from "@/lib/types"
import { PlayerList } from "@/components/features/PlayerList"
import { ShoppingBag } from "lucide-react"

async function getMercado(): Promise<JugadorDTO[]> {
    try {
        const res = await api.get('/jugadores/mercado');
        return res.data;
    } catch (error) {
        console.error("Failed to fetch market", error);
        return [];
    }
}

export default async function MercadoPage() {
    const jugadores = await getMercado();

    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <div className="p-3 bg-blue-100 text-blue-700 rounded-lg">
                    <ShoppingBag size={32} />
                </div>
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Mercado de Fichajes</h1>
                    <p className="text-muted-foreground">Jugadores libres disponibles para contratar</p>
                </div>
            </div>

            <div className="rounded-xl border bg-card text-card-foreground shadow">
                <div className="p-6">
                    <PlayerList jugadores={jugadores} />
                </div>
            </div>
        </div>
    )
}
