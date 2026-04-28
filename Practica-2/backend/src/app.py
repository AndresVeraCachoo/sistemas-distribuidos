import os
from flask import Flask
from routes.pokemon_routes import pokemon_bp

app = Flask(__name__)
app.register_blueprint(pokemon_bp)

# Especificar el método HTTP explícitamente
@app.route('/', methods=['GET'])
def home():
    return {"status": "OK", "mensaje": "¡Backend de Python conectado!"}

if __name__ == '__main__':
    host_ip = os.getenv('FLASK_HOST', '0.0.0.0')
    app.run(host=host_ip, port=5000)