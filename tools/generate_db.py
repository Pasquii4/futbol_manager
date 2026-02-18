import json
import random
import os
from datetime import datetime, timedelta

# Configuration
OUTPUT_FILE = r'football-manager-api/src/main/resources/data/laliga_db.json'
SEASON_YEAR = 2025
START_DATE = datetime(2025, 8, 15)  # Season start

# 1. TEAMS DEFINITION
TEAMS = [
    {"id": "real_madrid", "name": "Real Madrid CF", "shortName": "Real Madrid", "stadium": "Santiago Bernabéu", "city": "Madrid", "budget": 750000000, "overallRating": 88, "primaryColor": "#FFFFFF", "secondaryColor": "#FFD700"},
    {"id": "fc_barcelona", "name": "FC Barcelona", "shortName": "FC Barcelona", "stadium": "Spotify Camp Nou", "city": "Barcelona", "budget": 600000000, "overallRating": 86, "primaryColor": "#004D98", "secondaryColor": "#A50044"},
    {"id": "atletico_madrid", "name": "Atlético de Madrid", "shortName": "Atleti", "stadium": "Cívitas Metropolitano", "city": "Madrid", "budget": 450000000, "overallRating": 84, "primaryColor": "#CB3524", "secondaryColor": "#FFFFFF"},
    {"id": "athletic_club", "name": "Athletic Club", "shortName": "Athletic", "stadium": "San Mamés", "city": "Bilbao", "budget": 120000000, "overallRating": 82, "primaryColor": "#FF0000", "secondaryColor": "#FFFFFF"},
    {"id": "real_sociedad", "name": "Real Sociedad", "shortName": "Real Sociedad", "stadium": "Reale Arena", "city": "San Sebastián", "budget": 140000000, "overallRating": 81, "primaryColor": "#0067B1", "secondaryColor": "#FFFFFF"},
    {"id": "villarreal_cf", "name": "Villarreal CF", "shortName": "Villarreal", "stadium": "La Cerámica", "city": "Villarreal", "budget": 110000000, "overallRating": 80, "primaryColor": "#F5E20F", "secondaryColor": "#005187"},
    {"id": "real_betis", "name": "Real Betis", "shortName": "Betis", "stadium": "Benito Villamarín", "city": "Sevilla", "budget": 100000000, "overallRating": 80, "primaryColor": "#0BB363", "secondaryColor": "#FFFFFF"},
    {"id": "sevilla_fc", "name": "Sevilla FC", "shortName": "Sevilla", "stadium": "Ramón Sánchez-Pizjuán", "city": "Sevilla", "budget": 130000000, "overallRating": 79, "primaryColor": "#FFFFFF", "secondaryColor": "#D4001F"},
    {"id": "girona_fc", "name": "Girona FC", "shortName": "Girona", "stadium": "Montilivi", "city": "Girona", "budget": 90000000, "overallRating": 78, "primaryColor": "#D3122E", "secondaryColor": "#FFFFFF"},
    {"id": "valencia_cf", "name": "Valencia CF", "shortName": "Valencia", "stadium": "Mestalla", "city": "Valencia", "budget": 80000000, "overallRating": 77, "primaryColor": "#FFFFFF", "secondaryColor": "#000000"},
    {"id": "rc_celta", "name": "RC Celta", "shortName": "Celta", "stadium": "Abanca Balaídos", "city": "Vigo", "budget": 70000000, "overallRating": 76, "primaryColor": "#8AC3EE", "secondaryColor": "#FFFFFF"},
    {"id": "ca_osasuna", "name": "CA Osasuna", "shortName": "Osasuna", "stadium": "El Sadar", "city": "Pamplona", "budget": 60000000, "overallRating": 76, "primaryColor": "#DA291C", "secondaryColor": "#002A5C"},
    {"id": "rcd_mallorca", "name": "RCD Mallorca", "shortName": "Mallorca", "stadium": "Son Moix", "city": "Palma", "budget": 55000000, "overallRating": 75, "primaryColor": "#E20612", "secondaryColor": "#000000"},
    {"id": "rayo_vallecano", "name": "Rayo Vallecano", "shortName": "Rayo", "stadium": "Vallecas", "city": "Madrid", "budget": 45000000, "overallRating": 75, "primaryColor": "#FFFFFF", "secondaryColor": "#CE1126"},
    {"id": "getafe_cf", "name": "Getafe CF", "shortName": "Getafe", "stadium": "Coliseum", "city": "Getafe", "budget": 50000000, "overallRating": 75, "primaryColor": "#0053A0", "secondaryColor": "#FFFFFF"},
    {"id": "deportivo_alaves", "name": "Deportivo Alavés", "shortName": "Alavés", "stadium": "Mendizorrotza", "city": "Vitoria", "budget": 40000000, "overallRating": 74, "primaryColor": "#005695", "secondaryColor": "#FFFFFF"},
    {"id": "ud_las_palmas", "name": "UD Las Palmas", "shortName": "Las Palmas", "stadium": "Gran Canaria", "city": "Las Palmas", "budget": 42000000, "overallRating": 74, "primaryColor": "#FCDD09", "secondaryColor": "#005DAA"},
    {"id": "cd_leganes", "name": "CD Leganés", "shortName": "Leganés", "stadium": "Butarque", "city": "Leganés", "budget": 38000000, "overallRating": 73, "primaryColor": "#005DAA", "secondaryColor": "#FFFFFF"},
    {"id": "rcd_espanyol", "name": "RCD Espanyol", "shortName": "Espanyol", "stadium": "Stage Front Stadium", "city": "Barcelona", "budget": 45000000, "overallRating": 74, "primaryColor": "#007FC8", "secondaryColor": "#FFFFFF"},
    {"id": "real_valladolid", "name": "Real Valladolid", "shortName": "Valladolid", "stadium": "José Zorrilla", "city": "Valladolid", "budget": 35000000, "overallRating": 73, "primaryColor": "#5B145E", "secondaryColor": "#FFFFFF"}
]

