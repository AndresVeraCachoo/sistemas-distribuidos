import pytest
from unittest.mock import patch

class TestPokemonRoutes:

    # 🟢 TEST POSITIVO: Endpoint de buscar funciona
    @patch('src.routes.pokemon_routes.log_search_in_db') # <-- 1. MOCKEAMOS LA BD PARA QUE NO PETE
    @patch('src.routes.pokemon_routes.get_pokemon_data')
    def test_buscar_pokemon_exito(self, mock_get_data, mock_log_db, client): # <-- 2. AÑADIMOS mock_log_db AL PARÁMETRO
        # Simulamos que tu servicio devuelve la tupla de éxito
        mock_get_data.return_value = ({"name": "Pikachu", "id": 25}, 200)

        response = client.get('/api/pokemon/pikachu')

        assert response.status_code == 200
        assert response.json["name"] == "Pikachu"

    # 🔴 TEST NEGATIVO: Endpoint devuelve error 404 correctamente
    @patch('src.routes.pokemon_routes.get_pokemon_data')
    def test_buscar_pokemon_no_encontrado(self, mock_get_data, client):
        mock_get_data.return_value = ({"error": "Pokemon no encontrado"}, 404)
        response = client.get('/api/pokemon/agumon')
        assert response.status_code == 404
        assert response.json["error"] == "Pokemon no encontrado"
        
    # 🔴 TEST NEGATIVO: Endpoint devuelve error 503 si la PokeAPI cae
    @patch('src.routes.pokemon_routes.get_pokemon_data')
    def test_buscar_pokemon_api_caida(self, mock_get_data, client):
        mock_get_data.return_value = ({"error": "Error conectando con PokeAPI"}, 503)
        response = client.get('/api/pokemon/pikachu')
        assert response.status_code == 503
        assert response.json["error"] == "Error conectando con PokeAPI"