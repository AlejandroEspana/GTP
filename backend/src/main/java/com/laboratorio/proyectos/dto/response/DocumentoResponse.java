package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.CategoriaDocumento;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record DocumentoResponse(
        UUID id,
        UUID proyectoId,
        String titulo,
        String descripcion,
        CategoriaDocumento categoria,
        LocalDate fechaCreacion,
        List<VersionDocumentoResponse> versiones,
        int totalVersiones,
        VersionDocumentoResponse ultimaVersion
) {}
