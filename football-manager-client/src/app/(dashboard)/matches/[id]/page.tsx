'use client';

import { useEffect, useState } from 'react';
import { getMatch, Match } from '@/lib/api';
import { useParams } from 'next/navigation';
import Link from 'next/link';
import { ArrowLeft } from 'lucide-react';
import { getMatch, Match } from '@/lib/api';

export default function MatchDetailsPage() {
    const params = useParams();
    const id = Number(params.id);
    const [match, setMatch] = useState<Match | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!id) return;
        getMatch(id)
            .then(setMatch)
            .catch(console.error)
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) return <div className="p-8">Loading...</div>;
    if (!match) return <div className="p-8">Match not found</div>;

    return (
        <div className="p-8 max-w-4xl mx-auto">
            <Link href="/matches" className="flex items-center text-blue-600 mb-6 hover:underline">
                <ArrowLeft size={16} className="mr-2" /> Back to Matches
            </Link>

            <div className="bg-white dark:bg-neutral-900 rounded-xl shadow-lg overflow-hidden border border-neutral-200 dark:border-neutral-800">
                {/* Header */}
                <div className="bg-neutral-50 dark:bg-neutral-950 p-6 text-center border-b border-neutral-200 dark:border-neutral-800">
                    <div className="text-sm text-muted-foreground uppercase tracking-widest mb-2">
                        Jornada {match.matchday} • {match.played ? 'Finalizado' : new Date(match.matchDate).toLocaleDateString()}
                    </div>
                    <div className="flex justify-between items-center px-8">
                        <div className="text-center w-1/3">
                            <h2 className="text-2xl font-bold">{match.homeTeamName}</h2>
                        </div>
                        <div className="text-center w-1/3">
                            {match.played ? (
                                <div className="text-5xl font-black tracking-tighter">
                                    {match.homeGoals} - {match.awayGoals}
                                </div>
                            ) : (
                                <div className="text-4xl font-light text-muted-foreground">VS</div>
                            )}
                        </div>
                        <div className="text-center w-1/3">
                            <h2 className="text-2xl font-bold">{match.awayTeamName}</h2>
                        </div>
                    </div>
                </div>

                {/* Stats / Details Placeholder */}
                <div className="p-8">
                    {match.played ? (
                        <div className="text-center space-y-4">
                            <h3 className="text-lg font-semibold">Detalles del Partido</h3>
                            <p className="text-muted-foreground">Estadísticas detalladas disponibles en Fase 2.</p>

                            <div className="grid grid-cols-2 gap-8 mt-8">
                                <div className="text-right space-y-2">
                                    <h4 className="font-medium mb-2">Goles Local</h4>
                                    {match.events?.filter(e => e.type === 'GOAL' && e.teamId === Number(match.homeTeamId)).map(event => (
                                        <div key={event.id} className="text-sm">
                                            <span className="font-semibold">{event.playerName}</span> <span className="text-neutral-500">({event.minute}')</span>
                                        </div>
                                    ))}
                                    {(!match.events || match.events.filter(e => e.type === 'GOAL' && e.teamId === Number(match.homeTeamId)).length === 0) && (
                                        <p className="text-sm text-neutral-500">-</p>
                                    )}
                                </div>
                                <div className="text-left space-y-2">
                                    <h4 className="font-medium mb-2">Goles Visitante</h4>
                                    {match.events?.filter(e => e.type === 'GOAL' && e.teamId === Number(match.awayTeamId)).map(event => (
                                        <div key={event.id} className="text-sm">
                                            <span className="font-semibold">{event.playerName}</span> <span className="text-neutral-500">({event.minute}')</span>
                                        </div>
                                    ))}
                                    {(!match.events || match.events.filter(e => e.type === 'GOAL' && e.teamId === Number(match.awayTeamId)).length === 0) && (
                                        <p className="text-sm text-neutral-500">-</p>
                                    )}
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className="text-center py-12">
                            <p className="text-lg">Este partido aún no se ha jugado.</p>
                            <button className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50">
                                Simular (Próximamente)
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div >
    );
}
