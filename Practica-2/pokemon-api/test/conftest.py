import pytest
from src.app import app 

@pytest.fixture
def client():
    """
    Este 'fixture' crea un cliente web simulado.
    Cada vez que un test pida la variable 'client', Pytest le dará este cliente.
    """
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client