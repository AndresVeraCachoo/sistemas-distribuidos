import os
from flask import Flask

app = Flask(__name__)

# Especificar el método HTTP explícitamente
@app.route('/', methods=['GET'])
def home():
    return {"status": "OK", "mensaje": "¡Backend de Python vivo y conectado!"}

if __name__ == '__main__':
    host_ip = os.getenv('FLASK_HOST', '0.0.0.0')
    app.run(host=host_ip, port=5000)