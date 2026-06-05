package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import com.practica2.frontend.services.EmailPublisherService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailPublisherService emailPublisherService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, EmailPublisherService emailPublisherService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailPublisherService = emailPublisherService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String username, 
                                   @RequestParam String email, 
                                   @RequestParam String password,
                                   @RequestParam String nombre) {
        
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/registro?error=El nombre de usuario ya existe";
        }
        
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return "redirect:/registro?error=El correo electrónico ya está registrado";
        }

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setEmail(email);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setNombre(nombre);
        usuarioRepository.save(nuevo);

        // Disparar mensaje a RabbitMQ
        emailPublisherService.publishWelcomeEmail(nuevo.getEmail(), nuevo.getNombre());
        
        return "redirect:/login?registrado";
    }
}