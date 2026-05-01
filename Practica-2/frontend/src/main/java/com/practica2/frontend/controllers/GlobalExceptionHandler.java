package com.practica2.frontend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ERROR DE BASE DE DATOS APAGADA
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseErrors(DataAccessException ex, Model model) {
        logger.error("¡ALERTA! La base de datos local está caída", ex);
        model.addAttribute("error",
                "Error Crítico: Un Snorlax salvaje se ha dormido sobre la base de datos. Sistema bloqueado.");
        return "error";
    }

    // ERROR 404 (RUTA NO ENCONTRADA)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFoundError(NoResourceFoundException ex, Model model) {
        logger.warn("El usuario intentó acceder a una ruta inexistente: " + ex.getResourcePath());
        model.addAttribute("error", "Error 404: ¡Te has perdido en la hierba alta! Esa ruta no existe.");
        return "error";
    }

    // CUALQUIER OTRO ERROR
    @ExceptionHandler(Exception.class)
    public String handleGenericErrors(Exception ex, Model model) {
        logger.error("Error interno no controlado en el servidor", ex);
        model.addAttribute("error",
                "Error 500: El servidor usó Confusión... ¡Está tan confuso que se hirió a sí mismo!");
        return "error";
    }
}