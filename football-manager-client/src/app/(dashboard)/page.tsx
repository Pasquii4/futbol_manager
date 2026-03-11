'use client';

import { useEffect, useState } from 'react';
import { getStandings, getMatches, simulateMatchday, StandingsItem, Match } from '@/lib/api';
import Link from 'next/link';

export default function DashboardPage() {
    const [standings, setStandings] = useState<StandingsItem[]>([]);
    const [nextMatches, setNextMatches] = useState<Match[]>([]);
    const [loading, setLoading] = useState(true);
    const [simulating, setSimulating] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);
            const [standingsData, matchesData] = await Promise.all([
                getStandings(),
                getMatches()
            ]);

            setStandings(standingsData.slice(0, 5));

            // Find next unplayed matchday
            const unplayed = matchesData.filter(m => !m.played);
            const nextMatchday = unplayed.length > 0 ? unplayed[0].matchday : null;

            if (nextMatchday) {
                setNextMatches(unplayed.filter(m => m.matchday === nextMatchday));
            } else {
                setNextMatches([]);
            }
        } catch (err) {
            console.error('Failed to fetch dashboard data:', err);
            setError('No se pudieron cargar los datos. Verifica que el backend esté ejecutándose en http://localhost:8080');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleSimulate = async () => {
        setSimulating(true);
        try {
            await simulateMatchday();
            await fetchData();
        } catch (err) {
            console.error('Simulation failed:', err);
            setError('Error al simular la jornada. Inténtalo de nuevo.');
        } finally {
            setSimulating(false);
        }
    };

    if (error) {
        return (
            <div className="p-8 space-y-8">
                <h1 className="text-3xl font-bold text-gray-800 dark:text-white">LaLiga Manager Dashboard</h1>
                <div className="p-6 rounded-xl border border-red-200 bg-red-50 dark:bg-red-900/20 dark:border-red-800">
                    <h3 className="text-lg font-semibold text-red-700 dark:text-red-400 mb-2">Error al cargar datos</h3>
                    <p className="text-red-600 dark:text-red-300 mb-4">{error}</p>
                    <button
                        onClick={fetchData}
                        className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg font-semibold transition-colors"
                    >
                        Reintentar
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="p-8 space-y-8">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold text-gray-800 dark:text-white">LaLiga Manager Dashboard</h1>
                <button
                    onClick={handleSimulate}
                    disabled={loading || simulating || nextMatches.length === 0}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-semibold disabled:opacity-50 transition-colors"
                >
                    {simulating ? 'Simulando...' : 'Simular Próxima Jornada'}
                </button>
            </div>

            {loading && (
                <div className="text-center py-8">
                    <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mb-2"></div>
                    <p className="text-blue-600">Cargando datos...</p>
                </div>
            )}

            {!loading && (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Standings Preview */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-gray-100 dark:border-gray-700">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-semibold">Clasificación (Top 5)</h2>
                            <Link href="/laliga" className="text-blue-500 hover:text-blue-600 text-sm">Ver completa</Link>
                        </div>

                        <div className="overflow-x-auto">
                            <table className="w-full text-sm text-left">
                                <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                                    <tr>
                                        <th className="px-4 py-2">Pos</th>
                                        <th className="px-4 py-2">Equipo</th>
                                        <th className="px-4 py-2">PJ</th>
                                        <th className="px-4 py-2">Pts</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {standings.map((team) => (
                                        <tr key={team.teamId} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                                            <td className="px-4 py-2">{team.position}</td>
                                            <td className="px-4 py-2 font-medium">{team.teamName}</td>
                                            <td className="px-4 py-2">{team.played}</td>
                                            <td className="px-4 py-2 font-bold">{team.points}</td>
                                        </tr>
                                    ))}
                                    {standings.length === 0 && (
                                        <tr>
                                            <td colSpan={4} className="px-4 py-6 text-center text-gray-500">
                                                No hay datos de clasificación
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* Next Matchday Preview */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-gray-100 dark:border-gray-700">
                        <div className="flex justify-between items-center mb-4">
                            <h2 className="text-xl font-semibold">
                                {nextMatches.length > 0 ? `Próxima Jornada (${nextMatches[0].matchday})` : 'Temporada Finalizada'}
                            </h2>
                            <Link href="/matches" className="text-blue-500 hover:text-blue-600 text-sm">Ver Calendario</Link>
                        </div>

                        <div className="space-y-3">
                            {nextMatches.slice(0, 5).map((match) => (
                                <div key={match.id} className="flex justify-between items-center p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
                                    <span className="w-1/3 text-right font-medium">{match.homeTeamName}</span>
                                    <span className="text-gray-400 text-xs px-2">vs</span>
                                    <span className="w-1/3 text-left font-medium">{match.awayTeamName}</span>
                                </div>
                            ))}
                            {nextMatches.length > 5 && (
                                <p className="text-center text-xs text-gray-500">Y {nextMatches.length - 5} partidos más...</p>
                            )}
                            {nextMatches.length === 0 && (
                                <p className="text-gray-500 text-center py-4">No hay partidos próximos.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
