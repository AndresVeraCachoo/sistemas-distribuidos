import os
from flask import Flask
from src.routes.pokemon_routes import pokemon_bp
from src.routes.test_routes import test_bp 
from src.exceptions.error_handlers import register_error_handlers 

app = Flask(__name__) 
app.register_blueprint(pokemon_bp)
app.register_blueprint(test_bp)  

register_error_handlers(app) 

@app.route('/', methods=['GET'])
def home():
    return {"status": "OK", "mensaje": "¡Backend de Python conectado!"}

if __name__ == '__main__':
    host_ip = os.getenv('FLASK_HOST', '0.0.0.0')
    app.run(host=host_ip, port=5000)