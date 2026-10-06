package com.laboratorio.proyectos.dto.response;

import java.util.List;

public record DashboardGlobalResponse(
        long totalProyectos,
        long proyectosActivos,
        long proyectosFinalizados,
        long proyectosPlaneacion,
        long totalEstudiantes,
        long totalAsesores,
        long totalDocumentos,
        long totalTareas,
        long tareasCompletadas,
        List<ProyectoResponse> proyectosSinActividadReciente
) {}
