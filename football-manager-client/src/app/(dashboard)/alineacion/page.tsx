'use client';

import { useEffect, useState } from 'react';
import { getLeagueStatus, getTeam, updateTactics, Team, Player } from '@/lib/api';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Shield, Swords, Users } from 'lucide-react';

// Simplified positions for visual representation
const FORMATIONS: Record<string, { [key: string]: { top: string, left: string } }> = {
    '4-3-3': {
        'GK': { top: '85%', left: '50%' },
        'LB': { top: '70%', left: '20%' },
        'CB1': { top: '75%', left: '40%' },
        'CB2': { top: '75%', left: '60%' },
        'RB': { top: '70%', left: '80%' },
        'CM1': { top: '50%', left: '30%' },
        'CM2': { top: '50%', left: '50%' },
        'CM3': { top: '50%', left: '70%' },
        'LW': { top: '25%', left: '20%' },
        'ST': { top: '20%', left: '50%' },
        'RW': { top: '25%', left: '80%' }
    },
    '4-4-2': {
        'GK': { top: '85%', left: '50%' },
        'LB': { top: '70%', left: '20%' },
        'CB1': { top: '75%', left: '40%' },
        'CB2': { top: '75%', left: '60%' },
        'RB': { top: '70%', left: '80%' },
        'LM': { top: '45%', left: '20%' },
        'CM1': { top: '50%', left: '40%' },
        'CM2': { top: '50%', left: '60%' },
        'RM': { top: '45%', left: '80%' },
        'ST1': { top: '25%', left: '40%' },
        'ST2': { top: '25%', left: '60%' }
    },
    '3-5-2': {
        'GK': { top: '85%', left: '50%' },
        'CB1': { top: '75%', left: '30%' },
        'CB2': { top: '75%', left: '50%' },
        'CB3': { top: '75%', left: '70%' },
        'LWB': { top: '55%', left: '15%' },
        'CDM': { top: '60%', left: '50%' },
        'RWB': { top: '55%', left: '85%' },
        'CM1': { top: '45%', left: '40%' },
        'CM2': { top: '45%', left: '60%' },
        'ST1': { top: '25%', left: '40%' },
        'ST2': { top: '25%', left: '60%' }
    }
};

