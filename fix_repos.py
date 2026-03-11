import os

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Fix imports
    content = content.replace('import com.politecnics.football.repository.TeamRepository;', 'import com.politecnics.football.repository.EquipoRepository;')
    content = content.replace('import com.politecnics.football.entity.Team;', 'import com.politecnics.football.entity.Equipo;')
    
    # Fix variables and types
    content = content.replace('TeamRepository ', 'EquipoRepository ')
    content = content.replace('teamRepository', 'equipoRepository')
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

base_dir = r"c:\Users\CEP-MATI\Documents\GitHub\futbol_manager\football-manager-api\src\main\java\com\politecnics\football"
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            replace_in_file(os.path.join(root, file))
