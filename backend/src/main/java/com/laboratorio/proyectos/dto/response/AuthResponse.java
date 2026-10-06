package com.laboratorio.proyectos.dto.response;

import com.laboratorio.proyectos.domain.RolUsuario;
import java.util.UUID;

public record AuthResponse(
        String token,
        UUID id,
        String correo,
        String nombreCompleto,
        RolUsuario rol,
        String codigo
) {}
