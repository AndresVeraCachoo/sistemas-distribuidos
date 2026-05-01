# backend/test/test_exceptions.py
import pytest
from src.app import app  # Asegúrate de que tu app de Flask se llama 'app' en src/app.py

@pytest.fixture
def client():
    """Configura un cliente de pruebas de Flask"""
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_file_read_error_simulado(client):
    """Prueba 1: Excepción de apertura y lectura de archivos"""
    response = client.get('/test/file-error')
    data = response.get_json()
    
    assert response.status_code == 500
    assert data['error_type'] == 'FileReadError'
    assert data['critical'] is True

def test_database_error_simulado(client):
    """Prueba 2: Excepción de acceso a bases de datos"""
    response = client.get('/test/db-error')
    data = response.get_json()
    
    assert response.status_code == 503
    assert data['error_type'] == 'DatabaseError'
    assert data['critical'] is True

def test_external_api_error_simulado(client):
    """Prueba 3: Excepción de llamadas a APIs de terceros (Pokémon)"""
    response = client.get('/test/api-error')
    data = response.get_json()
    
    assert response.status_code == 502
    assert data['error_type'] == 'ExternalAPIError'
    assert data['critical'] is False
    assert "PokeAPI" in data['message']