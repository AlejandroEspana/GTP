package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.RolUsuario;
import java.time.LocalDate;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String codigo,
        String nombres,
        String apellidos,
        String correo,
        String programaAcademico,
        Integer semestre,
        LocalDate fechaIngresoLaboratorio,
        RolUsuario rol,
        boolean activo
) {}
