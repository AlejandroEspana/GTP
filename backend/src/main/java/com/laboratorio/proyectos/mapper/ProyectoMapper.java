package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.EstadoTarea;
import com.laboratorio.proyectos.domain.MiembroProyecto;
import com.laboratorio.proyectos.domain.Proyecto;
import com.laboratorio.proyectos.domain.Tarea;
import com.laboratorio.proyectos.dto.response.MiembroProyectoResponse;
import com.laboratorio.proyectos.dto.response.ProyectoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProyectoMapper {

    public ProyectoResponse toResponse(Proyecto proyecto) {
        if (proyecto == null) return null;

        long totalMiembros = proyecto.getMiembros().stream().filter(MiembroProyecto::isActivo).count();
        long totalTareas = proyecto.getTareas().size();
        long tareasCompletadas = proyecto.getTareas().stream()
                .filter(t -> t.getEstado() == EstadoTarea.COMPLETADA)
                .count();

        double porcentajeProgreso = totalTareas > 0
                ? Math.round(((double) tareasCompletadas / totalTareas) * 100.0 * 10.0) / 10.0
                : 0.0;

        return new ProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.getEstado(),
                proyecto.getPeriodo() != null ? proyecto.getPeriodo().fechaInicio() : null,
                proyecto.getPeriodo() != null ? proyecto.getPeriodo().fechaFin() : null,
                proyecto.getFechaCreacion(),
                totalMiembros,
                totalTareas,
                tareasCompletadas,
                porcentajeProgreso
        );
    }

    public List<ProyectoResponse> toResponseList(List<Proyecto> proyectos) {
        if (proyectos == null) return List.of();
        return proyectos.stream().map(this::toResponse).toList();
    }

    public MiembroProyectoResponse toMiembroResponse(MiembroProyecto miembro) {
        if (miembro == null) return null;

        return new MiembroProyectoResponse(
                miembro.getId(),
                miembro.getUsuario().getId(),
                miembro.getUsuario().getCodigo(),
                miembro.getUsuario().getNombreCompleto(),
                miembro.getUsuario().getCorreo(),
                miembro.getRolEnProyecto(),
                miembro.getPeriodo() != null ? miembro.getPeriodo().fechaInicio() : null,
                miembro.getPeriodo() != null ? miembro.getPeriodo().fechaFin() : null,
                miembro.isActivo()
        );
    }

    public List<MiembroProyectoResponse> toMiembroResponseList(List<MiembroProyecto> miembros) {
        if (miembros == null) return List.of();
        return miembros.stream().map(this::toMiembroResponse).toList();
    }
}
