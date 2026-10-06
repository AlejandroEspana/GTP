package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.RolEnProyecto;
import com.laboratorio.proyectos.domain.RolUsuario;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record FichaIntegranteResponse(
        UUID id,
        String codigo,
        String nombres,
        String apellidos,
        String correo,
        String programaAcademico,
        Integer semestre,
        LocalDate fechaIngresoLaboratorio,
        RolUsuario rol,
        boolean activo,
        List<ParticipacionProyectoDto> participaciones,
        List<ActividadRecienteDto> actividadesRecientes
) {
    public record ParticipacionProyectoDto(
            UUID proyectoId,
            String proyectoNombre,
            RolEnProyecto rolEnProyecto,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            boolean activo
    ) {}

    public record ActividadRecienteDto(
            String accion,
            String entidadAfectada,
            String detalles,
            LocalDateTime fechaHora
    ) {}
}
