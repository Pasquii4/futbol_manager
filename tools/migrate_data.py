import json
import random
import os

# Configuration
INPUT_FILE = r'C:\Users\CEP-MATI\Documents\GitHub\futbol_manager\jugadors.json'
OUTPUT_FILE = r'C:\Users\CEP-MATI\Documents\GitHub\futbol_manager\football-manager-api\src\main\resources\data\laliga_db.json'

# Team Metadata (augmenting jugadors.json)
TEAM_METADATA = {
    "FC Barcelona": {"id": "fc_barcelona", "stadium": "Spotify Camp Nou", "budget": 600000000, "rating": 86},
    "Real Madrid CF": {"id": "real_madrid", "stadium": "Santiago Bernabéu", "budget": 750000000, "rating": 88},
    "Atlético de Madrid": {"id": "atletico_madrid", "stadium": "Cívitas Metropolitano", "budget": 450000000, "rating": 84},
    "Sevilla FC": {"id": "sevilla_fc", "stadium": "Ramón Sánchez-Pizjuán", "budget": 150000000, "rating": 79},
    "Real Sociedad": {"id": "real_sociedad", "stadium": "Reale Arena", "budget": 180000000, "rating": 81},
    "Real Betis": {"id": "real_betis", "stadium": "Benito Villamarín", "budget": 160000000, "rating": 80},
    "Athletic Club": {"id": "athletic_club", "stadium": "San Mamés", "budget": 200000000, "rating": 82},
    "Villarreal CF": {"id": "villarreal_cf", "stadium": "Estadio de la Cerámica", "budget": 140000000, "rating": 80},
    "Valencia CF": {"id": "valencia_cf", "stadium": "Mestalla", "budget": 100000000, "rating": 77},
    "RC Celta de Vigo": {"id": "celta_vigo", "stadium": "Abanca-Balaídos", "budget": 80000000, "rating": 76},
    "RCD Mallorca": {"id": "rcd_mallorca", "stadium": "Son Moix", "budget": 60000000, "rating": 75},
    "Girona FC": {"id": "girona_fc", "stadium": "Montilivi", "budget": 100000000, "rating": 80},
    "CA Osasuna": {"id": "osasuna", "stadium": "El Sadar", "budget": 70000000, "rating": 76},
    "Getafe CF": {"id": "getafe_cf", "stadium": "Coliseum", "budget": 65000000, "rating": 75},
    "UD Las Palmas": {"id": "las_palmas", "stadium": "Gran Canaria", "budget": 50000000, "rating": 74},
    "RCD Espanyol": {"id": "rcd_espanyol", "stadium": "Stage Front Stadium", "budget": 55000000, "rating": 74},
    "CD Leganés": {"id": "cd_leganes", "stadium": "Butarque", "budget": 45000000, "rating": 73},
    "Real Valladolid CF": {"id": "real_valladolid", "stadium": "José Zorrilla", "budget": 40000000, "rating": 72},
    "Rayo Vallecano": {"id": "rayo_vallecano", "stadium": "Vallecas", "budget": 50000000, "rating": 75},
    "Deportivo Alavés": {"id": "alaves", "stadium": "Mendizorroza", "budget": 45000000, "rating": 74}
}

POSITIONS_MAP = {
    "Porter": "GK",
    "Defensa": "CB", # Default to CB, will randomize later
    "Migcampista": "CM", # Default to CM
    "Davanter": "ST" # Default to ST
}

def generate_player(team_name, position_category, team_rating):
    # Position logic
    if position_category == "GK": pos = "GK"
    elif position_category == "DEF": pos = random.choice(["CB", "CB", "LB", "RB"])
    elif position_category == "MID": pos = random.choice(["CM", "CM", "CDM", "CAM"])
    elif position_category == "FWD": pos = random.choice(["ST", "ST", "RW", "LW"])
    else: pos = "CM"

    # Rating logic (Normal distribution around team rating)
    rating = int(random.normalvariate(team_rating - 2, 3))
    rating = max(60, min(95, rating))
    potential = rating + random.randint(0, 5)
    
    first_names = ["Alejandro", "Pablo", "David", "Marc", "Javi", "Hugo", "Daniel", "Adrian", "Alvaro", "Diego", "Lucas", "Leo", "Manuel", "Mateo"]
    last_names = ["Garcia", "Rodriguez", "Gonzalez", "Fernandez", "Lopez", "Martinez", "Sanchez", "Perez", "Gomez", "Martin", "Jimenez", "Ruiz"]

    return {
        "id": f"gen_{random.randint(10000, 99999)}",
        "name": f"{random.choice(first_names)} {random.choice(last_names)}",
        "position": pos,
        "age": random.randint(18, 35),
        "overall": rating,
        "potential": potential
    }

def main():
    print(f"Reading {INPUT_FILE}...")
    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        source_data = json.load(f)

    final_teams = []

    for src_team in source_data:
        team_name = src_team['equip']
        if team_name not in TEAM_METADATA:
            print(f"Warning: Team {team_name} not in metadata, using defaults.")
            meta = {"id": team_name.lower().replace(" ", "_"), "stadium": "Unknown", "budget": 10000000, "rating": 70}
        else:
            meta = TEAM_METADATA[team_name]

        new_team = {
            "id": meta["id"],
            "name": team_name,
            "stadium": meta["stadium"],
            "budget": meta["budget"],
            "overallRating": meta["rating"],
            "players": []
        }

        # Process existing players
        for p in src_team.get('jugadors', []):
            mapped_pos = POSITIONS_MAP.get(p['posicio'], "CM")
            
            # Refine position based on name if possible (optional) or just use map
            # For simplicity, assign specific positions based on array index or random
            # Actually, let's keep it simple: mapped_pos is broad, let's specialize it
            final_pos = mapped_pos
            if mapped_pos == "CB": final_pos = random.choice(["CB", "CB", "LB", "RB"])
            if mapped_pos == "CM": final_pos = random.choice(["CM", "CDM", "CAM"])
            if mapped_pos == "ST": final_pos = random.choice(["ST", "RW", "LW"])
            if mapped_pos == "GK": final_pos = "GK"

            new_player = {
                "id": p['nomPersona'].lower().replace(" ", "_"),
                "name": p['nomPersona'],
                "position": final_pos,
                "age": random.randint(19, 34), # Randomize age as it's missing
                "overall": p['qualitat'],
                "potential": p['qualitat'] + 2
            }
            new_team['players'].append(new_player)

        # Fill squad to 22 players
        current_count = len(new_team['players'])
        target_count = 22
        
        # Ensure we have at least 2 GKs
        gk_count = sum(1 for p in new_team['players'] if p['position'] == 'GK')
        while gk_count < 3:
            new_team['players'].append(generate_player(team_name, "GK", meta['rating']))
            gk_count += 1
            current_count += 1

        while current_count < target_count:
            # Distribution
            r = random.random()
            if r < 0.3: cat = "DEF"
            elif r < 0.7: cat = "MID"
            else: cat = "FWD"
            
            new_team['players'].append(generate_player(team_name, cat, meta['rating']))
            current_count += 1

        final_teams.append(new_team)

    output_data = {
        "leagueName": "LaLiga EA Sports",
        "country": "Spain",
        "seasonYear": 2025,
        "teams": final_teams
    }

    print(f"Writing {len(final_teams)} teams to {OUTPUT_FILE}...")
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(output_data, f, indent=4, ensure_ascii=False)
    print("Done!")

if __name__ == "__main__":
    main()
