package com.practica2.frontend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PythonApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PythonApiClient pythonApiClient;

    @Test
    void getPokemon_Exito() {
        // Arrange
        Map<String, Object> mockResponse = Map.of("name", "pikachu", "id", 25);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        // Act
        Map<String, Object> result = pythonApiClient.getPokemon("pikachu");

        // Assert
        assertNotNull(result);
        assertEquals("pikachu", result.get("name"));
        assertFalse(result.containsKey("error"));
    }

    @Test
    void getPokemon_FiltroAntiHackers() {
        // Act: Intentamos buscar con caracteres no permitidos
        Map<String, Object> result = pythonApiClient.getPokemon("pika_chu!");

        // Assert: Comprobamos que el filtro lo detiene antes de llamar a RestTemplate
        assertTrue(result.containsKey("error"));
        assertEquals("¡Un Unown bloquea el camino! Por favor, usa solo letras y números.", result.get("error"));
    }

    @Test
    void getPokemon_ErrorDeRed_TeamRocket() {
        // Arrange: Simulamos que la conexión falla físicamente
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("Timeout"));

        // Act
        Map<String, Object> result = pythonApiClient.getPokemon("bulbasaur");

        // Assert: Verificamos el mensaje personalizado de tu catch
        assertTrue(result.containsKey("error"));
        assertEquals("El Team Rocket ha cortado los cables. No hay conexión con el servidor.", result.get("error"));
    }

    @Test
    void getPokemon_ErrorGenerico_Autodestruccion() {
        // Arrange: Simulamos cualquier otro error inesperado
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Crash"));

        // Act
        Map<String, Object> result = pythonApiClient.getPokemon("mewtwo");

        // Assert
        assertTrue(result.containsKey("error"));
        assertEquals("El servidor usó Autodestrucción. Error catastrófico.", result.get("error"));
    }
}