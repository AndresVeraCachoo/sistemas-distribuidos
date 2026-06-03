import psycopg2
import os
from src.exceptions.custom_exceptions import DatabaseError

def get_db_connection():
    """Establece la conexión REAL a PostgreSQL usando las variables de entorno de Docker."""
    try:
        conn = psycopg2.connect(
            host=os.getenv("DB_HOST", "db"),
            database=os.getenv("DB_NAME", "practica_db"),
            user=os.getenv("DB_USER", "admin"),
            password=os.getenv("DB_PASSWORD", "admin_secreto"),
            port=5432
        )
        return conn
    except psycopg2.OperationalError as e:
        # Capturo el error real nativo y lanzo nuestra excepción controlada
        raise DatabaseError(f"Fallo crítico al conectar con PostgreSQL: {str(e)}")

def log_search_in_db(pokemon_name):
    """Crea la tabla si no existe y guarda el registro de la búsqueda."""
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        # Aseguramos que la tabla exista
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS registro_api (
                id SERIAL PRIMARY KEY,
                pokemon_name VARCHAR(100) NOT NULL,
                fecha_busqueda TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Insertamos el Pokémon buscado
        cursor.execute(
            "INSERT INTO registro_api (pokemon_name) VALUES (%s)", 
            (pokemon_name,)
        )
        
        conn.commit()
        cursor.close()
        conn.close()
        
    except DatabaseError:
        # Si falló la conexión en get_db_connection(), simplemente propagamos el error
        raise 
    except Exception as e:
        raise DatabaseError(f"Error al ejecutar consulta en la BD: {str(e)}")