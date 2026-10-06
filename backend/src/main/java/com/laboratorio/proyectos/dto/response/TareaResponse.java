package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.EstadoTarea;
import com.laboratorio.proyectos.domain.Prioridad;
import java.time.LocalDate;
import java.util.UUID;

public record TareaResponse(
        UUID id,
        UUID proyectoId,
        String titulo,
        String descripcion,
        UUID responsableId,
        String responsableNombre,
        EstadoTarea estado,
        Prioridad prioridad,
        LocalDate fechaLimite,
        String etiquetas,
        LocalDate fechaCreacion
) {}
