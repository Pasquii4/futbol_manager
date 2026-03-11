import os
import re

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Substitutions for Jugador
    content = content.replace('.getPlayerId()', '.getJugadorId()')
    content = content.replace('.playerId(', '.jugadorId(')
    content = content.replace('.getName()', '.getNombre()')
    content = content.replace('.getPosition()', '(.getPosicion() != null ? .getPosicion().name() : null)')
    # Fix the generic replacement above with regex for safer usage
    content = re.sub(r'([a-zA-Z0-9_]+)\.getPosition\(\)', r'(\1.getPosicion() != null ? \1.getPosicion().name() : "")', content)
    content = content.replace('.getForm()', '.getForma()')
    content = content.replace('.setForm(', '.setForma(')
    
    # Age calculation - simpler to just look for .getAge() and replace with age calc
    content = re.sub(r'([a-zA-Z0-9_]+)\.getAge\(\)', r'(java.time.Period.between(\1.getFechaNacimiento(), java.time.LocalDate.now()).getYears())', content)
    
    # Potential doesn't exist natively, assume we add it or just map it to calidad for now
    content = content.replace('.getPotential()', '.getCalidad()')
    
    # Team to Equipo replacements
    content = content.replace('Team ', 'Equipo ')
    content = content.replace('Team>', 'Equipo>')
    content = content.replace('<Team>', '<Equipo>')
    content = content.replace('(Team ', '(Equipo ')
    
    # Special fix for StatsController
    content = content.replace('.findTop20ByOrderByOverallDesc()', '.findTop20ByOrderByCalidadDesc()')

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

base_dir = r"c:\Users\CEP-MATI\Documents\GitHub\futbol_manager\football-manager-api\src\main\java\com\politecnics\football"
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            replace_in_file(os.path.join(root, file))
