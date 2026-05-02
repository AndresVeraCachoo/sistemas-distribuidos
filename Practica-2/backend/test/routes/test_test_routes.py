import pytest

class TestTestRoutes:

    # 🟢 TEST POSITIVO: Comprobar que el endpoint de estado funciona
    def test_health_check(self, client):
        # Hacemos una petición a la ruta principal de tu app
        response = client.get('/')
        
        # Comprobamos que el servidor responde con un 200 OK
        assert response.status_code == 200
        assert response.is_json
        
        # Comprobamos que devuelve el mensaje que programaste en app.py
        data = response.json
        assert data["status"] == "OK"
        assert "conectado" in data["mensaje"]