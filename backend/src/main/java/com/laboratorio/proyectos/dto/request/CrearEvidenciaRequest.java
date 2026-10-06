package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.CategoriaEvidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearEvidenciaRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        CategoriaEvidencia categoria
) {}
