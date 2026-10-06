package com.laboratorio.proyectos.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;

public record CrearProyectoRequest(
        @NotBlank(message = "El nombre del proyecto es obligatorio")
        String nombre,

        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        UUID asesorLiderId
) {}
