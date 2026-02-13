"""
📸 DESCARGADOR DE FOTOS - FUTBOL MANAGER
========================================
Este script descarga automáticamente las fotos de los jugadores y entrenadores
del archivo 'jugadors.json' usando múltiples estrategias inteligentes:
1. Wikipedia en Español
2. Wikipedia en Inglés (para mayor cobertura)
3. Wikimedia Commons
4. Correcciones específicas para nombres problemáticos

Al finalizar, genera 'jugadors_con_fotos.json' con las rutas actualizadas.
"""

import json
import os
import requests
import time
import re
from urllib.parse import quote

# Configuración
JSON_INPUT = 'jugadors.json'
JSON_OUTPUT = 'jugadors_con_fotos.json'
CARPETA_IMAGENES = 'imagenes'

# Correcciones manuales de nombres para mejorar la búsqueda
CORRECCIONES_NOMBRES = {
    'Antoine Griezman': 'Antoine Griezmann',
    'Orjan Dyland': 'Ørjan Nyland',
    'Raúl Morata': 'Álvaro Morata',
    'Isaac Romero': 'Isaac Romero (footballer)',
    'Christantus Uche': 'Christantus Uche',
    'Vedat Muriqui': 'Vedat Muriqi',
    'Javier Puado': 'Javi Puado',
    'Samu Costa': 'Samu Costa (footballer)',
    'Nacho Vidal': 'Nacho Vidal (footballer)', # Evitar confusión con actor
    'Abdón Prats': 'Abdón Prats',
    'Javi Hernández': 'Javi Hernández (footballer, born 1989)',
    'Sergio González': 'Sergio González (footballer, born 1992)',
    'Luis Carrión': 'Luis Carrión',
    'Fran García': 'Fran García (footballer, born 1999)',
}

def sanitize_filename(name):
    """Convierte el nombre en un nombre de archivo válido"""
    name = re.sub(r'[<>:"/\\|?*]', '', name)
    name = name.replace(' ', '_')
    return name

def imagen_ya_existe(nombre_archivo):
    """Verifica si ya existe una imagen para esta persona"""
    extensiones = ['.jpg', '.jpeg', '.png', '.svg', '.gif']
    for ext in extensiones:
        if os.path.exists(os.path.join(CARPETA_IMAGENES, nombre_archivo + ext)):
            return True
    return False

def descargar_imagen(url, nombre_archivo):
    """Descarga una imagen desde una URL"""
    try:
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
        response = requests.get(url, headers=headers, timeout=15, stream=True)
        response.raise_for_status()
        
        # Determinar extensión
        content_type = response.headers.get('content-type', '')
        if 'png' in content_type:
            ext = '.png'
        elif 'svg' in content_type:
            ext = '.svg'
        else:
            ext = '.jpg'
            
        ruta = os.path.join(CARPETA_IMAGENES, nombre_archivo + ext)
        
        with open(ruta, 'wb') as f:
            for chunk in response.iter_content(chunk_size=8192):
                f.write(chunk)
        return True
    except Exception as e:
        return False

def buscar_wiki_api(query, lang='es'):
    """Busca en la API de Wikipedia del idioma especificado"""
    try:
        url = f"https://{lang}.wikipedia.org/w/api.php"
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
        
        # 1. Buscar página
        params = {
            'action': 'query',
            'format': 'json',
            'list': 'search',
            'srsearch': query,
            'srlimit': 1
        }
        
        r = requests.get(url, params=params, headers=headers, timeout=10)
        data = r.json()
        
        if not data.get('query', {}).get('search'):
            return None
            
        title = data['query']['search'][0]['title']
        
        # 2. Obtener imagen de la página
        img_params = {
            'action': 'query',
            'format': 'json',
            'titles': title,
            'prop': 'pageimages',
            'piprop': 'original|thumbnail',
            'pithumbsize': 500
        }
        
        r = requests.get(url, params=img_params, headers=headers, timeout=10)
        img_data = r.json()
        
        pages = img_data.get('query', {}).get('pages', {})
        if not pages:
            return None
            
        page = list(pages.values())[0]
        
        # Preferir thumbnail grande si existe, sino original
        if 'thumbnail' in page:
            return page['thumbnail']['source']
        if 'original' in page:
            return page['original']['source']
            
        return None
    except:
        return None

def buscar_commons(query):
    """Busca directamente en Wikimedia Commons"""
    try:
        url = "https://commons.wikimedia.org/w/api.php"
        headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'}
        
        params = {
            'action': 'query',
            'format': 'json',
            'list': 'search',
            'srsearch': query,
            'srnamespace': 6, # File namespace
            'srlimit': 1
        }
        
        r = requests.get(url, params=params, headers=headers, timeout=10)
        data = r.json()
        
        if not data.get('query', {}).get('search'):
            return None
            
        title = data['query']['search'][0]['title']
        
        file_params = {
            'action': 'query',
            'format': 'json',
            'titles': title,
            'prop': 'imageinfo',
            'iiprop': 'url'
        }
        
        r = requests.get(url, params=file_params, headers=headers, timeout=10)
        file_data = r.json()
        
        pages = file_data.get('query', {}).get('pages', {})
        page = list(pages.values())[0]
        
        if 'imageinfo' in page:
            return page['imageinfo'][0]['url']
            
        return None
    except:
        return None

