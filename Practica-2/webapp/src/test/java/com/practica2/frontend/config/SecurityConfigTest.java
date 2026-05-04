package com.practica2.frontend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void contextLoadsAndBeansAreCreated() {
        // Comprobamos que el encriptador de contraseñas existe y es del tipo correcto
        assertNotNull(passwordEncoder, "El PasswordEncoder debería haberse creado");
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder, "Debería ser un BCryptPasswordEncoder");

        // Comprobamos que la cadena de filtros de seguridad se ha cargado correctamente
        assertNotNull(securityFilterChain, "El SecurityFilterChain debería haberse creado");
    }
}