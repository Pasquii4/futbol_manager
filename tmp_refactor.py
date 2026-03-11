import os
import re

def replace_in_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {filepath}: {e}")
        return

    # specific import rename
    new_content = content.replace("import com.politecnics.football.entity.Player;", "import com.politecnics.football.entity.Jugador;")
    new_content = new_content.replace("import com.politecnics.football.repository.PlayerRepository;", "import com.politecnics.football.repository.JugadorRepository;")
    
    # Class types
    new_content = re.sub(r'\bPlayer\b', 'Jugador', new_content)
    # Revert PlayerDTO since we want to keep it
    new_content = re.sub(r'\bJugadorDTO\b', 'PlayerDTO', new_content)
    # Same for PlayerController, PlayerService if they were matched by word bound? Actually \bPlayer\b doesn't match PlayerController.
    
    new_content = re.sub(r'\bPlayerRepository\b', 'JugadorRepository', new_content)
    new_content = re.sub(r'\bplayerRepository\b', 'jugadorRepository', new_content)
    
    # Method names & variable names
    # Only replace 'player' to 'jugador' if it's an exact word
    new_content = re.sub(r'\bplayer\b', 'jugador', new_content)
    new_content = re.sub(r'\bplayers\b', 'jugadores', new_content)
    
    new_content = re.sub(r'\bgetPlayer\b', 'getJugador', new_content)
    new_content = re.sub(r'\bsetPlayer\b', 'setJugador', new_content)
    new_content = re.sub(r'\bgetPlayers\b', 'getJugadores', new_content)
    new_content = re.sub(r'\bsetPlayers\b', 'setJugadores', new_content)
    
    # Rename specific fields that changed
    new_content = new_content.replace('getOverall', 'getCalidad')
    new_content = new_content.replace('setOverall', 'setCalidad')
    
    if content != new_content:
        try:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Updated {filepath}")
        except Exception as e:
            print(f"Error writing {filepath}: {e}")

api_dir = r'c:\Users\CEP-MATI\Documents\GitHub\futbol_manager\football-manager-api\src\main\java\com\politecnics\football'
for root, _, files in os.walk(api_dir):
    for file in files:
        if file.endswith('.java') and file not in ['Jugador.java', 'JugadorRepository.java']:
            replace_in_file(os.path.join(root, file))
