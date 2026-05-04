package com.practica2.frontend.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    // Sonar Fix: Extraer "error" a una constante para evitar duplicados
    private static final String ERROR_VIEW = "error";

    private GlobalExceptionHandler exceptionHandler;
    private Model model;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        // Usamos un modelo real de Spring para comprobar que se le añaden cosas
        model = new ConcurrentModel();
    }

    @Test
    void testHandleDatabaseErrors() {
        // Simulamos un error de base de datos
        DataRetrievalFailureException ex = new DataRetrievalFailureException("Base de datos muerta");

        String vista = exceptionHandler.handleDatabaseErrors(ex, model);

        // Comprobamos que nos redirige a la vista "error" y nos pasa el mensaje
        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleNotFoundError() {
        // Simulamos un 404
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/ruta-falsa", null);

        String vista = exceptionHandler.handleNotFoundError(ex, model);

        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandlePythonConnectionError() {
        // Simulamos caída de Python
        RestClientException ex = new RestClientException("Fallo de red");

        String vista = exceptionHandler.handlePythonConnectionError(ex, model);

        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleAccessDenied() {
        // Simulamos intento de entrar sin login
        AccessDeniedException ex = new AccessDeniedException("No tienes permiso");

        String vista = exceptionHandler.handleAccessDenied(ex, model);

        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }

    @Test
    void testHandleGenericErrors() {
        // Simulamos un error genérico (ej. NullPointerException)
        Exception ex = new Exception("Error raro");

        String vista = exceptionHandler.handleGenericErrors(ex, model);

        assertEquals(ERROR_VIEW, vista);
        assertTrue(model.containsAttribute(ERROR_VIEW));
    }
}