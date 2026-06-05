import os
from flask import Flask
from src.routes.pokemon_routes import pokemon_bp
from src.routes.test_routes import test_bp 
from src.routes.team_routes import team_bp 
from src.exceptions.error_handlers import register_error_handlers 

app = Flask(__name__) 

# Registro de rutas
app.register_blueprint(pokemon_bp)
app.register_blueprint(test_bp)  
app.register_blueprint(team_bp)

register_error_handlers(app) 

@app.route('/', methods=['GET'])
def home():
    return {"status": "OK", "mensaje": "Backend Python listo"}

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)