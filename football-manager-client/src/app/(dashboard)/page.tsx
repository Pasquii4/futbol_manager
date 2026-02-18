'use client';

import { useEffect, useState } from 'react';
import { api, Match, StandingsItem } from '@/app/services/api';
import Link from 'next/link';

export default function DashboardPage() {
    const [standings, setStandings] = useState<StandingsItem[]>([]);
    const [nextMatches, setNextMatches] = useState<Match[]>([]);
    const [loading, setLoading] = useState(true);
    const [simulating, setSimulating] = useState(false);

    const fetchData = async () => {
        try {
            setLoading(true);
            const [standingsData, matchesData] = await Promise.all([
                api.getStandings(),
                api.getMatches() // Get all matches to filter next matchday
            ]);

            setStandings(standingsData.slice(0, 5)); // Top 5

            // Find next unplayed matchday
            const unplayed = matchesData.filter(m => !m.played);
            const nextMatchday = unplayed.length > 0 ? unplayed[0].matchday : null;

            if (nextMatchday) {
                setNextMatches(unplayed.filter(m => m.matchday === nextMatchday));
            } else {
                setNextMatches([]);
            }
        } catch (error) {
            console.error(error);
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
            await api.simulateMatchday();
            await fetchData(); // Refresh data
        } catch (error) {
            console.error(error);
            alert('Simulation failed');
        } finally {
            setSimulating(false);
        }
    };

    return (
        <div className="p-8 space-y-8">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold text-gray-800 dark:text-white">LaLiga Manager Dashboard</h1>
                <button
                    onClick={handleSimulate}
                    disabled={loading || simulating || nextMatches.length === 0}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg font-semibold disabled:opacity-50 transition-colors"
                >
                    {simulating ? 'Simulating...' : 'Simulate Next Matchday'}
                </button>
            </div>

            {loading && <div className="text-center text-blue-600 animate-pulse">Refreshing data...</div>}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {/* Standings Preview */}
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-gray-100 dark:border-gray-700">
                    <div className="flex justify-between items-center mb-4">
                        <h2 className="text-xl font-semibold">League Table (Top 5)</h2>
                        <Link href="/standings" className="text-blue-500 hover:text-blue-600 text-sm">View Full</Link>
                    </div>

                    <div className="overflow-x-auto">
                        <table className="w-full text-sm text-left">
                            <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                                <tr>
                                    <th className="px-4 py-2">Pos</th>
                                    <th className="px-4 py-2">Team</th>
                                    <th className="px-4 py-2">P</th>
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
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Next Matchday Preview */}
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 border border-gray-100 dark:border-gray-700">
                    <div className="flex justify-between items-center mb-4">
                        <h2 className="text-xl font-semibold">
                            {nextMatches.length > 0 ? `Next Matchday (${nextMatches[0].matchday})` : 'Season Finished'}
                        </h2>
                        <Link href="/matches" className="text-blue-500 hover:text-blue-600 text-sm">View Calendar</Link>
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
                            <p className="text-center text-xs text-gray-500">And {nextMatches.length - 5} more matches...</p>
                        )}
                        {nextMatches.length === 0 && (
                            <p className="text-gray-500 text-center py-4">No upcoming matches.</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
