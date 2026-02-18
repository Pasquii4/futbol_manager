"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Play, Loader2 } from "lucide-react"
import { useRouter } from "next/navigation"
import api from "@/lib/api"

interface SimulationControlProps {
    ligaId: number
    jornadaActual: number
    finalizada: boolean
}

export function SimulationControl({ ligaId, jornadaActual, finalizada }: SimulationControlProps) {
    const [loading, setLoading] = useState(false)
    const router = useRouter()

    const handleSimulate = async () => {
        if (finalizada) return;

        setLoading(true)
        try {
            await api.post(`/simulate/matchday`)
            router.refresh() // Refresh server components to show new stats/table
        } catch (error) {
            console.error("Simulation failed", error)
            alert("Error al simular la jornada")
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="flex items-center gap-4">
            <Button
                onClick={handleSimulate}
                disabled={loading || finalizada}
                className="bg-green-600 hover:bg-green-700 text-white"
            >
                {loading ? (
                    <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Simulando...
                    </>
                ) : (
                    <>
                        <Play className="mr-2 h-4 w-4" />
                        Simular Jornada {jornadaActual}
                    </>
                )}
            </Button>
        </div>
    )
}
