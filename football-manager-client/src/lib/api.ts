
import axios from 'axios';

// Interfaces
export interface LeagueStatus {
    currentMatchday: number;
    totalMatchdays: number;
    seasonYear: number;
    isStarted: boolean;
    isFinished: boolean;
}

export interface StandingsItem {
    position: number;
    teamId: string; // Changed to string to match backend ID strings like "real_madrid"
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
}

export interface Match {
    id: number;
    matchday: number;
    homeTeamId: string;
    homeTeamName: string;
    awayTeamId: string;
    awayTeamName: string;
    homeGoals: number;
    awayGoals: number;
    played: boolean;
    matchDate: string;
    events: MatchEvent[];
}

export interface Player {
    id: string;
    playerId: string;
    name: string;
    position: string;
    age: number;
    overall: number;
    potential: number;
    marketValue: number;
    teamId: string;
}

export interface Team {
    id: string; // e.g. "real_madrid"
    name: string;
    stadium: string;
    budget: number;
    overallRating: number;
}

// API Instance
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Methods
export const getLeagueStatus = async (): Promise<LeagueStatus> => {
    const response = await api.get('/league/status');
    return response.data;
};

export const getStandings = async (): Promise<StandingsItem[]> => {
    const response = await api.get('/standings');
    return response.data;
};

export const getMatches = async (matchday?: number): Promise<Match[]> => {
    const url = matchday ? `/matches?matchday=${matchday}` : '/matches';
    const response = await api.get(url);
    return response.data;
};

export const getMatch = async (id: number): Promise<Match> => {
    const response = await api.get(`/matches/${id}`);
    return response.data;
};

export const getTeams = async (): Promise<Team[]> => {
    const response = await api.get('/teams');
    return response.data;
};

export const getTeam = async (id: string): Promise<Team> => {
    const response = await api.get(`/teams/${id}`);
    return response.data;
};

export default api;
