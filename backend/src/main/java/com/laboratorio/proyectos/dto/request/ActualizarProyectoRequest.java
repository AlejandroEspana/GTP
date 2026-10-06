package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.EstadoProyecto;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ActualizarProyectoRequest(
        @NotBlank(message = "El nombre del proyecto es obligatorio")
        String nombre,

        String descripcion,
        EstadoProyecto estado,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {}
