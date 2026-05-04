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
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class PokemonControllerTest {

    // Sonar Fix: Extraer los literales repetidos a constantes
    private static final String PIKACHU_NAME = "pikachu";
    private static final String ASH_USERNAME = "ash";
    private static final String CHARMANDER_NAME = "charmander";
    private static final String SPRITE_IMG = "sprite";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PythonApiClient pythonApiClient;

    @MockitoBean
    private BusquedaRepository busquedaRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    // Sonar Fix: camelCase
    void testIndexSinParametrosNoLogueado() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = ASH_USERNAME)
    // Sonar Fix: camelCase
    void testIndexConBusquedaLogueado() throws Exception {
        Usuario mockUser = new Usuario();
        mockUser.setUsername(ASH_USERNAME);

        Map<String, Object> mockPokemon = Map.of(
                "name", PIKACHU_NAME,
                "id", 25,
                "SPRITE_IMG", "http://sprite.png");

        when(usuarioRepository.findByUsername(ASH_USERNAME)).thenReturn(Optional.of(mockUser));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(mockUser)).thenReturn(Collections.emptyList());
        when(pythonApiClient.getPokemon(PIKACHU_NAME)).thenReturn(mockPokemon);

        mockMvc.perform(get("/").param("name", PIKACHU_NAME))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pokemon"))
                .andExpect(model().attributeExists("sugerencias"));

        verify(busquedaRepository).save(any(Busqueda.class));
    }

    @Test
    @WithMockUser(username = ASH_USERNAME)
    // Sonar Fix: camelCase
    void testIndexConBusquedaYComparacion() throws Exception {
        Usuario mockUser = new Usuario();

        Map<String, Object> mockPokemon1 = Map.of("name", PIKACHU_NAME, "id", 25, "SPRITE_IMG", "url1");
        Map<String, Object> mockPokemon2 = Map.of("name", CHARMANDER_NAME, "id", 4, "SPRITE_IMG", "url2");

        when(usuarioRepository.findByUsername(ASH_USERNAME)).thenReturn(Optional.of(mockUser));
        when(pythonApiClient.getPokemon(PIKACHU_NAME)).thenReturn(mockPokemon1);
        when(pythonApiClient.getPokemon(CHARMANDER_NAME)).thenReturn(mockPokemon2);

        mockMvc.perform(get("/")
                .param("name", PIKACHU_NAME)
                .param("compare", CHARMANDER_NAME))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("pokemon"))
                .andExpect(model().attributeExists("pokemon2"));
    }
}