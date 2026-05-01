# backend/src/exceptions/error_handlers.py
from flask import jsonify
from .custom_exceptions import BaseCustomException

def register_error_handlers(app):
    
    # Manejador para nuestras excepciones personalizadas
    @app.errorhandler(BaseCustomException)
    def handle_custom_exception(error):
        response = {
            "error_type": error.__class__.__name__,
            "message": error.message,
            "critical": error.is_critical
        }
        return jsonify(response), error.status_code

    # Manejador genérico para errores 404 (Ruta no encontrada)
    @app.errorhandler(404)
    def handle_404_error(error):
        return jsonify({
            "error_type": "NotFoundError",
            "message": "La ruta solicitada no existe en el servidor",
            "critical": False
        }), 404

    # Manejador para un fallo nativo de Python (ej: FileNotFoundError real)
    @app.errorhandler(FileNotFoundError)
    def handle_file_not_found(error):
        return jsonify({
            "error_type": "FileReadError",
            "message": "Se intentó acceder a un archivo inexistente: " + str(error),
            "critical": True
        }), 500