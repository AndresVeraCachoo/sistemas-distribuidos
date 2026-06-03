import pytest
import psycopg2
from unittest.mock import patch

class TestTestRoutes:

    # TEST POSITIVO
    def test_health_check(self, client):
        response = client.get('/')
        assert response.status_code == 200
        assert response.is_json
        data = response.json
        assert data["status"] == "OK"
        assert "conectado" in data["mensaje"]

    def test_trigger_success(self, client):
        response = client.get('/test/success')
        assert response.status_code == 200
        assert response.json["status"] == "OK"

    # TESTS NEGATIVOS

    @patch('src.routes.test_routes.psycopg2.connect')
    def test_trigger_db_error(self, mock_connect, client):
        # MOCKEAMOS la conexión para evitar el bug de codificación de Windows.
        # Hacemos que directamente salte el OperationalError real de psycopg2
        mock_connect.side_effect = psycopg2.OperationalError("Simulando fallo de red")
        
        response = client.get('/test/db-error')
        
        # DatabaseError está configurado para devolver 503
        assert response.status_code == 503
        assert "error_type" in response.json
        assert response.json["error_type"] == "DatabaseError"

    def test_trigger_api_error(self, client):
        # Provoca el Timeout que lanza ExternalAPIError
        response = client.get('/test/api-error')
        
        # ExternalAPIError está configurado para devolver 502
        assert response.status_code == 502
        assert "error_type" in response.json
        assert response.json["error_type"] == "ExternalAPIError"

    def test_trigger_file_error(self, client):
        # Provoca el FileNotFoundError que lanza FileReadError
        response = client.get('/test/file-error')
        
        # FileReadError está configurado para devolver 500
        assert response.status_code == 500
        assert "error_type" in response.json
        assert response.json["error_type"] == "FileReadError"