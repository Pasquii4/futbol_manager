'use client';

import { useEffect, useState } from 'react';
import { getTeams, Team } from '@/lib/api';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useRouter } from 'next/navigation';
import api from '@/lib/api';

export default function SelectTeamPage() {
    const [teams, setTeams] = useState<Team[]>([]);
    const [loading, setLoading] = useState(true);
    const [selectedTeam, setSelectedTeam] = useState<number | null>(null);
    const router = useRouter();

    useEffect(() => {
        getTeams()
            .then(setTeams)
            .finally(() => setLoading(false));
    }, []);

    const handleSelect = async (teamId: number) => {
        try {
            await api.post(`/league/select-team/${teamId}`);
            alert(`¡Has seleccionado ${teams.find(t => t.id === teamId)?.name}!`);
            router.push('/dashboard');
        } catch (error) {
            console.error("Failed to select team", error);
            alert("Error al seleccionar equipo.");
        }
    };

    if (loading) return <div className="p-8 text-center text-muted-foreground">Cargando equipos...</div>;

    return (
        <div className="p-8 max-w-6xl mx-auto">
            <h1 className="text-4xl font-bold mb-2">Selecciona tu Equipo</h1>
            <p className="text-muted-foreground mb-8">Elige el club que gestionarás esta temporada.</p>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {teams.map(team => (
                    <Card
                        key={team.id}
                        className={`cursor-pointer transition-all hover:scale-105 ${selectedTeam === team.id ? 'ring-2 ring-blue-500 bg-blue-50 dark:bg-blue-950' : ''}`}
                        onClick={() => setSelectedTeam(team.id)}
                    >
                        <CardHeader className="pb-2">
                            <CardTitle>{team.name}</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <div className="space-y-1 text-sm">
                                <p><span className="font-semibold">Estadio:</span> {team.stadium}</p>
                                <p><span className="font-semibold">Presupuesto:</span> €{(team.budget / 1000000).toFixed(1)}M</p>
                                <p><span className="font-semibold">Valoración:</span> {team.overallRating}</p>
                            </div>
                            {selectedTeam === team.id && (
                                <Button className="w-full mt-4" onClick={(e) => {
                                    e.stopPropagation();
                                    handleSelect(team.id);
                                }}>
                                    Confirmar Selección
                                </Button>
                            )}
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );
}
