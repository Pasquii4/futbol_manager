"use client"

import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { JugadorDTO } from "@/lib/types"
import { Badge } from "@/components/ui/badge"

interface PlayerListProps {
    jugadores: JugadorDTO[]
}

export function PlayerList({ jugadores }: PlayerListProps) {
    return (
        <div className="rounded-md border bg-card">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead className="w-[50px]">Dorsal</TableHead>
                        <TableHead>Nombre</TableHead>
                        <TableHead>Posición</TableHead>
                        <TableHead className="text-center">Calidad</TableHead>
                        <TableHead className="text-center">Forma</TableHead>
                        <TableHead className="text-center">Goles</TableHead>
                        <TableHead className="text-center">Estado</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {jugadores.map((jugador) => (
                        <TableRow key={jugador.id}>
                            <TableCell className="font-medium">{jugador.dorsal}</TableCell>
                            <TableCell>
                                <div className="flex flex-col">
                                    <span className="font-medium">{jugador.nombre} {jugador.apellido}</span>
                                    <span className="text-xs text-muted-foreground">{jugador.nacionalidad}</span>
                                </div>
                            </TableCell>
                            <TableCell>
                                <Badge variant="outline">{jugador.posicion}</Badge>
                            </TableCell>
                            <TableCell className="text-center font-bold">{jugador.calidad}</TableCell>
                            <TableCell className="text-center">{jugador.forma ? jugador.forma.toFixed(1) : '-'}</TableCell>
                            <TableCell className="text-center">{jugador.estadisticas?.goles || 0}</TableCell>
                            <TableCell className="text-center">
                                {jugador.lesionado ? (
                                    <Badge variant="destructive">Lesionado</Badge>
                                ) : (
                                    <Badge variant="secondary" className="bg-green-100 text-green-800 hover:bg-green-100">Disponible</Badge>
                                )}
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    )
}
