package com.practica2.frontend.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class BusquedaTest {

    @Test
    void testBusquedaGettersAndSetters() {
        Busqueda busqueda = new Busqueda();
        Usuario usuario = new Usuario();
        usuario.setUsername("ash");
        LocalDateTime ahora = LocalDateTime.now();

        busqueda.setId(10L);
        busqueda.setPokemonName("pikachu");
        busqueda.setPokemonId(25);
        busqueda.setSpriteUrl("http://sprite.png");
        busqueda.setFechaBusqueda(ahora);
        busqueda.setUsuario(usuario);

        assertEquals(10L, busqueda.getId());
        assertEquals("pikachu", busqueda.getPokemonName());
        assertEquals(25, busqueda.getPokemonId());
        assertEquals("http://sprite.png", busqueda.getSpriteUrl());
        assertEquals(ahora, busqueda.getFechaBusqueda());
        assertEquals(usuario, busqueda.getUsuario());
        assertEquals("ash", busqueda.getUsuario().getUsername());
    }
}