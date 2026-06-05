package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.UsuarioRepository;
import com.practica2.frontend.services.PythonApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class EquipoController {

    private final PythonApiClient pythonApiClient;
    private final UsuarioRepository usuarioRepository;

    public EquipoController(PythonApiClient pythonApiClient, UsuarioRepository usuarioRepository) {
        this.pythonApiClient = pythonApiClient;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/equipo")
    public String mostrarCreadorEquipo(Model model, Principal principal) {
        if (principal != null) {
            Usuario user = usuarioRepository.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("usuario", user);
        }
        return "equipo";
    }

    @PostMapping("/equipo/analizar")
    public String analizarEquipo(@RequestParam List<String> pokemon, Model model, Principal principal) {
        if (principal != null) {
            Usuario user = usuarioRepository.findByUsername(principal.getName()).orElse(null);
            model.addAttribute("usuario", user);
        }

        Map<String, Object> analisis = pythonApiClient.analizarEquipo(pokemon);
        model.addAttribute("analisis", analisis);
        model.addAttribute("equipoEnviado", pokemon);
        
        return "equipo";
    }
}