package com.practica2.frontend.controllers;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import com.practica2.frontend.repositories.BusquedaRepository;
import com.practica2.frontend.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final BusquedaRepository busquedaRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioRepository usuarioRepository, BusquedaRepository busquedaRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.busquedaRepository = busquedaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal) {
        Usuario user = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("usuario", user);
        return "perfil";
    }

    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String actual, @RequestParam String nueva,
            @RequestParam String confirmacion, Principal principal) {
        Usuario user = usuarioRepository.findByUsername(principal.getName()).orElseThrow();

        // Verificamos si la contraseña actual coincide con la que hay en la base de
        // datos
        if (!passwordEncoder.matches(actual, user.getPassword())) {
            return "redirect:/perfil?error=La contraseña actual es incorrecta";
        }
        // Verificamos que las contraseñas nuevas coincidan entre sí
        if (!nueva.equals(confirmacion)) {
            return "redirect:/perfil?error=Las contraseñas nuevas no coinciden";
        }

        // Guardamos la nueva contraseña de forma segura
        user.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(user);
        return "redirect:/perfil?exito=Contraseña actualizada correctamente";
    }

    @PostMapping("/perfil/avatar")
    public String cambiarAvatar(@RequestParam String avatarUrl, Principal principal) {
        Usuario user = usuarioRepository.findByUsername(principal.getName()).orElseThrow();
        user.setAvatarUrl(avatarUrl);
        usuarioRepository.save(user);
        return "redirect:/perfil?exito=Avatar actualizado correctamente";
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarCuenta(Principal principal, HttpServletRequest request) {
        Usuario user = usuarioRepository.findByUsername(principal.getName()).orElseThrow();

        List<Busqueda> historial = busquedaRepository.findByUsuarioOrderByFechaBusquedaDesc(user);
        busquedaRepository.deleteAll(historial);

        // Borramos al usuario
        usuarioRepository.delete(user);

        // Forzamos el cierre de sesión
        request.getSession().invalidate();

        return "redirect:/login?logout";
    }
}