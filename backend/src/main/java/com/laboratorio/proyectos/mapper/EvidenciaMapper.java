package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.EvidenciaMultimedia;
import com.laboratorio.proyectos.dto.response.EvidenciaResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EvidenciaMapper {

    public EvidenciaResponse toResponse(EvidenciaMultimedia evidencia) {
        if (evidencia == null) return null;

        return new EvidenciaResponse(
                evidencia.getId(),
                evidencia.getProyecto().getId(),
                evidencia.getTitulo(),
                evidencia.getDescripcion(),
                evidencia.getAutor().getId(),
                evidencia.getAutor().getNombreCompleto(),
                evidencia.getFechaSubida(),
                evidencia.getCategoria(),
                evidencia.getArchivoUrl(),
                evidencia.getTipoArchivo()
        );
    }

    public List<EvidenciaResponse> toResponseList(List<EvidenciaMultimedia> evidencias) {
        if (evidencias == null) return List.of();
        return evidencias.stream().map(this::toResponse).toList();
    }
}
