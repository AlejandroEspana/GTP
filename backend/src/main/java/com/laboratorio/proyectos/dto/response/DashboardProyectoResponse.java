package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.EstadoProyecto;
import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardProyectoResponse(
        UUID proyectoId,
        String proyectoNombre,
        EstadoProyecto estado,
        long totalTareas,
        long tareasPendientes,
        long tareasEnDesarrollo,
        long tareasCompletadas,
        double porcentajeProgreso,
        long totalDocumentos,
        long totalEvidencias,
        long totalMiembros,
        LocalDateTime ultimaActividad
) {}
