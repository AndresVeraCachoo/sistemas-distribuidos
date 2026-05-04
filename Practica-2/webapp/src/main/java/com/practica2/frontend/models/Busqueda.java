package com.practica2.frontend.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_busquedas")
@Data
public class Busqueda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pokemonName;
    private Integer pokemonId;
    private String spriteUrl;
    private LocalDateTime fechaBusqueda;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}