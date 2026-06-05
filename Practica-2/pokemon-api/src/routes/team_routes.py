import os
import json
import google.generativeai as genai
from flask import Blueprint, request, jsonify

team_bp = Blueprint('team', __name__)

@team_bp.route('/api/team-analysis', methods=['POST'])
def analyze_team():
    try:
        data = request.get_json()
        equipo = data.get('equipo', [])

        if not equipo or len(equipo) != 6:
            return jsonify({"error_type": "ExternalAPIError", "critical": False}), 400

        api_key = os.getenv("GEMINI_API_KEY")
        if not api_key:
            print("ERROR: No se encontró la GEMINI_API_KEY en el entorno.", flush=True)
            return jsonify({"error_type": "ExternalAPIError", "critical": False}), 500
            
        genai.configure(api_key=api_key)

        # ✨ EL CAMBIO MÁGICO: Usamos el modelo actual que sí existe en los servidores de Google
        model = genai.GenerativeModel('gemini-2.5-flash')
        
        prompt = f"""
        Actúa como un experto en competitivo de Pokémon. Analiza este equipo: {', '.join(equipo)}.
        Devuelve estrictamente un objeto JSON válido, sin formato markdown ni texto adicional, con esta estructura exacta:
        {{
            "puntuacion": (un numero entero del 1 al 100),
            "sinergia": "(texto analizando debilidades y resistencias)",
            "balance": "(texto sobre atacantes físicos, especiales y defensas)",
            "estrategia": "(texto con el plan de juego recomendado)",
            "cambio_sugerido": "(texto sugiriendo cambiar un pokemon por otro)"
        }}
        """
        
        response = model.generate_content(prompt)
        
        # Limpiamos el texto por si Gemini lo envuelve en bloques de código
        raw_text = response.text.replace("```json", "").replace("```", "").strip()
        analisis_json = json.loads(raw_text)
        
        return jsonify(analisis_json), 200

    except json.JSONDecodeError as e:
        print(f"ERROR DE FORMATO IA: La IA no devolvió un JSON puro. {str(e)}", flush=True)
        return jsonify({"error_type": "ExternalAPIError", "critical": False}), 500
        
    except Exception as e:
        print(f"ERROR DE GOOGLE API: {type(e).__name__} - {str(e)}", flush=True)
        return jsonify({"error_type": "ExternalAPIError", "critical": False}), 500