package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.dto.request.CrearBitacoraRequest;
import com.laboratorio.proyectos.dto.response.BitacoraResponse;
import com.laboratorio.proyectos.service.BitacoraService;
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
@RequestMapping("/api/proyectos/{proyectoId}/bitacora")
@Tag(name = "Bitácora Cronológica", description = "Línea de tiempo de avances, hitos y actas de reunión")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    public BitacoraController(BitacoraService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @GetMapping
    @Operation(summary = "Listar la bitácora cronológica del proyecto")
    public ResponseEntity<List<BitacoraResponse>> listar(@PathVariable UUID proyectoId) {
        return ResponseEntity.ok(bitacoraService.listarPorProyecto(proyectoId));
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva entrada en la bitácora")
    public ResponseEntity<BitacoraResponse> crear(
            @PathVariable UUID proyectoId,
            @Valid @RequestBody CrearBitacoraRequest request,
            HttpServletRequest httpRequest,
            UriComponentsBuilder uriBuilder) {

        BitacoraResponse creada = bitacoraService.crear(proyectoId, request, httpRequest);
        URI uri = uriBuilder.path("/api/proyectos/{proyectoId}/bitacora/{id}")
                .buildAndExpand(proyectoId, creada.id()).toUri();
        return ResponseEntity.created(uri).body(creada);
    }
}
