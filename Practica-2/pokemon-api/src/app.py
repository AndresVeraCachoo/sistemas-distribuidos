import os
from flask import Flask
from src.routes.pokemon_routes import pokemon_bp
from src.exceptions.error_handlers import register_error_handlers 

app = Flask(__name__) 
app.register_blueprint(pokemon_bp)

# CONECTAMOS LOS MANEJADORES DE ERRORES A LA APP
register_error_handlers(app) 

# Especificar el método HTTP explícitamente
@app.route('/', methods=['GET'])
def home():
    return {"status": "OK", "mensaje": "¡Backend de Python conectado!"}

if __name__ == '__main__':
    host_ip = os.getenv('FLASK_HOST', '0.0.0.0')
    app.run(host=host_ip, port=5000)