'use client';

import { useEffect, useState } from 'react';
import { getTeams, Team } from '@/lib/api';
import Link from 'next/link';

export default function TeamsPage() {
    const [teams, setTeams] = useState<Team[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getTeams()
            .then(setTeams)
            .catch((err) => {
                console.error(err);
                setError('No se pudieron cargar los equipos. Verifica que el backend esté ejecutándose.');
            })
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div className="p-8">
                <h1 className="text-3xl font-bold mb-6">Equipos</h1>
                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                    {[...Array(8)].map((_, i) => (
                        <div key={i} className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 animate-pulse">
                            <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded mb-3 w-3/4"></div>
                            <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded mb-2 w-1/2"></div>
                            <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-1/3"></div>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="p-8">
                <h1 className="text-3xl font-bold mb-6">Equipos</h1>
                <div className="p-6 rounded-xl border border-red-200 bg-red-50 dark:bg-red-900/20 dark:border-red-800">
                    <p className="text-red-600 dark:text-red-300 mb-4">{error}</p>
                    <button
                        onClick={() => window.location.reload()}
                        className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-semibold transition-colors"
                    >
                        Reintentar
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Equipos</h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {teams.map((team) => (
                    <Link href={`/teams/${team.teamId}`} key={team.id}>
                        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-all cursor-pointer border border-transparent hover:border-blue-500 hover:-translate-y-1 duration-200">
                            <h2 className="text-xl font-bold mb-2">{team.name}</h2>
                            <p className="text-gray-500 text-sm">
                                ⭐ Rating: <span className="font-semibold text-gray-700 dark:text-gray-300">{team.overallRating}</span>
                            </p>
                            <p className="text-gray-500 text-sm">
                                💰 Presupuesto: <span className="font-semibold text-green-600">{(team.budget / 1000000).toFixed(1)}M€</span>
                            </p>
                            <p className="text-gray-500 text-sm">
                                🏟️ {team.stadium}
                            </p>
                            {team.players && (
                                <p className="text-gray-400 text-xs mt-2">{team.players.length} jugadores</p>
                            )}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}
