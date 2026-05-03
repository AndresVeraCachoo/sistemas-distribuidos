package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import com.practica2.frontend.services.PythonApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PythonApiClient pythonApiClient;

    @MockitoBean
    private BusquedaRepository busquedaRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void testIndex_SinParametros_NoLogueado() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "ash")
    void testIndex_ConBusqueda_Logueado() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setUsername("ash");

        Map<String, Object> mockPokemon = Map.of(
                "name", "pikachu",
                "id", 25,
                "sprite", "http://sprite.png");

        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(mockUser)).thenReturn(Collections.emptyList());
        when(pythonApiClient.getPokemon("pikachu")).thenReturn(mockPokemon);

        mockMvc.perform(get("/").param("name", "pikachu"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pokemon"))
                .andExpect(model().attributeExists("sugerencias"));

        verify(busquedaRepository).save(any(Busqueda.class));
    }

    @Test
    @WithMockUser(username = "ash")
    void testIndex_ConBusquedaYComparacion() throws Exception {
        Usuario mockUser = new Usuario();

        Map<String, Object> mockPokemon1 = Map.of("name", "pikachu", "id", 25, "sprite", "url1");
        Map<String, Object> mockPokemon2 = Map.of("name", "charmander", "id", 4, "sprite", "url2");

        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(mockUser));
        when(pythonApiClient.getPokemon("pikachu")).thenReturn(mockPokemon1);
        when(pythonApiClient.getPokemon("charmander")).thenReturn(mockPokemon2);

        mockMvc.perform(get("/")
                .param("name", "pikachu")
                .param("compare", "charmander"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pokemon"))
                .andExpect(model().attributeExists("pokemon2"));
    }
}