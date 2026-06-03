package com.practica2.frontend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    // Sonar Fix: Centralizamos las constantes para evitar duplicidad de literales
    private static final String ERROR_VIEW = "error";
    private static final String INDEX_VIEW = "index";
    private static final String POKEMON_ATTR = "pokemon";

    private GlobalExceptionHandler exceptionHandler;
    private Model model;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        model = new ConcurrentModel();
    }

    @Test
    void testHandleHttpClientException() {
        // Simulamos que el usuario busca un Pokémon que no existe (Python devuelve 404)
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
        String vista = exceptionHandler.handleHttpClientException(ex, model);
        
        assertEquals(INDEX_VIEW, vista);
        assertTrue(model.containsAttribute(POKEMON_ATTR));
    }

    @Test
    void testHandleHttpServerException() {
        // Simulamos que la BD de Python o la PokeAPI explotan (Python devuelve 500/503)
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");
        String vista = exceptionHandler.handleHttpServerException(ex, model);
        
        assertEquals(INDEX_VIEW, vista);
        assertTrue(model.containsAttribute(POKEMON_ATTR));
    }

    @Test
    void testHandleDatabaseErrors() {
        DataRetrievalFailureException ex = new DataRetrievalFailureException("Base de datos muerta");
        String vista = exceptionHandler.handleDatabaseErrors(ex, model);
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleNotFoundError() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/ruta-falsa", null);
        String vista = exceptionHandler.handleNotFoundError(ex, model);
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandlePythonConnectionError() {
        RestClientException ex = new RestClientException("Fallo de red");
        String vista = exceptionHandler.handlePythonConnectionError(ex, model);
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("No tienes permiso");
        String vista = exceptionHandler.handleAccessDenied(ex, model);
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleGenericErrors() {
        Exception ex = new Exception("Error raro");
        String vista = exceptionHandler.handleGenericErrors(ex, model);
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }
}