# 2. REAL PLAYERS DATA (Subset)
REAL_PLAYERS = {
    "real_madrid": [
        {"name": "Thibaut Courtois", "position": "GK", "overall": 90, "age": 32},
        {"name": "Dani Carvajal", "position": "RB", "overall": 86, "age": 32},
        {"name": "Eder Militão", "position": "CB", "overall": 87, "age": 26},
        {"name": "Antonio Rüdiger", "position": "CB", "overall": 88, "age": 31},
        {"name": "Ferland Mendy", "position": "LB", "overall": 84, "age": 29},
        {"name": "Aurélien Tchouaméni", "position": "CDM", "overall": 86, "age": 24},
        {"name": "Federico Valverde", "position": "CM", "overall": 89, "age": 26},
        {"name": "Jude Bellingham", "position": "CAM", "overall": 91, "age": 21},
        {"name": "Vinícius Júnior", "position": "LW", "overall": 92, "age": 24},
        {"name": "Kylian Mbappé", "position": "ST", "overall": 92, "age": 25},
        {"name": "Rodrygo Goes", "position": "RW", "overall": 87, "age": 23},
        {"name": "Luka Modrić", "position": "CM", "overall": 85, "age": 39},
        {"name": "Eduardo Camavinga", "position": "CDM", "overall": 87, "age": 22},
        {"name": "Brahim Díaz", "position": "CAM", "overall": 84, "age": 25},
        {"name": "Arda Güler", "position": "rw", "overall": 80, "age": 19},
        {"name": "Endrick", "position": "ST", "overall": 80, "age": 18}
    ],
    "fc_barcelona": [
        {"name": "Marc-André ter Stegen", "position": "GK", "overall": 89, "age": 32},
        {"name": "Jules Koundé", "position": "RB", "overall": 86, "age": 26},
        {"name": "Ronald Araújo", "position": "CB", "overall": 87, "age": 25},
        {"name": "Pau Cubarsí", "position": "CB", "overall": 82, "age": 17},
        {"name": "Alejandro Balde", "position": "LB", "overall": 83, "age": 21},
        {"name": "Marc Casadó", "position": "CDM", "overall": 79, "age": 21},
        {"name": "Pedri", "position": "CM", "overall": 88, "age": 22},
        {"name": "Gavi", "position": "CM", "overall": 85, "age": 20},
        {"name": "Lamine Yamal", "position": "RW", "overall": 89, "age": 17},
        {"name": "Robert Lewandowski", "position": "ST", "overall": 88, "age": 36},
        {"name": "Raphinha", "position": "LW", "overall": 86, "age": 27},
        {"name": "Dani Olmo", "position": "CAM", "overall": 85, "age": 26},
        {"name": "Frenkie de Jong", "position": "CM", "overall": 87, "age": 27},
        {"name": "Fermín López", "position": "CAM", "overall": 81, "age": 21},
        {"name": "Ferran Torres", "position": "LW", "overall": 80, "age": 24}
    ],
    "atletico_madrid": [
        {"name": "Jan Oblak", "position": "GK", "overall": 88, "age": 31},
        {"name": "Antoine Griezmann", "position": "ST", "overall": 88, "age": 33},
        {"name": "Julian Alvarez", "position": "ST", "overall": 86, "age": 24},
        {"name": "Koke", "position": "CM", "overall": 83, "age": 32},
        {"name": "Rodrigo De Paul", "position": "CM", "overall": 84, "age": 30},
        {"name": "Marcos Llorente", "position": "RM", "overall": 83, "age": 29},
        {"name": "Robin Le Normand", "position": "CB", "overall": 83, "age": 27},
        {"name": "Jose Maria Gimenez", "position": "CB", "overall": 84, "age": 29},
        {"name": "Samuel Lino", "position": "LWB", "overall": 81, "age": 24}
    ],
    "athletic_club": [
        {"name": "Nico Williams", "position": "LW", "overall": 85, "age": 22},
        {"name": "Inaki Williams", "position": "RW", "overall": 83, "age": 30},
        {"name": "Oihan Sancet", "position": "CAM", "overall": 82, "age": 24},
        {"name": "Unai Simon", "position": "GK", "overall": 84, "age": 27},
        {"name": "Dani Vivian", "position": "CB", "overall": 81, "age": 25}
    ],
    "real_sociedad": [
        {"name": "Martin Zubimendi", "position": "CDM", "overall": 84, "age": 25},
        {"name": "Takefusa Kubo", "position": "RW", "overall": 83, "age": 23},
        {"name": "Mikel Oyarzabal", "position": "LW", "overall": 84, "age": 27},
        {"name": "Alex Remiro", "position": "GK", "overall": 83, "age": 29}
    ],
     "villarreal_cf": [
        {"name": "Gerard Moreno", "position": "ST", "overall": 83, "age": 32},
        {"name": "Alex Baena", "position": "CAM", "overall": 82, "age": 23},
        {"name": "Yeremy Pino", "position": "LW", "overall": 80, "age": 21}
    ]
}

