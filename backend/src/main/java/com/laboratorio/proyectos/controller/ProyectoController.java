package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.request.ActualizarProyectoRequest;
import com.laboratorio.proyectos.dto.request.CrearProyectoRequest;
import com.laboratorio.proyectos.dto.request.VincularMiembroRequest;
import com.laboratorio.proyectos.dto.response.MiembroProyectoResponse;
import com.laboratorio.proyectos.dto.response.ProyectoResponse;
import com.laboratorio.proyectos.service.ProyectoService;
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
@RequestMapping("/api/proyectos")
@Tag(name = "Proyectos", description = "Gestión de proyectos del laboratorio y sus integrantes")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @GetMapping
    @Operation(summary = "Listar proyectos (todos para coordinador, asignados para asesores y estudiantes)")
    public ResponseEntity<List<ProyectoResponse>> listar() {
        return ResponseEntity.ok(proyectoService.listarProyectosUsuarioActual());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un proyecto")
    public ResponseEntity<ProyectoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(proyectoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Crear un nuevo proyecto (Solo Coordinador)")
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody CrearProyectoRequest request,
                                                  HttpServletRequest httpRequest,
                                                  UriComponentsBuilder uriBuilder) {
        ProyectoResponse creado = proyectoService.crear(request, httpRequest);
        URI uri = uriBuilder.path("/api/proyectos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(uri).body(creado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Editar información del proyecto (Coordinador o Asesor del proyecto)")
    public ResponseEntity<ProyectoResponse> actualizar(@PathVariable UUID id,
                                                       @Valid @RequestBody ActualizarProyectoRequest request,
                                                       HttpServletRequest httpRequest) {
        return ResponseEntity.ok(proyectoService.actualizar(id, request, httpRequest));
    }

    @PostMapping("/{id}/cerrar")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Cerrar/finalizar un proyecto (Solo Coordinador)")
    public ResponseEntity<ProyectoResponse> cerrar(@PathVariable UUID id, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(proyectoService.cerrarProyecto(id, httpRequest));
    }

    @GetMapping("/{id}/miembros")
    @Operation(summary = "Listar miembros de un proyecto")
    public ResponseEntity<List<MiembroProyectoResponse>> listarMiembros(@PathVariable UUID id) {
        return ResponseEntity.ok(proyectoService.listarMiembros(id));
    }

    @PostMapping("/{id}/miembros")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Vincular un integrante al proyecto")
    public ResponseEntity<MiembroProyectoResponse> vincularMiembro(@PathVariable UUID id,
                                                                   @Valid @RequestBody VincularMiembroRequest request,
                                                                   HttpServletRequest httpRequest) {
        return ResponseEntity.ok(proyectoService.vincularMiembro(id, request, httpRequest));
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Desvincular un integrante del proyecto")
    public ResponseEntity<Void> desvincularMiembro(@PathVariable UUID id,
                                                   @PathVariable UUID usuarioId,
                                                   HttpServletRequest httpRequest) {
        proyectoService.desvincularMiembro(id, usuarioId, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
