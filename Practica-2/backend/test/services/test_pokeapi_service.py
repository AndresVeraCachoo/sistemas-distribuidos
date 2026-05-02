import pytest
from unittest.mock import patch, MagicMock
import requests

# Importamos tu función real
from src.services.pokeapi_service import get_pokemon_data

class TestPokeApiService:

    # 🟢 TEST POSITIVO: La API responde bien y mapeamos los datos
    @patch('src.services.pokeapi_service.requests.get')
    def test_get_pokemon_data_exito(self, mock_get):
        # Simulamos la respuesta de la PokeAPI oficial
        mock_response = MagicMock()
        mock_response.status_code = 200
        # Simulamos un JSON con la estructura exacta que tu código espera leer
        mock_response.json.return_value = {
            "name": "pikachu",
            "id": 25,
            "height": 4,
            "weight": 60,
            "sprites": {"front_default": "url_foto_pikachu"},
            "types": [{"type": {"name": "electric"}}],
            "stats": [{"stat": {"name": "speed"}, "base_stat": 90}]
        }
        mock_get.return_value = mock_response

        # Ejecutamos tu función
        result, status = get_pokemon_data("pikachu")

        # Comprobamos que devuelve un 200 y el diccionario formateado
        assert status == 200
        assert result["name"] == "Pikachu" # Tu código hace capitalize()
        assert result["id"] == 25
        assert result["sprite"] == "url_foto_pikachu"
        assert result["types"] == ["electric"]
        assert result["stats"]["speed"] == 90

    # 🔴 TEST NEGATIVO: El Pokémon no existe en la PokeAPI (404)
    @patch('src.services.pokeapi_service.requests.get')
    def test_get_pokemon_data_404(self, mock_get):
        mock_response = MagicMock()
        mock_response.status_code = 404
        mock_get.return_value = mock_response

        # Ejecutamos tu función con un nombre falso
        result, status = get_pokemon_data("agumon")

        # Tu código debería devolver esta tupla exacta
        assert status == 404
        assert result == {"error": "Pokemon no encontrado"}

    # 🔴 TEST NEGATIVO: La PokeAPI se cae (Excepción de Request)
    @patch('src.services.pokeapi_service.requests.get')
    def test_get_pokemon_data_api_caida(self, mock_get):
        # Simulamos que requests lanza un error nativo
        mock_get.side_effect = requests.exceptions.RequestException("Timeout de red")

        result, status = get_pokemon_data("pikachu")

        # Tu código lo atrapa en el except y devuelve 503
        assert status == 503
        assert result == {"error": "Error conectando con PokeAPI"}