export default function TacticsPage() {
    const [team, setTeam] = useState<Team | null>(null);
    const [loading, setLoading] = useState(true);
    const [formation, setFormation] = useState("4-3-3");
    const [mentality, setMentality] = useState("Balanced");
    const [saving, setSaving] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const status = await getLeagueStatus();
                if (status && status.managedTeamId) {
                    // Backend ID is number, standard api Team ID is string.
                    // But in our hacked api.ts we changed Team.id to number.
                    // And getLeagueStatus return managedTeamId as number.
                    // However, getTeam expects a string ID (the textual one "real_madrid")?
                    // Wait, getTeam(id: string) calls /teams/{id}. 
                    // TeamController uses findByTeamId(String teamId).
                    // This is a disconnect. I need to find the textual ID from the numeric ID or use numeric ID everywhere.
                    // Let's assume for now I cannot easily get textual ID from numeric ID without fetching all teams.
                    // So I will fetch ALL teams and find mine.

                    // Actually, let's fix api.ts getTeam to accept number or string?
                    // No, Backend expects String teamId for getTeam("/{teamId}"). 
                    // BUT TeamController ALSO has findAll().

                    // Workaround: Fetch all teams, find mine by numeric ID.
                    const response = await fetch('http://localhost:8080/api/teams');
                    const teams: Team[] = await response.json();
                    const myTeam = teams.find(t => t.id === status.managedTeamId);

                    if (myTeam) {
                        setTeam(myTeam);
                        setFormation(myTeam.formation || "4-3-3");
                        setMentality(myTeam.mentality || "Balanced");
                    }
                }
            } catch (e) {
                console.error("Failed to load team tactics", e);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const handleSave = async () => {
        if (!team) return;
        setSaving(true);
        try {
            await updateTactics(team.teamId, formation, mentality);
            alert("Tácticas guardadas correctamente.");
        } catch (e) {
            console.error("Failed to save tactics", e);
            alert("Error al guardar tácticas.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="p-8 text-center">Cargando pizarra...</div>;
    if (!team) return <div className="p-8 text-center">No estás dirigiendo ningún equipo.</div>;

    const currentFormationPoints = FORMATIONS[formation] || FORMATIONS['4-3-3'];
    // Slice players to match formation count (11). 
    // In a real app we would map specific players to positions. 
    // Here we just take top 11 players.
    const startingXI = team.players ? team.players.slice(0, 11) : [];

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Pizarra Táctica</h1>
                    <p className="text-muted-foreground">Gestiona el estilo de juego del {team.name}</p>
                </div>
                <Button onClick={handleSave} disabled={saving} className="bg-green-600 hover:bg-green-700 text-white">
                    {saving ? "Guardando..." : "Guardar Cambios"}
                </Button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 h-[800px] lg:h-[600px]">
                {/* Visual Pitch */}
                <div className="lg:col-span-2 relative bg-green-800 rounded-xl border-4 border-white/20 shadow-inner overflow-hidden flex items-center justify-center">
                    {/* Pitch markings */}
                    <div className="absolute inset-0 opacity-20 pointer-events-none">
                        <div className="absolute top-0 bottom-0 left-1/2 w-0.5 bg-white -translate-x-1/2"></div>
                        <div className="absolute top-1/2 left-1/2 w-32 h-32 border-2 border-white rounded-full -translate-x-1/2 -translate-y-1/2"></div>
                        <div className="absolute top-0 left-1/4 right-1/4 h-16 border-b-2 border-x-2 border-white"></div>
                        <div className="absolute bottom-0 left-1/4 right-1/4 h-16 border-t-2 border-x-2 border-white"></div>
                    </div>

                    {/* Players */}
                    {Object.entries(currentFormationPoints).map(([pos, coords], index) => {
                        const player = startingXI[index];
                        return (
                            <div
                                key={pos}
                                className="absolute flex flex-col items-center justify-center w-24 transform -translate-x-1/2 -translate-y-1/2 transition-all duration-500 ease-in-out"
                                style={{ top: coords.top, left: coords.left }}
                            >
                                <div className="w-10 h-10 rounded-full bg-blue-600 border-2 border-white shadow-lg flex items-center justify-center text-xs font-bold text-white mb-1 z-10 relative group cursor-pointer hover:scale-110 transition-transform">
                                    {player ? player.position : pos}
                                    {/* Tooltip */}
                                    <div className="absolute bottom-full mb-2 hidden group-hover:block bg-black/80 text-white text-xs p-2 rounded whitespace-nowrap z-20">
                                        {player ? `${player.name} (${player.overall})` : "Vacante"}
                                    </div>
                                </div>
                                <div className="px-2 py-0.5 bg-black/40 rounded text-[10px] text-white backdrop-blur-sm truncate max-w-full">
                                    {player ? player.name.split(' ').pop() : pos}
                                </div>
                            </div>
                        );
                    })}
                </div>

                {/* Controls */}
                <Card className="h-full">
                    <CardHeader>
                        <CardTitle>Configuración</CardTitle>
                        <CardDescription>Ajusta tu planteamiento</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-6">
                        <div className="space-y-2">
                            <label className="text-sm font-medium flex items-center gap-2">
                                <Users className="h-4 w-4" /> Formación
                            </label>
                            <Select value={formation} onValueChange={setFormation}>
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="4-3-3">4-3-3 Clásica</SelectItem>
                                    <SelectItem value="4-4-2">4-4-2 Equilibrada</SelectItem>
                                    <SelectItem value="3-5-2">3-5-2 Posesión</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="space-y-2">
                            <label className="text-sm font-medium flex items-center gap-2">
                                <Swords className="h-4 w-4" /> Mentalidad
                            </label>
                            <Select value={mentality} onValueChange={setMentality}>
                                <SelectTrigger>
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="Attacking">Ofensiva</SelectItem>
                                    <SelectItem value="Balanced">Equilibrada</SelectItem>
                                    <SelectItem value="Defensive">Defensiva</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="p-4 bg-muted rounded-lg text-sm space-y-2">
                            <div className="flex items-center gap-2">
                                <Shield className="h-4 w-4 text-blue-500" />
                                <span className="font-semibold">Media Defensiva:</span>
                                <span>{team.players ? Math.round(team.players.filter(p => p.position.includes('B')).reduce((acc, p) => acc + p.overall, 0) / Math.max(1, team.players.filter(p => p.position.includes('B')).length)) : 0}</span>
                            </div>
                            <div className="flex items-center gap-2">
                                <Swords className="h-4 w-4 text-red-500" />
                                <span className="font-semibold">Media Ofensiva:</span>
                                <span>{team.players ? Math.round(team.players.filter(p => p.position.includes('F') || p.position.includes('S') || p.position.includes('W')).reduce((acc, p) => acc + p.overall, 0) / Math.max(1, team.players.filter(p => p.position.includes('F') || p.position.includes('S') || p.position.includes('W')).length)) : 0}</span>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
