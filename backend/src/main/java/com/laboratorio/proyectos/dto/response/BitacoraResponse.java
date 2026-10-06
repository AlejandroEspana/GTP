package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.TipoEntradaBitacora;
import java.time.LocalDateTime;
import java.util.UUID;

public record BitacoraResponse(
        UUID id,
        UUID proyectoId,
        UUID autorId,
        String autorNombre,
        LocalDateTime fechaHora,
        String titulo,
        String contenido,
        TipoEntradaBitacora tipo
) {}
