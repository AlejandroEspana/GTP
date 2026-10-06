package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.request.ActualizarTareaRequest;
import com.laboratorio.proyectos.dto.request.CambiarEstadoTareaRequest;
import com.laboratorio.proyectos.dto.request.CrearTareaRequest;
import com.laboratorio.proyectos.dto.response.TareaResponse;
import com.laboratorio.proyectos.service.TareaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Tareas", description = "Gestión de tareas y tablero Kanban del proyecto")
public class TareaController {

    private final TareaService tareaService;

    public TareaController(TareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping("/api/proyectos/{proyectoId}/tareas")
    @Operation(summary = "Listar tareas de un proyecto")
    public ResponseEntity<List<TareaResponse>> listarPorProyecto(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(tareaService.listarPorProyecto(proyectoId));
    }

    @PostMapping("/api/proyectos/{proyectoId}/tareas")
    @Operation(summary = "Crear tarea en un proyecto")
    public ResponseEntity<TareaResponse> crear(@PathVariable UUID proyectoId,
                                               @Valid @RequestBody CrearTareaRequest request,
                                               HttpServletRequest httpRequest,
                                               UriComponentsBuilder uriBuilder) {
        TareaResponse creada = tareaService.crear(proyectoId, request, httpRequest);
        URI uri = uriBuilder.path("/api/tareas/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(uri).body(creada);
    }

    @GetMapping("/api/tareas/{id}")
    @Operation(summary = "Obtener detalle de una tarea")
    public ResponseEntity<TareaResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(tareaService.obtenerPorId(id));
    }

    @PutMapping("/api/tareas/{id}")
    @Operation(summary = "Actualizar información de una tarea")
    public ResponseEntity<TareaResponse> actualizar(@PathVariable UUID id,
                                                    @Valid @RequestBody ActualizarTareaRequest request,
                                                    HttpServletRequest httpRequest) {
        return ResponseEntity.ok(tareaService.actualizar(id, request, httpRequest));
    }

    @PatchMapping("/api/tareas/{id}/estado")
    @Operation(summary = "Cambiar estado de una tarea (PENDIENTE, EN_DESARROLLO, COMPLETADA)")
    public ResponseEntity<TareaResponse> cambiarEstado(@PathVariable UUID id,
                                                       @Valid @RequestBody CambiarEstadoTareaRequest request,
                                                       HttpServletRequest httpRequest) {
        return ResponseEntity.ok(tareaService.cambiarEstado(id, request, httpRequest));
    }

    @DeleteMapping("/api/tareas/{id}")
    @Operation(summary = "Eliminar una tarea")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, HttpServletRequest httpRequest) {
        tareaService.eliminar(id, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
