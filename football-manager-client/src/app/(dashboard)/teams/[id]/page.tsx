'use client';

import { useEffect, useState } from 'react';
import { api, Team } from '@/app/services/api';
import { useParams } from 'next/navigation';

export default function TeamDetailPage() {
    const { id } = useParams();
    const [team, setTeam] = useState<Team | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (id) {
            api.getTeam(id as string)
                .then(setTeam)
                .catch(console.error)
                .finally(() => setLoading(false));
        }
    }, [id]);

    if (loading) return <div className="p-8">Loading team...</div>;
    if (!team) return <div className="p-8">Team not found</div>;

    return (
        <div className="p-8 space-y-8">
            {/* Team Header */}
            <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-8 border border-gray-100 dark:border-gray-700">
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                    <div>
                        <h1 className="text-4xl font-bold mb-2">{team.name}</h1>
                        <div className="flex gap-4 text-gray-500">
                            <span>{team.stadium}</span>
                            <span>•</span>
                            <span>Budget: {(team.budget / 1000000).toFixed(1)}M€</span>
                        </div>
                    </div>
                    <div className="text-right">
                        <div className="text-sm text-gray-500 uppercase tracking-wider">Overall Rating</div>
                        <div className="text-4xl font-bold text-blue-600">{team.overallRating}</div>
                    </div>
                </div>
            </div>

            {/* Squad List */}
            <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm overflow-hidden border border-gray-100 dark:border-gray-700">
                <div className="p-6 border-b dark:border-gray-700">
                    <h2 className="text-xl font-semibold">Squad</h2>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                            <tr>
                                <th className="px-6 py-3">Pos</th>
                                <th className="px-6 py-3">Name</th>
                                <th className="px-6 py-3">Age</th>
                                <th className="px-6 py-3 text-center">OVR</th>
                                <th className="px-6 py-3 text-center">POT</th>
                                <th className="px-6 py-3 text-center">Apps</th>
                                <th className="px-6 py-3 text-center">Goals</th>
                                <th className="px-6 py-3 text-center">Assists</th>
                            </tr>
                        </thead>
                        <tbody>
                            {team.players.map((player) => (
                                <tr key={player.playerId} className="border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600">
                                    <td className="px-6 py-4 font-medium text-gray-500">{player.position}</td>
                                    <td className="px-6 py-4 font-medium text-gray-900 dark:text-white">{player.name}</td>
                                    <td className="px-6 py-4">{player.age}</td>
                                    <td className="px-6 py-4 text-center font-bold bg-gray-50 dark:bg-gray-900">{player.overall}</td>
                                    <td className="px-6 py-4 text-center text-gray-500">{player.potential}</td>
                                    <td className="px-6 py-4 text-center">{player.matchesPlayed}</td>
                                    <td className="px-6 py-4 text-center font-semibold text-green-600">{player.goalsScored}</td>
                                    <td className="px-6 py-4 text-center">{player.assists}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}
