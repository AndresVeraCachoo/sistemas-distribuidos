import pytest
from src.exceptions.custom_exceptions import ExternalAPIError

class TestErrorHandlers:

    # 🟢 1. Probamos el 404 (Este funcionaba bien)
    def test_404_handler(self, client):
        response = client.get('/ruta_inventada_que_no_existe')
        
        assert response.status_code == 404
        assert response.is_json
        data = response.json
        assert data["error_type"] == "NotFoundError"
        assert "no existe" in data["message"]
        assert data["critical"] is False

    # 🟢 2. Probamos tu CustomException Handler directamente
    def test_custom_exception_handler(self, client):
        # Creamos una instancia real de tu error
        error = ExternalAPIError("Error forzado de prueba")
        
        # Invocamos al manejador que tienes registrado en Flask
        handler = client.application.error_handler_spec[None][None][ExternalAPIError.__bases__[0]]
        
        with client.application.app_context():
            response, status_code = handler(error)
            
            assert status_code == 502 
            assert response.is_json
            data = response.get_json()
            assert data["error_type"] == "ExternalAPIError"
            assert data["message"] == "Error forzado de prueba"

    # 🟢 3. Probamos el FileNotFoundError Handler directamente
    def test_file_not_found_handler(self, client):
        error = FileNotFoundError("Archivo falso.txt")
        
        # Buscamos el manejador del FileNotFoundError
        handler = client.application.error_handler_spec[None][None][FileNotFoundError]
        
        with client.application.app_context():
            response, status_code = handler(error)
            
            assert status_code == 500
            assert response.is_json
            data = response.get_json()
            assert data["error_type"] == "FileReadError"
            assert data["critical"] is True