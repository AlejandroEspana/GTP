package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.CategoriaEvidencia;
import java.time.LocalDateTime;
import java.util.UUID;

public record EvidenciaResponse(
        UUID id,
        UUID proyectoId,
        String titulo,
        String descripcion,
        UUID autorId,
        String autorNombre,
        LocalDateTime fechaSubida,
        CategoriaEvidencia categoria,
        String archivoUrl,
        String tipoArchivo
) {}
