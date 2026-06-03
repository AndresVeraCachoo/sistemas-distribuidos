package com.practica2.frontend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.client.RestClientException;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERROR_KEY = "error";

    // ====================================================================
    // ERRORES ESPECÍFICOS DE LA API DE PYTHON (Mantienen al usuario en index)
    // ====================================================================

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientException(HttpClientErrorException ex, Model model) {
        // FIX SONAR: Ahora usamos 'ex' para imprimir el mensaje real en el log
        logger.warn("El usuario buscó un Pokémon que no existe: {}", ex.getMessage());
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, "El Pokémon introducido no existe.");
        model.addAttribute("pokemon", errorMap);
        return "index"; 
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public String handleHttpServerException(HttpServerErrorException ex, Model model) {
        logger.error("Error interno en el backend de Python (500/503)", ex);
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, "Servicio temporalmente inestable (Error interno de Base de Datos de Python o PokéAPI).");
        model.addAttribute("pokemon", errorMap);
        return "index"; 
    }

    // ====================================================================
    // MANEJADORES GLOBALES (Redirigen a error.html)
    // ====================================================================

    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseErrors(DataAccessException ex, Model model) {
        logger.error("¡ALERTA! La base de datos local está caída", ex);
        model.addAttribute(ERROR_KEY,
                "Error Crítico: Un Snorlax salvaje se ha dormido sobre la base de datos. Sistema bloqueado.");
        return ERROR_KEY;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFoundError(NoResourceFoundException ex, Model model) {
        logger.warn("El usuario intentó acceder a una ruta inexistente: {}", ex.getResourcePath());
        model.addAttribute(ERROR_KEY, "Error 404: ¡Te has perdido en la hierba alta! Esa ruta no existe.");
        return ERROR_KEY;
    }

    @ExceptionHandler(RestClientException.class)
    public String handlePythonConnectionError(RestClientException ex, Model model) {
        logger.error("¡Fallo de comunicación con la API de Python!", ex);
        model.addAttribute(ERROR_KEY,
                "Error de red: La Pokédex de Python no responde. ¿Quizás el Team Rocket cortó los cables?");
        return ERROR_KEY;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        // FIX SONAR: Ahora usamos 'ex' en el log
        logger.warn("Intento de acceso no autorizado detectado: {}", ex.getMessage());
        model.addAttribute(ERROR_KEY,
                "Alto ahí: No tienes las medallas de gimnasio suficientes (no has iniciado sesión) para entrar aquí.");
        return ERROR_KEY;
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericErrors(Exception ex, Model model) {
        logger.error("Error interno no controlado en el servidor", ex);
        model.addAttribute(ERROR_KEY,
                "Error 500: El servidor usó Confusión... ¡Está tan confuso que se hirió a sí mismo!");
        return ERROR_KEY;
    }
}