# 3. HELPER FUNCTIONS

FIRST_NAMES = ["Jose", "Juan", "Carlos", "Luis", "David", "Miguel", "Javier", "Manuel", "Antonio", "Pablo", "Alejandro", "Daniel", "Adrian", "Alvaro", "Diego", "Lucas", "Mario", "Sergio", "Marcos"]
LAST_NAMES = ["Garcia", "Rodriguez", "Gonzalez", "Fernandez", "Lopez", "Martinez", "Sanchez", "Perez", "Gomez", "Martin", "Jimenez", "Ruiz", "Hernandez", "Diaz", "Moreno", "Muñoz"]

POSITIONS = {
    "GK": (1, 3),
    "DEF": (6, 8),
    "MID": (6, 8),
    "FWD": (4, 6)
}

SPECIFIC_POSITIONS = {
    "GK": ["GK"],
    "DEF": ["CB", "LB", "RB", "LWB", "RWB"],
    "MID": ["CDM", "CM", "CAM", "LM", "RM"],
    "FWD": ["ST", "CF", "LW", "RW"]
}

def generate_random_name():
    return f"{random.choice(FIRST_NAMES)} {random.choice(LAST_NAMES)}"

def generate_id(name):
    return name.lower().replace(" ", "_").replace("ñ", "n").replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") + "_" + str(random.randint(100, 999))

def generate_player(position_group, team_rating, forced_name=None, forced_pos=None, forced_overall=None, forced_age=None, team_id=None):
    
    pos = forced_pos if forced_pos else random.choice(SPECIFIC_POSITIONS[position_group])
    
    # Rating logic: Team rating +/- variance
    base_rating = team_rating
    
    if forced_overall:
        overall = forced_overall
    else:
        # Star player chance
        if random.random() < 0.1:
            overall = base_rating + random.randint(3, 6)
        else:
            overall = base_rating + random.randint(-5, 3)
            
    # Cap stats
    overall = max(60, min(94, overall))
    potential = overall + random.randint(0, 5)
    if potential > 95: potential = 95
    
    age = forced_age if forced_age else random.randint(17, 36)
    
    name = forced_name if forced_name else generate_random_name()
    p_id = generate_id(name)
    
    # Minimal stats generation (simplified for JSON)
    # In a real app, you'd calculate detailed attributes based on position and overall
    
    player = {
        "id": p_id,
        "playerId": p_id,
        "teamId": team_id,
        "name": name,
        "position": pos,
        "age": age,
        "overall": overall,
        "potential": potential,
        "marketValue": overall * 500000, # Simplified
        "seasonStats": {
            "matchesPlayed": 0,
            "goals": 0,
            "assists": 0,
            "yellowCards": 0,
            "redCards": 0,
            "averageRating": 0.0
        }
    }
    return player

