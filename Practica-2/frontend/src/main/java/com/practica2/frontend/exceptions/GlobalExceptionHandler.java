package com.practica2.frontend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.client.RestClientException;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // CONSTANTE PARA SONARCLOUD
    private static final String ERROR_KEY = "error";

    // ERROR DE BASE DE DATOS APAGADA
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseErrors(DataAccessException ex, Model model) {
        logger.error("¡ALERTA! La base de datos local está caída", ex);
        model.addAttribute(ERROR_KEY,
                "Error Crítico: Un Snorlax salvaje se ha dormido sobre la base de datos. Sistema bloqueado.");
        return ERROR_KEY;
    }

    // ERROR 404 (RUTA NO ENCONTRADA)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFoundError(NoResourceFoundException ex, Model model) {
        logger.warn("El usuario intentó acceder a una ruta inexistente: {}", ex.getResourcePath());
        model.addAttribute(ERROR_KEY, "Error 404: ¡Te has perdido en la hierba alta! Esa ruta no existe.");
        return ERROR_KEY;
    }

    // ERROR AL COMUNICARSE CON PYTHON (MICROSERVICIO CAÍDO)
    @ExceptionHandler(RestClientException.class)
    public String handlePythonConnectionError(RestClientException ex, Model model) {
        logger.error("¡Fallo de comunicación con la API de Python!", ex);
        model.addAttribute(ERROR_KEY,
                "Error de red: La Pokédex de Python no responde. ¿Quizás el Team Rocket cortó los cables?");
        return ERROR_KEY;
    }

    // ERROR DE ACCESO DENEGADO (INTENTO DE ENTRAR SIN LOGIN)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        logger.warn("Intento de acceso no autorizado detectado.");
        model.addAttribute(ERROR_KEY,
                "Alto ahí: No tienes las medallas de gimnasio suficientes (no has iniciado sesión) para entrar aquí.");
        return ERROR_KEY;
    }

    // CUALQUIER OTRO ERROR
    @ExceptionHandler(Exception.class)
    public String handleGenericErrors(Exception ex, Model model) {
        logger.error("Error interno no controlado en el servidor", ex);
        model.addAttribute(ERROR_KEY,
                "Error 500: El servidor usó Confusión... ¡Está tan confuso que se hirió a sí mismo!");
        return ERROR_KEY;
    }
}