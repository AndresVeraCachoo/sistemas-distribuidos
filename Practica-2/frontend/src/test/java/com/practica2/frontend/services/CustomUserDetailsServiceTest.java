package com.practica2.frontend.services;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LoginRateLimiterService loginRateLimiterService;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_Exito() {
        Usuario mockUser = new Usuario();
        mockUser.setUsername("ash");
        mockUser.setPassword("1234");
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));

        UserDetails result = userDetailsService.loadUserByUsername("ash");

        assertNotNull(result);
        assertEquals("ash", result.getUsername());
    }

    @Test
    void loadUserByUsername_NoExiste() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("fantasma");
        });
    }
}