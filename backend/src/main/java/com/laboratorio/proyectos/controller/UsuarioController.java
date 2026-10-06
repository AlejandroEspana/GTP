package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.domain.RolUsuario;
import com.laboratorio.proyectos.dto.request.ActualizarUsuarioRequest;
import com.laboratorio.proyectos.dto.request.RegistroUsuarioRequest;
import com.laboratorio.proyectos.dto.response.FichaIntegranteResponse;
import com.laboratorio.proyectos.dto.response.UsuarioResponse;
import com.laboratorio.proyectos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios e Integrantes", description = "Gestión de usuarios y fichas de integrantes")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Listar integrantes (filtrable opcionalmente por rol)")
    public ResponseEntity<List<UsuarioResponse>> listar(@RequestParam(required = false) RolUsuario rol) {
        if (rol != null) {
            return ResponseEntity.ok(usuarioService.listarPorRol(rol));
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR') or #id == authentication.principal.id")
    @Operation(summary = "Obtener información de un usuario por ID")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping("/{id}/ficha")
    @PreAuthorize("hasRole('COORDINADOR') or #id == authentication.principal.id")
    @Operation(summary = "Consultar ficha completa individual de un integrante con proyectos y auditoría")
    public ResponseEntity<FichaIntegranteResponse> obtenerFicha(@PathVariable UUID id, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(usuarioService.obtenerFichaIntegrante(id, httpRequest));
    }

    @PostMapping
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Crear nuevo usuario por parte del coordinador")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody RegistroUsuarioRequest request,
                                                 HttpServletRequest httpRequest,
                                                 UriComponentsBuilder uriBuilder) {
        UsuarioResponse creado = usuarioService.crearUsuario(request, httpRequest);
        URI uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(uri).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Actualizar datos y rol de un usuario")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable UUID id,
                                                      @RequestBody ActualizarUsuarioRequest request,
                                                      HttpServletRequest httpRequest) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(id, request, httpRequest));
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Activar usuario")
    public ResponseEntity<Void> activar(@PathVariable UUID id, HttpServletRequest httpRequest) {
        usuarioService.cambiarEstadoActivo(id, true, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Desactivar usuario")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id, HttpServletRequest httpRequest) {
        usuarioService.cambiarEstadoActivo(id, false, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
