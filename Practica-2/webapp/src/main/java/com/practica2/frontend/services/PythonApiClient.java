package com.practica2.frontend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class PythonApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PythonApiClient.class);
    private static final String ERROR_KEY = "error";

    // Leemos la variable del application.properties
    @Value("${api.python.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PythonApiClient() {
        // Configuramos el tiempo de espera (Timeout) para dar margen a la IA
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5 segundos para establecer conexión con Python
        factory.setReadTimeout(60000);    // 60 segundos de paciencia para recibir el análisis de Gemini
        
        this.restTemplate = new RestTemplate(factory);
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

        String url = apiUrl + "/api/pokemon/" + nombrePokemon.toLowerCase();

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

    @SuppressWarnings("unchecked")
    public Map<String, Object> analizarEquipo(List<String> equipo) {
        Map<String, Object> respuestaError = new HashMap<>();

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("equipo", equipo);

            String endpoint = apiUrl + "/api/team-analysis";
            return restTemplate.postForObject(endpoint, requestBody, Map.class);

        } catch (HttpStatusCodeException httpException) {
            logger.error("Error HTTP del API Python en análisis: {}", httpException.getStatusCode());
            try {
                String responseBody = httpException.getResponseBodyAsString();
                Map<String, Object> errorJson = objectMapper.readValue(responseBody, Map.class);
                
                String tipoError = (String) errorJson.get("error_type");
                Boolean esCritico = (Boolean) errorJson.get("critical");

                if (Boolean.TRUE.equals(esCritico)) {
                    respuestaError.put(ERROR_KEY, "El sistema de análisis central ha fallado crítico.");
                } else if ("ExternalAPIError".equals(tipoError)) {
                    respuestaError.put(ERROR_KEY, "El Profesor Oak (IA) está ocupado o no responde en este momento.");
                } else {
                    respuestaError.put(ERROR_KEY, "Ha ocurrido un error al procesar el equipo.");
                }
            } catch (Exception parseException) {
                logger.error("Fallo al traducir el JSON de error", parseException);
                respuestaError.put(ERROR_KEY, "Error de comunicación con el Centro Pokémon central.");
            }
            return respuestaError;

        } catch (ResourceAccessException networkException) {
            logger.error("Error de Red o Timeout", networkException);
            respuestaError.put(ERROR_KEY, "No hay conexión con el servidor de análisis o se superó el límite de tiempo.");
            return respuestaError;

        } catch (Exception genericException) {
            logger.error("Error grave en el cliente", genericException);
            respuestaError.put(ERROR_KEY, "Error catastrófico en el sistema de análisis.");
            return respuestaError;
        }
    }
}