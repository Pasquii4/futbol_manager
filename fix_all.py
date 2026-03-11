import os
import re

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Fix builder methods for PlayerDTO and MatchEventDTO
    if "Controller.java" in filepath:
        content = content.replace('.jugadorId(', '.playerId(')

    # Fix MatchController
    if "MatchController.java" in filepath:
        content = content.replace('.jugadorId(Long)', '.playerId(Long)')
        # Fix the generic wildcard error on line 56? Wait
        # inference variable T has incompatible bounds. equality constraints: MatchEventDTO
        # This usually happens if the map() isn't returning MatchEventDTO.

    # Fix DataLoadService
    if "DataLoadService.java" in filepath:
        content = content.replace('com.politecnics.football.entity.League', 'com.politecnics.football.entity.Liga')
        content = content.replace('LeagueRepository', 'LigaRepository')
        content = content.replace('leagueRepository', 'ligaRepository')
        
        content = content.replace('league = com.politecnics.football.entity.Liga.builder()', 'league = com.politecnics.football.entity.Liga.builder()')
        content = content.replace('.country(', '//.country(')
        content = content.replace('.seasonYear(', '//.seasonYear(')
        content = content.replace('.managedTeamId(', '//.managedTeamId(')
        
        content = content.replace('Equipo team =', 'Equipo equipo =')
        content = content.replace('team =', 'equipo =')
        content = content.replace('team.', 'equipo.')
        content = content.replace('equipo = Equipo.builder()', 'equipo = Equipo.builder()')
        content = content.replace('.team(', '.equipo(')
        
        content = content.replace('homeTeam.isPresent()', 'homeEquipo.isPresent()')
        content = content.replace('awayTeam.isPresent()', 'awayEquipo.isPresent()')
        content = content.replace('homeTeam.get()', 'homeEquipo.get()')
        content = content.replace('awayTeam.get()', 'awayEquipo.get()')
        
        content = content.replace('homeTeam =', 'homeEquipo =')
        content = content.replace('awayTeam =', 'awayEquipo =')

        # fix builder name() -> nombre()
        content = content.replace('Jugador.builder()\n                                    .jugadorId(playerId)\n                                    .name', 'Jugador.builder()\n                                    .jugadorId(playerId)\n                                    .nombre')

    # Fix FixtureGenerator
    if "FixtureGenerator.java" in filepath:
        content = content.replace('fixedTeam', 'fixedEquipo')

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

base_dir = r"c:\Users\CEP-MATI\Documents\GitHub\futbol_manager\football-manager-api\src\main\java\com\politecnics\football"
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            fix_file(os.path.join(root, file))
