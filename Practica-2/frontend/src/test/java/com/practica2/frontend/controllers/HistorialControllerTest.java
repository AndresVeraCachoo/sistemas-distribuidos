package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HistorialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BusquedaRepository busquedaRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = "ash")
    void testHistorialView_ConDatos() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setUsername("ash");

        Busqueda b1 = new Busqueda();
        b1.setPokemonName("pikachu");
        b1.setFechaBusqueda(LocalDateTime.now());

        Busqueda b2 = new Busqueda();
        b2.setPokemonName("charmander");
        b2.setFechaBusqueda(LocalDateTime.now().minusDays(1));

        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(mockUser)).thenReturn(List.of(b1, b2));
        when(busquedaRepository.countByUsuario(mockUser)).thenReturn(2L);

        mockMvc.perform(get("/historial"))
                .andExpect(status().isOk())
                .andExpect(view().name("historial"))
                .andExpect(model().attributeExists("usuario"))
                .andExpect(model().attributeExists("historial"))
                .andExpect(model().attribute("totalBusquedas", 2L))
                .andExpect(model().attribute("pokemonUnicos", 2L))
                .andExpect(model().attribute("ultimaBusqueda", "pikachu"));
    }
}