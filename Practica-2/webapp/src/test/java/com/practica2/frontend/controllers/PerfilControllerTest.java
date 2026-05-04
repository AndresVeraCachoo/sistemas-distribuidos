package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private BusquedaRepository busquedaRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "ash")
    void testPerfilView() throws Exception {
        Usuario mockUser = new Usuario();
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(get("/perfil"))
                .andExpect(status().isOk())
                .andExpect(view().name("perfil"))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @WithMockUser(username = "ash")
    void testCambiarPassword_ActualIncorrecta() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setPassword("pass_codificada");
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("malapass", "pass_codificada")).thenReturn(false);

        mockMvc.perform(post("/perfil/password").with(csrf())
                .param("actual", "malapass")
                .param("nueva", "1234")
                .param("confirmacion", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil?error=La contraseña actual es incorrecta"));
    }

    @Test
    @WithMockUser(username = "ash")
    void testCambiarPassword_NuevasNoCoinciden() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setPassword("pass_codificada");
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("bienpass", "pass_codificada")).thenReturn(true);

        mockMvc.perform(post("/perfil/password").with(csrf())
                .param("actual", "bienpass")
                .param("nueva", "1234")
                .param("confirmacion", "5678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil?error=Las contraseñas nuevas no coinciden"));
    }

    @Test
    @WithMockUser(username = "ash")
    void testCambiarAvatar_Exito() throws Exception {
        Usuario mockUser = new Usuario();
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/perfil/avatar").with(csrf())
                .param("avatarUrl", "http://mi-avatar.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/perfil?exito=Avatar actualizado correctamente"));

        // ARREGLO: Verificamos que se haya guardado al menos una vez (ignora el del
        // DataInitializer)
        verify(usuarioRepository, atLeastOnce()).save(any(Usuario.class));
    }

    @Test
    @WithMockUser(username = "ash")
    void testEliminarCuenta() throws Exception {
        Usuario mockUser = new Usuario();
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(mockUser)).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/perfil/eliminar").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));

        // Estábamos pidiendo verificar save(), cuando debíamos pedir delete()
        verify(usuarioRepository).delete(mockUser);
    }
}