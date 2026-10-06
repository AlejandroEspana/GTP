package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.TipoEntradaBitacora;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearBitacoraRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El contenido es obligatorio")
        String contenido,

        @NotNull(message = "El tipo de entrada es obligatorio")
        TipoEntradaBitacora tipo
) {}
