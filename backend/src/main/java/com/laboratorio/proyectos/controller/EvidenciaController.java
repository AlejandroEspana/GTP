package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.domain.CategoriaEvidencia;
import com.laboratorio.proyectos.dto.request.CrearEvidenciaRequest;
import com.laboratorio.proyectos.dto.response.EvidenciaResponse;
import com.laboratorio.proyectos.service.EvidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Galería Multimedia y Evidencias", description = "Repositorio visual de prototipos, capturas y evidencias")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("/api/proyectos/{proyectoId}/evidencias")
    @Operation(summary = "Listar galería de evidencias de un proyecto (filtrable por categoría)")
    public ResponseEntity<List<EvidenciaResponse>> listarPorProyecto(
            @PathVariable UUID proyectoId,
            @RequestParam(required = false) CategoriaEvidencia categoria) {
        return ResponseEntity.ok(evidenciaService.listarPorProyecto(proyectoId, categoria));
    }

    @PostMapping(value = "/api/proyectos/{proyectoId}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir una nueva evidencia multimedia al proyecto")
    public ResponseEntity<EvidenciaResponse> crear(
            @PathVariable UUID proyectoId,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("categoria") CategoriaEvidencia categoria,
            @RequestPart("archivo") MultipartFile archivo,
            HttpServletRequest httpRequest,
            UriComponentsBuilder uriBuilder) {

        CrearEvidenciaRequest request = new CrearEvidenciaRequest(titulo, descripcion, categoria);
        EvidenciaResponse creada = evidenciaService.crear(proyectoId, request, archivo, httpRequest);
        URI uri = uriBuilder.path("/api/evidencias/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(uri).body(creada);
    }

    @DeleteMapping("/api/evidencias/{id}")
    @Operation(summary = "Eliminar una evidencia multimedia")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id, HttpServletRequest httpRequest) {
        evidenciaService.eliminar(id, httpRequest);
        return ResponseEntity.noContent().build();
    }
}
