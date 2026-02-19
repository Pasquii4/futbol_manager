'use client';

import { useEffect, useState } from 'react';
import { getStandings, StandingsItem } from '@/lib/api';

export default function StandingsPage() {
    const [standings, setStandings] = useState<StandingsItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getStandings()
            .then(setStandings)
            .catch(console.error)
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="p-8">Loading standings...</div>;

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">League Standings</h1>

            <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm overflow-hidden">
                <table className="w-full text-sm text-left">
                    <thead className="text-xs text-gray-700 uppercase bg-gray-100 dark:bg-gray-700 dark:text-gray-400">
                        <tr>
                            <th className="px-6 py-3">Pos</th>
                            <th className="px-6 py-3">Team</th>
                            <th className="px-6 py-3">Played</th>
                            <th className="px-6 py-3">Won</th>
                            <th className="px-6 py-3">Drawn</th>
                            <th className="px-6 py-3">Lost</th>
                            <th className="px-6 py-3">GF</th>
                            <th className="px-6 py-3">GA</th>
                            <th className="px-6 py-3">GD</th>
                            <th className="px-6 py-3">Points</th>
                        </tr>
                    </thead>
                    <tbody>
                        {standings.map((team) => (
                            <tr key={team.teamId} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                                <td className="px-6 py-4">{team.position}</td>
                                <td className="px-6 py-4 font-medium text-gray-900 dark:text-white">{team.teamName}</td>
                                <td className="px-6 py-4">{team.played}</td>
                                <td className="px-6 py-4">{team.won}</td>
                                <td className="px-6 py-4">{team.drawn}</td>
                                <td className="px-6 py-4">{team.lost}</td>
                                <td className="px-6 py-4">{team.goalsFor}</td>
                                <td className="px-6 py-4">{team.goalsAgainst}</td>
                                <td className="px-6 py-4">{team.goalDifference}</td>
                                <td className="px-6 py-4 font-bold text-lg">{team.points}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
