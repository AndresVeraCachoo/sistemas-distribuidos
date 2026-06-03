from flask import Blueprint
import requests
import psycopg2
from src.exceptions.custom_exceptions import DatabaseError, ExternalAPIError, FileReadError

test_bp = Blueprint('test', __name__)

@test_bp.route('/test/db-error', methods=['GET'])
def trigger_db_error():
    """Fuerza un error de base de datos REAL intentando conectar con credenciales inválidas"""
    try:
        # Intentamos conectarnos de verdad a una BD que no existe para provocar el fallo
        psycopg2.connect(
            dbname="bd_inventada",
            user="usuario_falso",
            password="bad_password", # NOSONAR
            host="localhost",
            port="5432"
        )
    except psycopg2.OperationalError as e:
        # CAPTURAMOS el error real de la librería psycopg2 y lanzamos nuestra excepción
        raise DatabaseError(f"No se pudo conectar a la base de datos: {str(e)}")


@test_bp.route('/test/api-error', methods=['GET'])
def trigger_api_error():
    """Fuerza una caída de la PokeAPI haciendo una petición REAL con un timeout imposible"""
    try:
        # Hacemos una petición real, pero le damos 0.001 segundos para responder (fallará seguro)
        requests.get("https://pokeapi.co/api/v2/pokemon/pikachu", timeout=0.001)
    except requests.exceptions.RequestException:
        # CAPTURAMOS el error de la librería requests y lanzamos el nuestro
        raise ExternalAPIError("La PokeAPI devolvió un error o timeout al intentar conectar.")


@test_bp.route('/test/file-error', methods=['GET'])
def trigger_file_error():
    """Fuerza un error REAL intentando abrir un archivo inexistente"""
    try:
        # Intentamos leer un archivo que no existe en el disco duro
        with open("archivo_inexistente.json", "r") as file:
            file.read()
    except FileNotFoundError:

        raise FileReadError("Error crítico: No se encuentra el archivo en el sistema.")


@test_bp.route('/test/success', methods=['GET'])
def trigger_success():
    """Ruta de control para verificar que lo que no falla, funciona"""
    return {"message": "Esta ruta funciona perfectamente", "status": "OK"}