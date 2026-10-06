package com.laboratorio.proyectos.security;

import com.laboratorio.proyectos.domain.RolUsuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getId)
                .orElseThrow(() -> new IllegalStateException("No hay un usuario autenticado en la sesión"));
    }

    public static String getCurrentUserEmail() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getUsername)
                .orElse("sistema@laboratorio.edu");
    }

    public static RolUsuario getCurrentUserRole() {
        return getCurrentUserDetails()
                .map(CustomUserDetails::getRol)
                .orElse(null);
    }

    public static boolean hasRole(RolUsuario rol) {
        return getCurrentUserRole() == rol;
    }
}
