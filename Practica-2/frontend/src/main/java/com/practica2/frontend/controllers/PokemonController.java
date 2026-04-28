package com.practica2.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PokemonController {

    @GetMapping("/")
    public String index(Model model) {
        // Placeholder para el nombre del usuario logueado que vendrá de la sesión
        model.addAttribute("username", "Usuario Invitado");
        return "index";
    }
}