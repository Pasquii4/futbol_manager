'use client';

import { useEffect, useState } from 'react';
import { api, Player } from '@/app/services/api';
import { useParams } from 'next/navigation';

export default function PlayerDetailPage() {
    const { id } = useParams();
    const [player, setPlayer] = useState<Player | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (id) {
            api.getPlayer(id as string)
                .then(setPlayer)
                .catch(console.error)
                .finally(() => setLoading(false));
        }
    }, [id]);

    if (loading) return <div className="p-8">Loading player...</div>;
    if (!player) return <div className="p-8">Player not found</div>;

    return (
        <div className="p-8">
            <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm p-8 max-w-2xl mx-auto border border-gray-100 dark:border-gray-700">
                <div className="text-center mb-8">
                    <div className="inline-block bg-blue-100 text-blue-800 text-xs font-semibold px-2.5 py-0.5 rounded dark:bg-blue-200 dark:text-blue-800 mb-2">
                        {player.position}
                    </div>
                    <h1 className="text-4xl font-bold mb-1">{player.name}</h1>
                    <p className="text-gray-500">Age: {player.age}</p>
                </div>

                <div className="grid grid-cols-2 gap-8 mb-8">
                    <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                        <div className="text-sm text-gray-500 uppercase tracking-wider mb-1">Overall</div>
                        <div className="text-5xl font-bold text-blue-600">{player.overall}</div>
                    </div>
                    <div className="text-center p-4 bg-gray-50 dark:bg-gray-700 rounded-lg">
                        <div className="text-sm text-gray-500 uppercase tracking-wider mb-1">Potential</div>
                        <div className="text-5xl font-bold text-green-600">{player.potential}</div>
                    </div>
                </div>

                <div className="border-t dark:border-gray-700 pt-6">
                    <h2 className="text-xl font-semibold mb-4">Season Stats</h2>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <StatBox label="Apps" value={player.matchesPlayed} />
                        <StatBox label="Goals" value={player.goalsScored} />
                        <StatBox label="Assists" value={player.assists} />
                        <StatBox label="Yellow Cards" value={player.yellowCards} />
                    </div>
                </div>
            </div>
        </div>
    );
}

function StatBox({ label, value }: { label: string, value: number }) {
    return (
        <div className="text-center">
            <div className="text-2xl font-bold">{value}</div>
            <div className="text-xs text-gray-500 uppercase">{label}</div>
        </div>
    );
}
