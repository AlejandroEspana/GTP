package com.laboratorio.proyectos.dto.request;

import com.laboratorio.proyectos.domain.RolUsuario;

public record ActualizarUsuarioRequest(
        String nombres,
        String apellidos,
        String programaAcademico,
        Integer semestre,
        RolUsuario rol,
        Boolean activo
) {}
