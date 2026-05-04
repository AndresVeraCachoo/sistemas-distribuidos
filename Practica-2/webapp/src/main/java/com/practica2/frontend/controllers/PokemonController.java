package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import com.practica2.frontend.services.PythonApiClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class PokemonController {

    private final PythonApiClient pythonApiClient;
    private final BusquedaRepository busquedaRepository;
    private final UsuarioRepository usuarioRepository;

    public PokemonController(PythonApiClient pythonApiClient, BusquedaRepository busquedaRepository,
            UsuarioRepository usuarioRepository) {
        this.pythonApiClient = pythonApiClient;
        this.busquedaRepository = busquedaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String name,
            @RequestParam(required = false) String compare,
            Model model, Principal principal) {

        Usuario user = null;
        if (principal != null) {
            user = usuarioRepository.findByUsername(principal.getName()).orElse(null);

            // --- NUEVO: Extraer últimas 5 búsquedas únicas para el autocompletado ---
            List<Busqueda> historial = busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(user);
            List<String> sugerencias = historial.stream()
                    .map(Busqueda::getPokemonName)
                    .distinct()
                    .limit(5)
                    .toList();
            model.addAttribute("sugerencias", sugerencias);
        }
        model.addAttribute("usuario", user);

        // 1. Búsqueda del Primer Pokémon
        if (name != null && !name.isEmpty()) {
            Map<String, Object> pokemon = pythonApiClient.getPokemon(name);
            model.addAttribute("pokemon", pokemon);

            // Guardar en el historial
            if (pokemon != null && !pokemon.containsKey("error") && user != null
                    && (compare == null || compare.isEmpty())) {
                Busqueda b = new Busqueda();
                b.setPokemonName((String) pokemon.get("name"));
                b.setPokemonId((Integer) pokemon.get("id"));
                b.setSpriteUrl((String) pokemon.get("sprite"));
                b.setFechaBusqueda(LocalDateTime.now());
                b.setUsuario(user);
                busquedaRepository.save(b);
            }

            // 2. Búsqueda del Segundo Pokémon (Modo Comparación)
            if (compare != null && !compare.isEmpty()) {
                Map<String, Object> pokemon2 = pythonApiClient.getPokemon(compare);
                model.addAttribute("pokemon2", pokemon2);
            }
        }
        return "index";
    }
}