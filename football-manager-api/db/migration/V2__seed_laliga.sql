-- V2__seed_laliga.sql
-- Seed data for 20 LaLiga teams
-- Run after V1__initial_schema.sql

-- Create LaLiga league
INSERT INTO leagues (name, country, season_year) VALUES ('LaLiga EA Sports', 'Spain', 2025);

-- Insert 20 LaLiga teams
INSERT INTO teams (team_id, name, stadium, budget, overall_rating) VALUES
('real_madrid', 'Real Madrid', 'Santiago Bernabéu', 200000000, 86),
('barcelona', 'FC Barcelona', 'Spotify Camp Nou', 180000000, 85),
('atletico_madrid', 'Atlético de Madrid', 'Cívitas Metropolitano', 120000000, 82),
('real_sociedad', 'Real Sociedad', 'Reale Arena', 80000000, 80),
('villarreal', 'Villarreal CF', 'Estadio de la Cerámica', 75000000, 79),
('athletic_bilbao', 'Athletic Club', 'San Mamés', 70000000, 79),
('real_betis', 'Real Betis', 'Benito Villamarín', 65000000, 78),
('sevilla', 'Sevilla FC', 'Ramón Sánchez-Pizjuán', 60000000, 77),
('valencia', 'Valencia CF', 'Mestalla', 55000000, 76),
('girona', 'Girona FC', 'Montilivi', 50000000, 78),
('celta_vigo', 'RC Celta de Vigo', 'Abanca-Balaídos', 40000000, 75),
('osasuna', 'CA Osasuna', 'El Sadar', 35000000, 74),
('getafe', 'Getafe CF', 'Coliseum Alfonso Pérez', 30000000, 73),
('mallorca', 'RCD Mallorca', 'Son Moix', 30000000, 73),
('rayo_vallecano', 'Rayo Vallecano', 'Estadio de Vallecas', 25000000, 73),
('las_palmas', 'UD Las Palmas', 'Estadio Gran Canaria', 25000000, 72),
('alaves', 'Deportivo Alavés', 'Mendizorroza', 20000000, 71),
('cadiz', 'Cádiz CF', 'Nuevo Mirandilla', 18000000, 70),
('granada', 'Granada CF', 'Nuevo Los Cármenes', 18000000, 70),
('almeria', 'UD Almería', 'Power Horse Stadium', 20000000, 70);

-- Note: Players are loaded dynamically from laliga_db.json by DataLoadService.
-- For a full PostgreSQL setup, you can export players from the H2 database
-- or modify DataLoadService to also seed players from this script.
