package com.practica2.frontend.config;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private BusquedaRepository busquedaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        // Simulamos la contraseña del application.properties
        ReflectionTestUtils.setField(dataInitializer, "adminPassword", "admin123");
    }

    @Test
    void run_AdminExisteYAshNoExiste() throws Exception {
        // 1. Arrange: Simulamos que el admin existe y tiene una búsqueda
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        List<Busqueda> adminBusquedas = List.of(new Busqueda());

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(admin)).thenReturn(adminBusquedas);

        // Simulamos que Ash NO existe aún
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        // 2. Act: Ejecutamos el inicializador
        dataInitializer.run();

        // 3. Assert: Verificamos que se borraron las búsquedas y el admin
        verify(busquedaRepository).deleteAll(adminBusquedas);
        verify(usuarioRepository).delete(admin);

        // Verificamos que se guardó a Ash
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void run_AdminNoExisteYAshYaExiste() throws Exception {
        // 1. Arrange: Simulamos que ya no hay admin y Ash ya está creado
        Usuario ash = new Usuario();
        ash.setUsername("ash");

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername("ash")).thenReturn(Optional.of(ash));

        // 2. Act
        dataInitializer.run();

        // 3. Assert: Comprobamos que NO se llamó a ningún método de borrado ni de
        // guardado
        verify(busquedaRepository, never()).deleteAll(any());
        verify(usuarioRepository, never()).delete(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}