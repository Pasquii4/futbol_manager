
import axios from 'axios';

// Interfaces
export interface LeagueStatus {
    currentMatchday: number;
    totalMatchdays: number;
    seasonYear: number;
    isStarted: boolean;
    isFinished: boolean;
    managedTeamId: number | null;
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

export interface MatchEvent {
    id: number;
    playerId: number;
    playerName: string;
    type: 'GOAL' | 'ASSIST' | 'YELLOW_CARD' | 'RED_CARD';
    minute: number;
    teamId: number;
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
    events: MatchEvent[];
}

export interface Player {
    id: number;
    playerId: string;
    name: string;
    position: string;
    age: number;
    overall: number;
    potential: number;
    marketValue: number;
    teamId: string;
    teamName?: string;
    goalsScored?: number;
    assists?: number;
    yellowCards?: number;
    redCards?: number;
    matchesPlayed?: number;
}

export interface Team {
    id: number;
    teamId: string;
    name: string;
    stadium: string;
    budget: number;
    overallRating: number;
    formation?: string;
    mentality?: string;
    players?: Player[];
}

// API Instance
const api = axios.create({
    baseURL: 'http://127.0.0.1:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Stats Interfaces
export interface PlayerStatsDTO {
    id: number;
    playerId: number;
    name: string;
    position: string;
    teamName: string;
    goalsScored: number;
    assists: number;
    matchesPlayed: number;
    overall: number;
}

// ============ League ============

export const getLeagueStatus = async (): Promise<LeagueStatus> => {
    const response = await api.get('/league/status');
    return response.data;
};

export const resetLeague = async (): Promise<void> => {
    await api.post('/league/reset');
};

export const selectTeam = async (teamId: number): Promise<void> => {
    await api.post(`/league/select-team/${teamId}`);
};

// ============ Standings ============

export const getStandings = async (): Promise<StandingsItem[]> => {
    const response = await api.get('/standings');
    return response.data;
};

// ============ Matches ============

export const getMatches = async (matchday?: number): Promise<Match[]> => {
    const url = matchday ? `/matches?matchday=${matchday}` : '/matches';
    const response = await api.get(url);
    return response.data;
};

export const getMatch = async (id: number): Promise<Match> => {
    const response = await api.get(`/matches/${id}`);
    return response.data;
};

// ============ Teams ============

export const getTeams = async (): Promise<Team[]> => {
    const response = await api.get('/teams');
    return response.data;
};

export const getTeam = async (id: string): Promise<Team> => {
    const response = await api.get(`/teams/${id}`);
    return response.data;
};

export const updateTactics = async (teamId: string, formation: string, mentality: string): Promise<Team> => {
    const response = await api.put(`/teams/${teamId}/tactics?formation=${formation}&mentality=${mentality}`);
    return response.data;
};

// ============ Simulation ============

export const simulateMatchday = async (): Promise<string> => {
    const response = await api.post('/simulate/matchday');
    return response.data;
};

export const simulateSeason = async (): Promise<string> => {
    const response = await api.post('/simulate/season');
    return response.data;
};

// ============ Stats ============

export const getTopScorers = async (): Promise<PlayerStatsDTO[]> => {
    const response = await api.get('/stats/top-scorers');
    return response.data;
};

export const getTopAssists = async (): Promise<PlayerStatsDTO[]> => {
    const response = await api.get('/stats/top-assists');
    return response.data;
};

export const getTopRated = async (): Promise<PlayerStatsDTO[]> => {
    const response = await api.get('/stats/top-rated');
    return response.data;
};

// ============ Transfers ============

export const buyPlayer = async (playerId: number, teamId: number): Promise<string> => {
    const response = await api.post(`/transfers/buy?playerId=${playerId}&teamId=${teamId}`);
    return response.data;
};

export default api;

