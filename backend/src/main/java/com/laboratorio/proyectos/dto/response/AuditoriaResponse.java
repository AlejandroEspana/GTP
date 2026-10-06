package com.laboratorio.proyectos.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditoriaResponse(
        UUID id,
        String accion,
        UUID usuarioId,
        String correoUsuario,
        String rolUsuario,
        String entidadAfectada,
        String entidadId,
        String detalles,
        LocalDateTime fechaHora,
        String ipOrigen
) {}
