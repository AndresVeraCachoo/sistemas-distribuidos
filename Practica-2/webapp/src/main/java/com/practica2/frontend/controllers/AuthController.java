package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
    public String registrarUsuario(@RequestParam String username, @RequestParam String password,
            @RequestParam String nombre) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/registro?error=El nombre de usuario ya existe";
        }
        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setNombre(nombre);
        nuevo.setEmail(username + "@entrenador.com"); // Email autogenerado para que la BD no explote
        usuarioRepository.save(nuevo);
        return "redirect:/login?registrado";
    }
}