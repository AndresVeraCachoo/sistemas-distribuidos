# backend/src/routes/test_routes.py
from flask import Blueprint
from src.exceptions.custom_exceptions import ExternalAPIError, DatabaseError, FileReadError

test_bp = Blueprint('test_routes', __name__)

@test_bp.route('/test/file-error', methods=['GET'])
def trigger_file_error():
    """Simula un error abriendo un archivo (nativo de Python)"""
    # Esto lanzará un FileNotFoundError nativo que atrapará nuestro handler
    with open("archivo_que_no_existe_en_ningun_lado.txt", "r") as file:
        contenido = file.read()
    return contenido

@test_bp.route('/test/db-error', methods=['GET'])
def trigger_db_error():
    """Simula un error de base de datos usando nuestra excepción"""
    # Lanzamos el error manualmente a modo de simulación
    raise DatabaseError("No se pudo conectar a la base de datos de historial")

@test_bp.route('/test/api-error', methods=['GET'])
def trigger_api_error():
    """Simula una caída de la PokeAPI"""
    raise ExternalAPIError("La PokeAPI devolvió un error 500 o timeout")

@test_bp.route('/test/success', methods=['GET'])
def trigger_success():
    """Ruta de control para verificar que lo que no falla, funciona"""
    return {"message": "Esta ruta funciona perfectamente", "status": "OK"}