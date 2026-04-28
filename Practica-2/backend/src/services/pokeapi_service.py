import requests
from flask import current_app

def get_pokemon_data(name):
    """Consulta la PokeAPI externa y devuelve los datos limpios."""
    url = f"https://pokeapi.co/api/v2/pokemon/{name.lower()}"
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 404:
            return {"error": "Pokemon no encontrado"}, 404
        
        data = response.json()
        # Filtramos solo lo que nos interesa para no saturar a Java
        result = {
            "name": data["name"].capitalize(),
            "id": data["id"],
            "height": data["height"],
            "weight": data["weight"],
            "sprite": data["sprites"]["front_default"],
            "types": [t["type"]["name"] for t in data["types"]],
            "stats": {s["stat"]["name"]: s["base_stat"] for s in data["stats"]}
        }
        return result, 200
    except requests.exceptions.RequestException:
        return {"error": "Error conectando con PokeAPI"}, 503