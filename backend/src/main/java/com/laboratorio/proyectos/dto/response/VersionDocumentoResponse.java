package com.laboratorio.proyectos.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record VersionDocumentoResponse(
        UUID id,
        int numeroVersion,
        UUID autorId,
        String autorNombre,
        LocalDateTime fechaSubida,
        String archivoUrl,
        String nombreArchivoOriginal,
        String resumenCambios,
        Long tamanoBytes,
        String tipoContenido
) {}
