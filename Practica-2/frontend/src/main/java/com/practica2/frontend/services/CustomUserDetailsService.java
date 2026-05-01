package com.practica2.frontend.services;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.springframework.security.authentication.LockedException; // <-- IMPORTANTE
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final LoginRateLimiterService loginRateLimiterService;

    // Inyectamos ambos repositorios
    public CustomUserDetailsService(UsuarioRepository usuarioRepository,
            LoginRateLimiterService loginRateLimiterService) {
        this.usuarioRepository = usuarioRepository;
        this.loginRateLimiterService = loginRateLimiterService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 1. COMPROBACIÓN DE FUERZA BRUTA (Antes de tocar la BD)
        if (loginRateLimiterService.estaBloqueado(username)) {
            throw new LockedException("¡Demasiados intentos! Cuenta bloqueada por 1 minuto por seguridad.");
        }

        // 2. BÚSQUEDA NORMAL EN BASE DE DATOS
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles("USER")
                .build();
    }
}