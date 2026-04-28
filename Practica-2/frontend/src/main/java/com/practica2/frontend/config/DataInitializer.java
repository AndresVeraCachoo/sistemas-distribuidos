package com.practica2.frontend.config;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            // Guardamos la contraseña cifrada: "admin123"
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@practica.com");
            admin.setNombre("Administrador de Pruebas");
            admin.setEsProtegido(true); // Marcado como no borrable
            usuarioRepository.save(admin);
        }
    }
}