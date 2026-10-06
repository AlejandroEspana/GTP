package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.request.LoginRequest;
import com.laboratorio.proyectos.dto.request.RegistroUsuarioRequest;
import com.laboratorio.proyectos.dto.response.AuthResponse;
import com.laboratorio.proyectos.dto.response.UsuarioResponse;
import com.laboratorio.proyectos.security.SecurityUtils;
import com.laboratorio.proyectos.service.AuthService;
import com.laboratorio.proyectos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de login, registro y usuario actual")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo integrante")
    public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroUsuarioRequest request,
                                                HttpServletRequest httpRequest,
                                                UriComponentsBuilder uriBuilder) {
        AuthResponse response = authService.registrar(request, httpRequest);
        URI uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener datos del usuario actualmente autenticado")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioActual() {
        UUID usuarioId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(usuarioService.obtenerPorId(usuarioId));
    }
}
