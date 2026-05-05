package com.practica2.frontend.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    void testValoresPorDefecto() {
        // Arrange
        Usuario usuario = new Usuario();

        // Act & Assert
        assertFalse(usuario.isEsProtegido(), "Un usuario nuevo no debería estar protegido por defecto");
        assertEquals("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png", 
                     usuario.getAvatarUrl(), 
                     "El avatar por defecto debe ser la Pokéball");
        assertNull(usuario.getId(), "El ID debe ser nulo antes de persistir en base de datos");
    }

    @Test
    void testSettersYGetters() {
        // Arrange
        Usuario usuario = new Usuario();

        // Act
        usuario.setId(1L);
        usuario.setUsername("AshKetchum");
        usuario.setPassword("Pikachu123");
        usuario.setEmail("ash@pueblopaleta.com");
        usuario.setNombre("Ash");
        usuario.setEsProtegido(true);
        usuario.setAvatarUrl("https://mi-imagen.com/ash.png");

        // Assert
        assertEquals(1L, usuario.getId());
        assertEquals("AshKetchum", usuario.getUsername());
        assertEquals("Pikachu123", usuario.getPassword());
        assertEquals("ash@pueblopaleta.com", usuario.getEmail());
        assertEquals("Ash", usuario.getNombre());
        assertTrue(usuario.isEsProtegido());
        assertEquals("https://mi-imagen.com/ash.png", usuario.getAvatarUrl());
    }

    @Test
    void testLombokEqualsYHashCode() {
        // Arrange
        Usuario usuario1 = new Usuario();
        usuario1.setUsername("Misty");
        usuario1.setEmail("misty@gimnasioceleste.com");

        Usuario usuario2 = new Usuario();
        usuario2.setUsername("Misty");
        usuario2.setEmail("misty@gimnasioceleste.com");

        Usuario usuario3 = new Usuario();
        usuario3.setUsername("Brock");
        usuario3.setEmail("brock@gimnasioplateado.com");

        // Act & Assert
        assertEquals(usuario1, usuario2, "Dos usuarios con los mismos datos deberían ser iguales gracias a @Data");
        assertEquals(usuario1.hashCode(), usuario2.hashCode(), "El hashCode debería coincidir");
        assertNotEquals(usuario1, usuario3, "Usuarios con distintos datos no deben ser iguales");
    }
}