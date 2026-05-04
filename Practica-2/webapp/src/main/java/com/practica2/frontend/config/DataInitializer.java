package com.practica2.frontend.config;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final BusquedaRepository busquedaRepository; // AÑADIDO
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.password}")
    private String adminPassword;

    // AÑADIDO BusquedaRepository AL CONSTRUCTOR
    public DataInitializer(UsuarioRepository usuarioRepository, BusquedaRepository busquedaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.busquedaRepository = busquedaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // Limpiamos al admin antiguo si existe
        usuarioRepository.findByUsername("admin").ifPresent(admin -> {
            // Borramos sus búsquedas primero para que PostgreSQL no se queje
            List<Busqueda> historial = busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(admin);
            busquedaRepository.deleteAll(historial);

            // 2. Ahora sí podemos borrar al usuario
            usuarioRepository.delete(admin);
        });

        // Creamos al nuevo entrenador de pruebas
        if (usuarioRepository.findByUsername("ash").isEmpty()) {
            Usuario ash = new Usuario();
            ash.setUsername("ash");
            ash.setPassword(passwordEncoder.encode(adminPassword));
            ash.setEmail("ash@entrenador.com");
            ash.setNombre("Ash Ketchum");
            ash.setAvatarUrl("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/poke-ball.png");
            ash.setEsProtegido(true);
            usuarioRepository.save(ash);
        }
    }
}