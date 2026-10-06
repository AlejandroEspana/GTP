package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistroUsuarioRequest(
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        String nombres,

        @NotBlank(message = "El apellido es obligatorio")
        String apellidos,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo no válido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password,

        String programaAcademico,
        Integer semestre,
        LocalDate fechaIngresoLaboratorio,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol
) {}
