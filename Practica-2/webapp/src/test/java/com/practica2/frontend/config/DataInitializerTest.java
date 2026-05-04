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

    // SonarCloud Fix: Definir constantes en lugar de repetir literales de texto
    private static final String ADMIN_USERNAME = "admin";
    private static final String ASH_USERNAME = "ash";

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
    void runAdminExisteYAshNoExiste() throws Exception {
        // Arrange: Simulamos que el admin existe y tiene una búsqueda
        Usuario admin = new Usuario();
        admin.setUsername(ADMIN_USERNAME);
        List<Busqueda> adminBusquedas = List.of(new Busqueda());

        when(usuarioRepository.findByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(admin));
        when(busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(admin)).thenReturn(adminBusquedas);

        // Simulamos que Ash NO existe aún
        when(usuarioRepository.findByUsername(ASH_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_pass");

        // Act: Ejecutamos el inicializador
        dataInitializer.run();

        // Assert: Verificamos que se borraron las búsquedas y el admin
        verify(busquedaRepository).deleteAll(adminBusquedas);
        verify(usuarioRepository).delete(admin);

        // Verificamos que se guardó a Ash
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void runAdminNoExisteYAshYaExiste() throws Exception {
        // Arrange: Simulamos que ya no hay admin y Ash ya está creado
        Usuario ash = new Usuario();
        ash.setUsername(ASH_USERNAME);

        when(usuarioRepository.findByUsername(ADMIN_USERNAME)).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername(ASH_USERNAME)).thenReturn(Optional.of(ash));

        dataInitializer.run();

        // Comprobamos que NO se llamó a ningún método de borrado ni de guardado
        verify(busquedaRepository, never()).deleteAll(any());
        verify(usuarioRepository, never()).delete(any());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}