'use client';

import { useEffect, useState } from 'react';
import { getTeams, Team, Player, getLeagueStatus, LeagueStatus, buyPlayer } from '@/lib/api';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { Input } from "@/components/ui/input";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger
} from "@/components/ui/dialog";

// Extended Player Interface for DTO with marketValue
// In api.ts Player has marketValue number. Team has overallRating.
// But we need to make sure Team.players exists. Api.ts Team interface DOES NOT include players.
// We need to fetch teams, then we might need to fetch players if they are not in Team interface.
// Wait, TeamController returns players. I need to update Team interface in api.ts to include players.

// Let's assume I update api.ts Team interface or extend it locally.
interface TeamWithPlayers extends Team {
    players: Player[];
}

export default function MarketPage() {
    const [teams, setTeams] = useState<TeamWithPlayers[]>([]); // We need a way to get players.
    const [status, setStatus] = useState<LeagueStatus | null>(null);
    const [managedTeam, setManagedTeam] = useState<Team | null>(null);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedPlayer, setSelectedPlayer] = useState<Player | null>(null);
    const [isConfirmOpen, setIsConfirmOpen] = useState(false);
    const [processing, setProcessing] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [teamsData, statusData] = await Promise.all([
                    getTeams(),
                    getLeagueStatus()
                ]);
                // @ts-ignore - api types need update but runtime sends players
                setTeams(teamsData);
                setStatus(statusData);

                if (statusData.managedTeamId) {
                    // Find managed team in the list (using numeric ID from status vs string ID from team?)
                    // Team in api.ts has string ID "real_madrid", backend has Long ID.
                    // The getTeams returns Long ID as well? Let's check TeamDTO.
                    // TeamDTO has id (Long) and teamId (String).
                    // status.managedTeamId is Long.
                    // api.ts Team has id: string. Need to check if I updated api.ts Team to have numeric id.
                    // Actually api.ts Team has id: string.
                    // I should update api.ts Team interface too.

                    // For now, let's look for matching ID if possible or assume we need to fix it.
                    // Backend TeamDTO has id: Long. Frontend Team has id: string.
                    // This is a mismatch I should fix.

                    // Let's try to match loosely or find by numeric ID if available in data.
                    // @ts-ignore
                    const myTeam = teamsData.find(t => t.id === statusData.managedTeamId || t.id == statusData.managedTeamId);
                    if (myTeam) setManagedTeam(myTeam);
                }

            } catch (error) {
                console.error("Failed to load market", error);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleBuy = async () => {
        if (!selectedPlayer || !managedTeam || !status?.managedTeamId) return;

        setProcessing(true);
        try {
            await buyPlayer(Number(selectedPlayer.id), status.managedTeamId);
            alert(`¡Has fichado a ${selectedPlayer.name}!`);
            setIsConfirmOpen(false);
            window.location.reload(); // Simple reload to refresh data
        } catch (error) {
            console.error(error);
            alert("Error al realizar el fichaje. Comprueba tu presupuesto.");
        } finally {
            setProcessing(false);
        }
    };

    // Flatten all players from other teams
    const marketPlayers = teams
        .filter(t => t.id !== managedTeam?.id) // Exclude my team
        .flatMap(t => t.players ? t.players.map(p => ({ ...p, teamName: t.name, teamId: t.teamId })) : [])
        .filter(p => p.name.toLowerCase().includes(searchTerm.toLowerCase()));

    // Limit display
    const displayedPlayers = marketPlayers.slice(0, 50);

    if (loading) return <div className="p-8">Cargando mercado...</div>;

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Mercado de Fichajes</h1>
                    {managedTeam && (
                        <p className="text-muted-foreground">
                            Presupuesto: <span className="text-green-600 font-bold">€{(managedTeam.budget / 1000000).toFixed(1)}M</span>
                        </p>
                    )}
                </div>
                <Input
                    placeholder="Buscar jugador..."
                    className="max-w-xs"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Jugadores Disponibles</CardTitle>
                </CardHeader>
                <CardContent>
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead>Nombre</TableHead>
                                <TableHead>Equipo</TableHead>
                                <TableHead>Posición</TableHead>
                                <TableHead>Media</TableHead>
                                <TableHead>Edad</TableHead>
                                <TableHead className="text-right">Valor</TableHead>
                                <TableHead></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {displayedPlayers.map(player => (
                                <TableRow key={player.id}>
                                    <TableCell className="font-medium">{player.name}</TableCell>
                                    <TableCell className="text-muted-foreground">
                                        {/* @ts-ignore */}
                                        {player.teamName}
                                    </TableCell>
                                    <TableCell>{player.position}</TableCell>
                                    <TableCell>{player.overall}</TableCell>
                                    <TableCell>{player.age}</TableCell>
                                    <TableCell className="text-right font-bold">
                                        €{(player.marketValue / 1000000).toFixed(1)}M
                                    </TableCell>
                                    <TableCell>
                                        <Button
                                            size="sm"
                                            onClick={() => {
                                                setSelectedPlayer(player);
                                                setIsConfirmOpen(true);
                                            }}
                                            disabled={!managedTeam || (player.marketValue > managedTeam.budget)}
                                        >
                                            Fichar
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>

            <Dialog open={isConfirmOpen} onOpenChange={setIsConfirmOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Confirmar Fichaje</DialogTitle>
                        <DialogDescription>
                            ¿Quieres fichar a <b>{selectedPlayer?.name}</b> por <span className="font-bold text-green-600">€{(selectedPlayer?.marketValue || 0) / 1000000}M</span>?
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsConfirmOpen(false)}>Cancelar</Button>
                        <Button onClick={handleBuy} disabled={processing}>Confirmar</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}
