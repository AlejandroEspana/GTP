package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.response.AuditoriaResponse;
import com.laboratorio.proyectos.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auditoria")
@Tag(name = "Auditoría Automática", description = "Trazabilidad inmutable de acciones del sistema")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Listar registros de auditoría más recientes")
    public ResponseEntity<List<AuditoriaResponse>> listarTodas() {
        return ResponseEntity.ok(auditoriaService.listarTodas());
    }

    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Listar registros de auditoría por usuario")
    public ResponseEntity<List<AuditoriaResponse>> listarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/entidades/{entidad}")
    @PreAuthorize("hasAnyRole('COORDINADOR', 'ASESOR')")
    @Operation(summary = "Listar registros de auditoría por entidad afectada")
    public ResponseEntity<List<AuditoriaResponse>> listarPorEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }
}