def buscar_imagen_inteligente(nombre, equipo):
    """Estrategia combinada para encontrar la mejor imagen"""
    
    # 0. Usar nombre corregido si existe
    nombre_busqueda = CORRECCIONES_NOMBRES.get(nombre, nombre)
    
    # Lista de intentos ordenados por probabilidad de éxito
    intentos = [
        (buscar_wiki_api, [nombre_busqueda, 'es']),                    # 1. Wikipedia ES exacto
        (buscar_wiki_api, [f"{nombre_busqueda} (futbolista)", 'es']),  # 2. Wikipedia ES + contexto
        (buscar_wiki_api, [nombre_busqueda, 'en']),                    # 3. Wikipedia EN exacto
        (buscar_wiki_api, [f"{nombre_busqueda} (footballer)", 'en']),  # 4. Wikipedia EN + contexto
        (buscar_commons, [f"{nombre_busqueda} {equipo}"]),             # 5. Commons + Equipo
        (buscar_commons, [nombre_busqueda])                            # 6. Commons exacto
    ]
    
    for func, args in intentos:
        url = func(*args)
        if url:
            return url
            
    return None

def actualizar_json_rutas(data):
    """Actualiza el JSON con las rutas de las imágenes encontradas"""
    count = 0
    for equipo in data:
        # Jugadores
        for jugador in equipo['jugadors']:
            nombre = jugador['nomPersona']
            nombre_archivo = sanitize_filename(nombre)
            
            # Buscar imagen con cualquier extensión válida
            encontrado = False
            for ext in ['.jpg', '.jpeg', '.png', '.svg']:
                if os.path.exists(os.path.join(CARPETA_IMAGENES, nombre_archivo + ext)):
                    jugador['foto'] = f"{CARPETA_IMAGENES}/{nombre_archivo}{ext}"
                    encontrado = True
                    count += 1
                    break
            
            if not encontrado:
                jugador['foto'] = "" # Limpiar si no hay foto
                
        # Entrenador
        entrenador = equipo['entrenador']
        nombre = entrenador['nomPersona']
        nombre_archivo = sanitize_filename(nombre)
        
        encontrado = False
        for ext in ['.jpg', '.jpeg', '.png', '.svg']:
            if os.path.exists(os.path.join(CARPETA_IMAGENES, nombre_archivo + ext)):
                entrenador['foto'] = f"{CARPETA_IMAGENES}/{nombre_archivo}{ext}"
                encontrado = True
                count += 1
                break
        
        if not encontrado:
            entrenador['foto'] = ""

    return count

def main():
    print("==================================================")
    print("⚽ DESCARGADOR DE FOTOS - FUTBOL MANAGER")
    print("==================================================")
    
    # 1. Preparar directorios
    if not os.path.exists(CARPETA_IMAGENES):
        os.makedirs(CARPETA_IMAGENES)
        print(f"📁 Carpeta '{CARPETA_IMAGENES}' creada.")
        
    # 2. Cargar JSON
    if not os.path.exists(JSON_INPUT):
        print(f"❌ Error: No se encuentra {JSON_INPUT}")
        return
        
    with open(JSON_INPUT, 'r', encoding='utf-8') as f:
        data = json.load(f)
        
    total_personas = 0
    descargadas_nuevas = 0
    ya_existentes = 0
    fallidas = 0
    
    # 3. Procesar lista completa
    personas_a_procesar = []
    
    for equipo in data:
        for jugador in equipo['jugadors']:
            personas_a_procesar.append((jugador['nomPersona'], equipo['equip'], 'Jugador'))
        
        entrenador = equipo['entrenador']
        personas_a_procesar.append((entrenador['nomPersona'], equipo['equip'], 'Entrenador'))
        
    total_personas = len(personas_a_procesar)
    print(f"📋 Procesando {total_personas} personas...")
    
    for nombre, equipo, rol in personas_a_procesar:
        nombre_archivo = sanitize_filename(nombre)
        
        if imagen_ya_existe(nombre_archivo):
            print(f"✅ [EXISTE] {nombre}")
            ya_existentes += 1
            continue
            
        print(f"🔍 [BUSCANDO] {nombre} ({rol} - {equipo})...")
        url = buscar_imagen_inteligente(nombre, equipo)
        
        if url:
            if descargar_imagen(url, nombre_archivo):
                print(f"  ⬇️ [DESCARGADA]")
                descargadas_nuevas += 1
            else:
                print(f"  ❌ [ERROR DESCARGA]")
                fallidas += 1
        else:
            print(f"  ⚠️ [NO ENCONTRADA]")
            fallidas += 1
            
        # Pequeña pausa para no saturar APIs
        time.sleep(1)
        
    # 4. Actualizar JSON
    print("\n🔄 Actualizando archivo JSON...")
    total_con_foto = actualizar_json_rutas(data)
    
    with open(JSON_OUTPUT, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)
        
    print("\n==================================================")
    print("📊 RESUMEN FINAL")
    print("==================================================")
    print(f"Total procesados: {total_personas}")
    print(f"✅ Ya existían:    {ya_existentes}")
    print(f"⬇️ Nuevas:         {descargadas_nuevas}")
    print(f"❌ Fallidas:       {fallidas}")
    print(f"--------------------------------------------------")
    print(f"📸 Total con foto: {total_con_foto} / {total_personas} ({(total_con_foto/total_personas*100):.1f}%)")
    print(f"💾 JSON guardado:  {JSON_OUTPUT}")
    print("==================================================")

if __name__ == "__main__":
    main()
