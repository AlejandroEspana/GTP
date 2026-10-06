package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.Tarea;
import com.laboratorio.proyectos.dto.response.TareaResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TareaMapper {

    public TareaResponse toResponse(Tarea tarea) {
        if (tarea == null) return null;

        return new TareaResponse(
                tarea.getId(),
                tarea.getProyecto().getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getResponsable() != null ? tarea.getResponsable().getId() : null,
                tarea.getResponsable() != null ? tarea.getResponsable().getNombreCompleto() : "Sin asignar",
                tarea.getEstado(),
                tarea.getPrioridad(),
                tarea.getFechaLimite(),
                tarea.getEtiquetas(),
                tarea.getFechaCreacion()
        );
    }

    public List<TareaResponse> toResponseList(List<Tarea> tareas) {
        if (tareas == null) return List.of();
        return tareas.stream().map(this::toResponse).toList();
    }
}
