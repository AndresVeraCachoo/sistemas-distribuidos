package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HistorialController {

    private final BusquedaRepository busquedaRepository;
    private final UsuarioRepository usuarioRepository;

    public HistorialController(BusquedaRepository busquedaRepository, UsuarioRepository usuarioRepository) {
        this.busquedaRepository = busquedaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/historial")
    public String historial(Model model, Principal principal) {
        Usuario user = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        List<Busqueda> historial = busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(user);

        model.addAttribute("usuario", user);
        model.addAttribute("historial", historial);
        model.addAttribute("totalBusquedas", busquedaRepository.countByUsuario(user));

        long pokemonUnicos = historial.stream().map(Busqueda::getPokemonName).distinct().count();
        model.addAttribute("pokemonUnicos", pokemonUnicos);

        if (!historial.isEmpty()) {
            model.addAttribute("ultimaBusqueda", historial.get(0).getPokemonName());
        }
        return "historial";
    }
}