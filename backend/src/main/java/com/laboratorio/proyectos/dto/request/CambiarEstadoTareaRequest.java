package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.EstadoTarea;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoTareaRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoTarea estado
) {}
