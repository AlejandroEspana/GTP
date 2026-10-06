package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.EstadoProyecto;
import java.time.LocalDate;
import java.util.UUID;

public record ProyectoResponse(
        UUID id,
        String nombre,
        String descripcion,
        EstadoProyecto estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCreacion,
        long totalMiembros,
        long totalTareas,
        long tareasCompletadas,
        double porcentajeProgreso
) {}
