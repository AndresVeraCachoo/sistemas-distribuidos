from flask import Blueprint, jsonify
from src.services.pokeapi_service import get_pokemon_data

pokemon_bp = Blueprint('pokemon', __name__)

@pokemon_bp.route('/api/pokemon/<name>', methods=['GET'])
def get_pokemon(name):
    result, status = get_pokemon_data(name)
    return jsonify(result), status