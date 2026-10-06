package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.CategoriaDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearDocumentoRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaDocumento categoria,

        String resumenCambios
) {}
