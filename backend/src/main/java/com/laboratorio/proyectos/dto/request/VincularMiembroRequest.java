package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.RolEnProyecto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record VincularMiembroRequest(
        @NotNull(message = "El ID del usuario es obligatorio")
        UUID usuarioId,

        @NotNull(message = "El rol en el proyecto es obligatorio")
        RolEnProyecto rolEnProyecto,

        LocalDate fechaInicio
) {}
