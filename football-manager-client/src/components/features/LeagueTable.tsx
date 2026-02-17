"use client"

import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { EquipoDTO } from "@/lib/types"

interface LeagueTableProps {
    equipos: EquipoDTO[]
}

export function LeagueTable({ equipos }: LeagueTableProps) {
    // Sort teams by points, then goal difference, then goals scored
    const sortedEquipos = [...equipos].sort((a, b) => {
        if (b.puntos !== a.puntos) return b.puntos - a.puntos;
        const diffA = a.golesFavor - a.golesContra;
        const diffB = b.golesFavor - b.golesContra;
        if (diffB !== diffA) return diffB - diffA;
        return b.golesFavor - a.golesFavor;
    });

    return (
        <div className="rounded-md border bg-card">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead className="w-[50px]">Pos</TableHead>
                        <TableHead>Equipo</TableHead>
                        <TableHead className="text-center">PJ</TableHead>
                        <TableHead className="text-center">V</TableHead>
                        <TableHead className="text-center">E</TableHead>
                        <TableHead className="text-center">D</TableHead>
                        <TableHead className="text-center">GF</TableHead>
                        <TableHead className="text-center">GC</TableHead>
                        <TableHead className="text-center">DG</TableHead>
                        <TableHead className="text-right font-bold">Pts</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {sortedEquipos.map((equipo, index) => (
                        <TableRow key={equipo.id}>
                            <TableCell className="font-medium">{index + 1}</TableCell>
                            <TableCell className="font-semibold">{equipo.nombre}</TableCell>
                            <TableCell className="text-center">{equipo.partidosJugados}</TableCell>
                            <TableCell className="text-center">{equipo.victorias}</TableCell>
                            <TableCell className="text-center">{equipo.empates}</TableCell>
                            <TableCell className="text-center">{equipo.derrotas}</TableCell>
                            <TableCell className="text-center">{equipo.golesFavor}</TableCell>
                            <TableCell className="text-center">{equipo.golesContra}</TableCell>
                            <TableCell className="text-center">{equipo.golesFavor - equipo.golesContra}</TableCell>
                            <TableCell className="text-right font-bold text-lg">{equipo.puntos}</TableCell>
                        </TableRow>
                    ))}
                    {sortedEquipos.length === 0 && (
                        <TableRow>
                            <TableCell colSpan={10} className="text-center py-6 text-muted-foreground">
                                No hay equipos en esta liga.
                            </TableCell>
                        </TableRow>
                    )}
                </TableBody>
            </Table>
        </div>
    )
}
