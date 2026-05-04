package com.practica2.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PythonApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PythonApiClient.class);
    private static final String ERROR_KEY = "error";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PythonApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPokemon(String nombrePokemon) {
        Map<String, Object> respuestaError = new HashMap<>();

        // Filtro Anti-Hackers
        if (nombrePokemon == null || !nombrePokemon.matches("^[a-zA-Z0-9-]+$")) {
            respuestaError.put(ERROR_KEY, "¡Un Unown bloquea el camino! Por favor, usa solo letras y números.");
            return respuestaError;
        }

        String url = "http://localhost:5000/api/pokemon/" + nombrePokemon.toLowerCase();

        try {
            return restTemplate.getForObject(url, Map.class);

        } catch (HttpStatusCodeException e) {

            // Límite de Peticiones
            if (e.getStatusCode().value() == 429) {
                respuestaError.put(ERROR_KEY,
                        "Límite de consultas superado. Has saturado la red del Centro Pokémon. Por favor, haz las búsquedas más despacio.");
                return respuestaError;
            }

            // Pokémon no existe
            if (e.getStatusCode().value() == 404) {
                respuestaError.put(ERROR_KEY,
                        "La Pokédex está confundida... Ese Pokémon no existe. ¿Seguro que no es un Digimon?");
                return respuestaError;
            }

            try {
                Map<String, Object> errorJson = objectMapper.readValue(e.getResponseBodyAsString(), Map.class);
                String tipoError = (String) errorJson.get("error_type");
                boolean esCritico = (Boolean) errorJson.get("critical");

                if (esCritico) {
                    respuestaError.put(ERROR_KEY,
                            "Un Porygon se ha corrompido en el sistema. Contacte con el administrador.");
                } else if ("ExternalAPIError".equals(tipoError)) {
                    respuestaError.put(ERROR_KEY,
                            "La conexión con el Profesor Oak ha fallado. La PokeAPI no responde.");
                } else {
                    respuestaError.put(ERROR_KEY, "Ha ocurrido un error misterioso al procesar al Pokémon.");
                }
            } catch (Exception parseException) {
                logger.error("Fallo al traducir el JSON de error", parseException);
                respuestaError.put(ERROR_KEY, "Error de comunicación con el Centro Pokémon central.");
            }
            return respuestaError;

        } catch (ResourceAccessException networkException) {
            logger.error("Error de Red o Timeout", networkException);
            respuestaError.put(ERROR_KEY,
                    "El Team Rocket ha cortado los cables. No hay conexión con el servidor.");
            return respuestaError;

        } catch (Exception genericException) {
            logger.error("Error grave en el cliente", genericException);
            respuestaError.put(ERROR_KEY, "El servidor usó Autodestrucción. Error catastrófico.");
            return respuestaError;
        }
    }
}