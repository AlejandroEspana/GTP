package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.Prioridad;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

public record CrearTareaRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String descripcion,
        UUID responsableId,
        Prioridad prioridad,
        LocalDate fechaLimite,
        String etiquetas
) {}
