"use client"

import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import { StandingsItem } from "@/lib/api"

interface LeagueTableProps {
    data: StandingsItem[]
}

export function LeagueTable({ data }: LeagueTableProps) {
    // Data is already sorted by backend
    const sortedEquipos = data;

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
                        <TableRow key={equipo.teamId}>
                            <TableCell className="font-medium">{index + 1}</TableCell>
                            <TableCell className="font-semibold">{equipo.teamName}</TableCell>
                            <TableCell className="text-center">{equipo.played}</TableCell>
                            <TableCell className="text-center">{equipo.won}</TableCell>
                            <TableCell className="text-center">{equipo.drawn}</TableCell>
                            <TableCell className="text-center">{equipo.lost}</TableCell>
                            <TableCell className="text-center">{equipo.goalsFor}</TableCell>
                            <TableCell className="text-center">{equipo.goalsAgainst}</TableCell>
                            <TableCell className="text-center">{equipo.goalDifference}</TableCell>
                            <TableCell className="text-right font-bold text-lg">{equipo.points}</TableCell>
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
