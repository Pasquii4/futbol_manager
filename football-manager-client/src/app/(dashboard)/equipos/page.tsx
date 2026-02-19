'use client';

import { useEffect, useState } from 'react';
import { getTeams, Team } from '@/lib/api';
import Link from 'next/link';

export default function TeamsPage() {
    const [teams, setTeams] = useState<Team[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTeams()
            .then(setTeams)
            .catch(console.error)
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <div className="p-8">Loading teams...</div>;

    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold mb-6">Teams</h1>

            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
                {teams.map((team) => (
                    <Link href={`/teams/${team.teamId}`} key={team.id}>
                        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow cursor-pointer border border-transparent hover:border-blue-500">
                            <h2 className="text-xl font-bold mb-2">{team.name}</h2>
                            <p className="text-gray-500 text-sm">Rating: <span className="font-semibold text-gray-700 dark:text-gray-300">{team.overallRating}</span></p>
                            <p className="text-gray-500 text-sm">Budget: {(team.budget / 1000000).toFixed(1)}M€</p>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}
