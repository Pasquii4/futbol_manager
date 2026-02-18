const API_BASE_URL = 'http://localhost:8080/api';

export interface Team {
    id: number;
    teamId: string;
    name: string;
    stadium: string;
    budget: number;
    overallRating: number;
    players: Player[];
}

export interface Player {
    id: number;
    playerId: string;
    name: string;
    position: string;
    age: number;
    overall: number;
    potential: number;
    goalsScored: number;
    assists: number;
    yellowCards: number;
    redCards: number;
    matchesPlayed: number;
    teamId?: number;
}

export interface Match {
    id: number;
    matchday: number;
    homeTeamId: number;
    homeTeamName: string;
    awayTeamId: number;
    awayTeamName: string;
    homeGoals: number;
    awayGoals: number;
    played: boolean;
    matchDate: string;
}

export interface StandingsItem {
    position: number;
    teamId: number;
    teamName: string;
    played: number;
    won: number;
    drawn: number;
    lost: number;
    goalsFor: number;
    goalsAgainst: number;
    goalDifference: number;
    points: number;
}

export const api = {
    getTeams: async (): Promise<Team[]> => {
        const res = await fetch(`${API_BASE_URL}/teams`, { cache: 'no-store' });
        if (!res.ok) throw new Error('Failed to fetch teams');
        return res.json();
    },

    getTeam: async (teamId: string): Promise<Team> => {
        const res = await fetch(`${API_BASE_URL}/teams/${teamId}`, { cache: 'no-store' });
        if (!res.ok) throw new Error('Failed to fetch team');
        return res.json();
    },

    getPlayer: async (playerId: string): Promise<Player> => {
        const res = await fetch(`${API_BASE_URL}/players/${playerId}`, { cache: 'no-store' });
        if (!res.ok) throw new Error('Failed to fetch player');
        return res.json();
    },

    getMatches: async (matchday?: number): Promise<Match[]> => {
        const url = matchday
            ? `${API_BASE_URL}/matches?matchday=${matchday}`
            : `${API_BASE_URL}/matches`;
        const res = await fetch(url, { cache: 'no-store' });
        if (!res.ok) throw new Error('Failed to fetch matches');
        return res.json();
    },

    getStandings: async (): Promise<StandingsItem[]> => {
        const res = await fetch(`${API_BASE_URL}/standings`, { cache: 'no-store' });
        if (!res.ok) throw new Error('Failed to fetch standings');
        return res.json();
    },

    simulateMatchday: async (): Promise<string> => {
        const res = await fetch(`${API_BASE_URL}/simulate/matchday`, {
            method: 'POST',
            cache: 'no-store'
        });
        if (!res.ok) throw new Error('Failed to simulate matchday');
        return res.text();
    },

    simulateSeason: async (): Promise<string> => {
        const res = await fetch(`${API_BASE_URL}/simulate/season`, {
            method: 'POST',
            cache: 'no-store'
        });
        if (!res.ok) throw new Error('Failed to simulate season');
        return res.text();
    }
};
