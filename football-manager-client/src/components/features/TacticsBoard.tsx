import { JugadorDTO, Formacion, Posicion } from "@/lib/types"
import { cn } from "@/lib/utils"

interface TacticsBoardProps {
    formation: Formacion
    jugadores: JugadorDTO[]
}

export function TacticsBoard({ formation, jugadores }: TacticsBoardProps) {
    // Simplified logic to distribute players on the field based on formation
    // In a real app, we would calculate precise coordinates

    const portero = jugadores.find(j => j.posicion === Posicion.POR);
    const defensas = jugadores.filter(j => j.posicion === Posicion.DEF);
    const mediocampistas = jugadores.filter(j => j.posicion === Posicion.MIG);
    const delanteros = jugadores.filter(j => j.posicion === Posicion.DAV);

    return (
        <div className="relative w-full aspect-[2/3] bg-green-600 rounded-lg border-2 border-white/20 shadow-inner overflow-hidden p-4">
            {/* Field Markings */}
            <div className="absolute inset-4 border-2 border-white/30 rounded-sm"></div>
            <div className="absolute top-1/2 left-0 right-0 border-t border-white/30"></div>
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-24 h-24 border border-white/30 rounded-full"></div>

            {/* Goals areas */}
            <div className="absolute bottom-4 left-1/2 -translate-x-1/2 w-32 h-16 border border-white/30 border-b-0"></div>
            <div className="absolute top-4 left-1/2 -translate-x-1/2 w-32 h-16 border border-white/30 border-t-0"></div>

            {/* Players Layer */}
            <div className="relative h-full flex flex-col justify-between py-8">

                {/* Delanteros (Top - Opponent area in this view? Or Bottom is GK?)
              Let's assume Bottom is GK (Home)
           */}

                {/* Row 4: Forwards */}
                <div className="flex justify-center gap-8">
                    {delanteros.map(p => <PlayerToken key={p.id} player={p} />)}
                </div>

                {/* Row 3: Midfielders */}
                <div className="flex justify-center gap-8">
                    {mediocampistas.map(p => <PlayerToken key={p.id} player={p} />)}
                </div>

                {/* Row 2: Defenders */}
                <div className="flex justify-center gap-8">
                    {defensas.map(p => <PlayerToken key={p.id} player={p} />)}
                </div>

                {/* Row 1: Goalkeeper */}
                <div className="flex justify-center">
                    {portero && <PlayerToken player={portero} isGK />}
                </div>
            </div>
        </div>
    )
}

function PlayerToken({ player, isGK }: { player: JugadorDTO; isGK?: boolean }) {
    return (
        <div className="flex flex-col items-center gap-1 group cursor-pointer hover:scale-110 transition-transform">
            <div className={cn(
                "w-10 h-10 rounded-full flex items-center justify-center text-xs font-bold shadow-md border-2",
                isGK ? "bg-yellow-400 border-yellow-600 text-yellow-900" : "bg-red-500 border-red-700 text-white"
            )}>
                {player.dorsal}
            </div>
            <span className="text-[10px] bg-black/50 text-white px-2 py-0.5 rounded-full backdrop-blur-sm whitespace-nowrap">
                {player.apellido}
            </span>
        </div>
    )
}