def generate_schedule(team_ids):
    # Round robin
    schedule = []
    
    teams = team_ids[:]
    if len(teams) % 2 != 0:
        teams.append(None) # Bye week if odd
        
    n = len(teams)
    rounds = n - 1
    
    start_date = START_DATE
    
    match_id = 1
    
    # Single round (First half)
    matches_first_half = []
    
    for r in range(rounds):
        round_matches = []
        date = start_date + timedelta(days=r*7) # Weekly
        
        for i in range(n // 2):
            t1 = teams[i]
            t2 = teams[n - 1 - i]
            
            if t1 is not None and t2 is not None:
                # Decide home/away - alternate
                if r % 2 == 0:
                    home, away = t1, t2
                else:
                    home, away = t2, t1
                    
                match = {
                    "id": match_id,
                    "matchday": r + 1,
                    "homeTeamId": home,
                    "awayTeamId": away,
                    "date": date.strftime("%Y-%m-%dT%H:%M:%S"),
                    "played": False,
                    "homeGoals": 0,
                    "awayGoals": 0
                }
                round_matches.append(match)
                match_id += 1
        
        matches_first_half.extend(round_matches)
        
        # Rotate team list
        teams = [teams[0]] + [teams[-1]] + teams[1:-1]
        
    # Second half (Return matches)
    matches_second_half = []
    for m in matches_first_half:
        date = datetime.strptime(m["date"], "%Y-%m-%dT%H:%M:%S") + timedelta(days=19*7)
        match = {
            "id": match_id,
            "matchday": m["matchday"] + 19,
            "homeTeamId": m["awayTeamId"],
            "awayTeamId": m["homeTeamId"],
            "date": date.strftime("%Y-%m-%dT%H:%M:%S"),
            "played": False,
            "homeGoals": 0,
            "awayGoals": 0
        }
        matches_second_half.append(match)
        match_id += 1
        
    return matches_first_half + matches_second_half

# 4. MAIN GENERATION

def main():
    final_teams = []
    all_players = []
    
    print("Generating teams and players...")
    for t_def in TEAMS:
        team_id = t_def["id"]
        team_rating = t_def["overallRating"]
        
        team_players = []
        
        # 1. Add Real Players
        real_list = REAL_PLAYERS.get(team_id, [])
        counts = {"GK": 0, "DEF": 0, "MID": 0, "FWD": 0}
        
        for p_real in real_list:
            # Determine group
            pos = p_real["position"].upper()
            grp = "MID"
            if pos in SPECIFIC_POSITIONS["GK"]: grp = "GK"
            elif pos in SPECIFIC_POSITIONS["DEF"]: grp = "DEF"
            elif pos in SPECIFIC_POSITIONS["FWD"]: grp = "FWD"
            
            counts[grp] += 1
            
            p_obj = generate_player(grp, team_rating, 
                                    forced_name=p_real["name"],
                                    forced_pos=pos,
                                    forced_overall=p_real["overall"],
                                    forced_age=p_real["age"],
                                    team_id=team_id)
            team_players.append(p_obj)
            
        # 2. Fill Slots
        # Target: ~3 GK, ~7 DEF, ~7 MID, ~6 FWD -> ~23 total
        target_counts = {"GK": 3, "DEF": 7, "MID": 7, "FWD": 6}
        
        for grp, target in target_counts.items():
            needed = target - counts.get(grp, 0)
            if needed > 0:
                for _ in range(needed):
                    p_obj = generate_player(grp, team_rating, team_id=team_id)
                    team_players.append(p_obj)
        
        # Assign to team
        t_def["players"] = team_players
        final_teams.append(t_def)
        
        print(f"  > {t_def['name']}: {len(team_players)} players")

    print("Generating schedule...")
    team_ids = [t["id"] for t in final_teams]
    all_matches = generate_schedule(team_ids)
    print(f"  > Generated {len(all_matches)} matches.")
    
    # 5. ASSEMBLE DB
    db = {
        "leagueName": "LaLiga EA Sports",
        "country": "Spain",
        "seasonYear": SEASON_YEAR,
        "teams": final_teams,
        "matches": all_matches
    }
    
    # Write
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(db, f, indent=4, ensure_ascii=False)
        
    print(f"Done. Saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
