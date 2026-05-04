package com.practica2.frontend.services;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimiterService {

    // Guarda los intentos de cada usuario
    private final ConcurrentHashMap<String, Integer> intentos = new ConcurrentHashMap<>();
    // Guarda el momento (milisegundos) en el que fue bloqueado
    private final ConcurrentHashMap<String, Long> bloqueos = new ConcurrentHashMap<>();

    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO_MS = 60000; // 1 minuto de castigo

    public boolean estaBloqueado(String username) {
        if (bloqueos.containsKey(username)) {
            if (System.currentTimeMillis() - bloqueos.get(username) < TIEMPO_BLOQUEO_MS) {
                return true; // Sigue castigado
            } else {
                // Ya pasó el minuto, le perdonamos
                bloqueos.remove(username);
                intentos.remove(username);
            }
        }
        return false;
    }

    // Spring Security nos avisa automáticamente cuando alguien falla la contraseña
    @EventListener
    public void onLoginFallido(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        int contador = intentos.getOrDefault(username, 0) + 1;
        intentos.put(username, contador);

        if (contador >= MAX_INTENTOS) {
            bloqueos.put(username, System.currentTimeMillis()); // Bloqueado
        }
    }

    // Spring Security nos avisa si hace un login correcto
    @EventListener
    public void onLoginExitoso(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        intentos.remove(username);
        bloqueos.remove(username);
    }
}