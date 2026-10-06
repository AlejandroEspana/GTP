package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.response.DashboardGlobalResponse;
import com.laboratorio.proyectos.dto.response.DashboardProyectoResponse;
import com.laboratorio.proyectos.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard e Indicadores", description = "Métricas globales del laboratorio y progreso por proyecto")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/global")
    @PreAuthorize("hasRole('COORDINADOR')")
    @Operation(summary = "Obtener panel de indicadores globales del laboratorio (Solo Coordinador)")
    public ResponseEntity<DashboardGlobalResponse> obtenerDashboardGlobal() {
        return ResponseEntity.ok(dashboardService.obtenerDashboardGlobal());
    }

    @GetMapping("/proyectos/{proyectoId}")
    @Operation(summary = "Obtener métricas y avance de un proyecto específico")
    public ResponseEntity<DashboardProyectoResponse> obtenerDashboardProyecto(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(dashboardService.obtenerDashboardProyecto(proyectoId));
    }
}
