'use client';

import { useEffect, useState } from 'react';
import { getMatches, Match } from '@/lib/api';
import Link from 'next/link';

export default function MatchesPage() {
    const [matches, setMatches] = useState<Match[]>([]);
    const [loading, setLoading] = useState(true);
    const [matchday, setMatchday] = useState<number | null>(1);
    const [maxMatchday, setMaxMatchday] = useState(38);

    useEffect(() => {
        setLoading(true);
        getMatches(matchday || undefined)
            .then(data => {
                setMatches(data);
                if (!matchday && data.length > 0) {
                    const max = Math.max(...data.map(m => m.matchday));
                    setMaxMatchday(max);
                }
            })
            .catch(console.error)
            .finally(() => setLoading(false));
    }, [matchday]);

    return (
        <div className="p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">Matches</h1>
                <div className="flex items-center space-x-2">
                    <label className="text-sm font-medium">Matchday:</label>
                    <select
                        value={matchday || ''}
                        onChange={(e) => setMatchday(Number(e.target.value))}
                        className="border p-2 rounded bg-white dark:bg-gray-800"
                    >
                        {Array.from({ length: 38 }, (_, i) => i + 1).map(d => (
                            <option key={d} value={d}>Jornada {d}</option>
                        ))}
                    </select>
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {matches.map((match) => (
                    <Link key={match.id} href={`/matches/${match.id}`} className="block">
                        <div className={`bg-white dark:bg-gray-800 rounded-xl shadow-sm p-4 border-l-4 ${match.played ? 'border-green-500' : 'border-gray-300'} hover:shadow-md transition-shadow`}>
                            <div className="text-xs text-gray-500 mb-2 uppercase tracking-wider">
                                {match.played ? 'Finished' : new Date(match.matchDate).toLocaleDateString()}
                            </div>
                            <div className="flex justify-between items-center mb-2">
                                <span className="font-semibold text-lg">{match.homeTeamName}</span>
                                <span className="font-bold text-xl">{match.played ? match.homeGoals : '-'}</span>
                            </div>
                            <div className="flex justify-between items-center">
                                <span className="font-semibold text-lg">{match.awayTeamName}</span>
                                <span className="font-bold text-xl">{match.played ? match.awayGoals : '-'}</span>
                            </div>
                        </div>
                    </Link>
                ))}
                {matches.length === 0 && !loading && <p>No matches found.</p>}
            </div>
        </div>
    );
}
