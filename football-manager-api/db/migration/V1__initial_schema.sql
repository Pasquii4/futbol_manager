-- V1__initial_schema.sql
-- Initial database schema for LaLiga Manager
-- Compatible with PostgreSQL 15+

-- League table
CREATE TABLE IF NOT EXISTS leagues (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    season_year INTEGER,
    managed_team_id BIGINT
);

-- Teams table
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    team_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    stadium VARCHAR(100),
    budget BIGINT DEFAULT 0,
    overall_rating INTEGER DEFAULT 70,
    formation VARCHAR(20) DEFAULT '4-3-3',
    mentality VARCHAR(20) DEFAULT 'Balanced'
);

-- Players table
CREATE TABLE IF NOT EXISTS players (
    id BIGSERIAL PRIMARY KEY,
    player_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(10),
    age INTEGER,
    overall INTEGER DEFAULT 70,
    potential INTEGER DEFAULT 75,
    market_value BIGINT DEFAULT 0,
    team_id BIGINT REFERENCES teams(id) ON DELETE SET NULL,
    goals_scored INTEGER DEFAULT 0,
    assists INTEGER DEFAULT 0,
    yellow_cards INTEGER DEFAULT 0,
    red_cards INTEGER DEFAULT 0,
    matches_played INTEGER DEFAULT 0,
    injured BOOLEAN DEFAULT FALSE,
    injury_duration INTEGER DEFAULT 0,
    form DOUBLE PRECISION DEFAULT 7.0
);

-- Matches table
CREATE TABLE IF NOT EXISTS matches (
    id BIGSERIAL PRIMARY KEY,
    matchday INTEGER NOT NULL,
    home_team_id BIGINT REFERENCES teams(id),
    away_team_id BIGINT REFERENCES teams(id),
    home_goals INTEGER DEFAULT 0,
    away_goals INTEGER DEFAULT 0,
    is_played BOOLEAN DEFAULT FALSE,
    match_date DATE,
    possession INTEGER,
    home_shots_on_target INTEGER,
    away_shots_on_target INTEGER
);

-- Match Events table
CREATE TABLE IF NOT EXISTS match_events (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT REFERENCES matches(id) ON DELETE CASCADE,
    player_id BIGINT REFERENCES players(id),
    type VARCHAR(20) NOT NULL,
    minute INTEGER
);

-- Seasons (history) table
CREATE TABLE IF NOT EXISTS seasons (
    id BIGSERIAL PRIMARY KEY,
    year INTEGER NOT NULL,
    champion VARCHAR(100),
    top_scorer VARCHAR(100),
    top_scorer_goals INTEGER,
    standings_snapshot TEXT
);

-- Users table (for authentication)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_players_team ON players(team_id);
CREATE INDEX IF NOT EXISTS idx_matches_matchday ON matches(matchday);
CREATE INDEX IF NOT EXISTS idx_matches_played ON matches(is_played);
CREATE INDEX IF NOT EXISTS idx_match_events_match ON match_events(match_id);
CREATE INDEX IF NOT EXISTS idx_players_goals ON players(goals_scored DESC);
