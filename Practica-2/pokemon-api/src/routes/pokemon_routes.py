from flask import Blueprint, jsonify
from src.services.pokeapi_service import get_pokemon_data
from src.database import log_search_in_db

pokemon_bp = Blueprint('pokemon', __name__)

@pokemon_bp.route('/api/pokemon/<name>', methods=['GET'])
def get_pokemon(name):
    result, status = get_pokemon_data(name)
    
    # Si la PokeAPI devolvió los datos correctamente, lo registramos en nuestra base de datos
    if status == 200:
        log_search_in_db(name)
        
    return jsonify(result), status