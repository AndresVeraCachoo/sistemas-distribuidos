package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testLoginView() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk()).andExpect(view().name("login"));
    }

    @Test
    void testRegistroView() throws Exception {
        mockMvc.perform(get("/registro")).andExpect(status().isOk()).andExpect(view().name("registro"));
    }

    @Test
    void testRegistrarUsuario_YaExiste() throws Exception {
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(new Usuario()));

        mockMvc.perform(post("/registro").with(csrf())
                .param("username", "ash")
                .param("password", "1234")
                .param("nombre", "Ash Ketchum"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registro?error=El nombre de usuario ya existe")); // ARREGLADO
    }

    @Test
    void testRegistrarUsuario_NuevoExito() throws Exception {
        when(usuarioRepository.findByUsername("red")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        mockMvc.perform(post("/registro").with(csrf())
                .param("username", "red")
                .param("password", "1234")
                .param("nombre", "Red"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registrado")); // ARREGLADO

        verify(usuarioRepository).save(any(Usuario.class));
    }
}