package com.practica2.frontend.services;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PythonApiClientTest {

    @Test
    void testTraduccionDeErrorDePython() {
        // Preparamos el RestTemplate falso
        RestTemplate restTemplateFalso = mock(RestTemplate.class);

        // Creamos el cliente pasándole SOLO el RestTemplate (ya creará él su
        // ObjectMapper)
        PythonApiClient cliente = new PythonApiClient(restTemplateFalso);

        // Fabricamos la mentira del error
        HttpStatusCodeException excepcionFalsa = mock(HttpStatusCodeException.class);
        String jsonErrorDePython = "{\"critical\": false, \"error_type\": \"ExternalAPIError\", \"message\": \"Fallo\"}";
        when(excepcionFalsa.getResponseBodyAsString()).thenReturn(jsonErrorDePython);

        when(restTemplateFalso.getForObject(anyString(), eq(Map.class))).thenThrow(excepcionFalsa);

        // Ejecutamos y verificamos
        Map<String, Object> resultado = cliente.getPokemon("pikachu");
        assertEquals("La PokeAPI está cansada. El centro Pokémon no responde ahora mismo.", resultado.get("error"));
    }
}