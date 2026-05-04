package com.practica2.frontend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class LoginRateLimiterServiceTest {

    private LoginRateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new LoginRateLimiterService();
    }

    @Test
    void testBloqueoYReinicio() {
        String username = "ash_ketchum";

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(username);
        when(auth.getName()).thenReturn(username);

        // AQUÍ ESTÁ EL ARREGLO: Pasamos un BadCredentialsException real
        AuthenticationFailureBadCredentialsEvent falloEvent = new AuthenticationFailureBadCredentialsEvent(auth,
                new BadCredentialsException("Clave mal"));
        AuthenticationSuccessEvent exitoEvent = new AuthenticationSuccessEvent(auth);

        for (int i = 0; i < 5; i++) {
            rateLimiterService.onLoginFallido(falloEvent);
        }

        assertTrue(rateLimiterService.estaBloqueado(username));

        rateLimiterService.onLoginExitoso(exitoEvent);
        assertFalse(rateLimiterService.estaBloqueado(username));
    }
}