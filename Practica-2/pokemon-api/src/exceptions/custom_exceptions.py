# backend/src/exceptions/custom_exceptions.py

class BaseCustomException(Exception):
    """Clase base para nuestras excepciones personalizadas"""
    def __init__(self, message, status_code=500, is_critical=False):
        super().__init__(message)
        self.message = message
        self.status_code = status_code
        self.is_critical = is_critical

class FileReadError(BaseCustomException):
    def __init__(self, message="Error de lectura de archivo en el servidor"):
        super().__init__(message, status_code=500, is_critical=True)

class DatabaseError(BaseCustomException):
    def __init__(self, message="Error de acceso a la base de datos"):
        super().__init__(message, status_code=503, is_critical=True)

class ExternalAPIError(BaseCustomException):
    def __init__(self, message="Fallo en la comunicación con la PokeAPI"):
        super().__init__(message, status_code=502, is_critical=False) # No es crítico, el front puede mostrar un mensaje