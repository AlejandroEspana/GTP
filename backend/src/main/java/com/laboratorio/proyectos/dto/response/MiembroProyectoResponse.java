package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.RolEnProyecto;
import java.time.LocalDate;
import java.util.UUID;

public record MiembroProyectoResponse(
        UUID id,
        UUID usuarioId,
        String codigoUsuario,
        String nombreUsuario,
        String correoUsuario,
        RolEnProyecto rolEnProyecto,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        boolean activo
) {}
