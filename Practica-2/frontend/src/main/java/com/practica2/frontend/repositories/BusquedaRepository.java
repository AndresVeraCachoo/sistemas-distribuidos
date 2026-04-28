package com.practica2.frontend.repositories;

import com.practica2.frontend.models.Busqueda;
import com.practica2.frontend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusquedaRepository extends JpaRepository<Busqueda, Long> {
    // Obtenemos las búsquedas de un usuario ordenadas por las más recientes
    List<Busqueda> findByUsuarioOrderByFechaBusquedaDesc(Usuario usuario);

    // Contamos cuántas búsquedas ha hecho el usuario para las estadísticas
    long countByUsuario(Usuario usuario);
}