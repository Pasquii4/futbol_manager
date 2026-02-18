import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { ModeToggle } from "@/components/mode-toggle"

export default function SettingsPage() {
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
                            <h3 className="font-medium">Reiniciar Simulación</h3>
                            <p className="text-sm text-neutral-500">Borrar todos los datos y empezar de cero</p>
                        </div>
                        <Button variant="destructive">Resetear Datos</Button>
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}
