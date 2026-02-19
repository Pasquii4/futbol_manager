'use client';

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ModeToggle } from "@/components/mode-toggle"
import { resetLeague } from "@/lib/api"
import { useState } from "react"
import { useRouter } from "next/navigation"

export default function SettingsPage() {
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    const handleReset = async () => {
        if (!confirm("¿Estás seguro de que quieres borrar todos los datos de la liga? Esta acción no se puede deshacer.")) return;

        setLoading(true);
        try {
            await resetLeague();
            alert("Liga reiniciada correctamente.");
            router.refresh();
            router.push('/select-team');
        } catch (error) {
            console.error(error);
            alert("Error al reiniciar la liga.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-3xl font-bold tracking-tight">Configuración</h1>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Preferencias Generales</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-4 border rounded-lg">
                        <div>
                            <h3 className="font-medium">Modo Oscuro</h3>
                            <p className="text-sm text-neutral-500">Activar apariencia oscura de la interfaz</p>
                        </div>
                        <ModeToggle />
                    </div>

                    <div className="flex items-center justify-between p-4 border rounded-lg">
                        <div>
                            <h3 className="font-medium">Idioma</h3>
                            <p className="text-sm text-neutral-500">Español (España)</p>
                        </div>
                        <Button variant="outline">Cambiar</Button>
                    </div>

                    <div className="flex items-center justify-between p-4 border rounded-lg">
                        <div>
                            <h3 className="font-medium">Crear Nueva Liga</h3>
                            <p className="text-sm text-neutral-500">Reiniciar la simulación y empezar una nueva liga</p>
                        </div>
                        <Button variant="default" onClick={handleReset} disabled={loading}>
                            {loading ? "Creando..." : "Crear Liga"}
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
