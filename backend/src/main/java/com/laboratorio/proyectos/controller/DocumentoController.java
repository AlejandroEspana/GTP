package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.domain.CategoriaDocumento;
import com.laboratorio.proyectos.dto.request.CrearDocumentoRequest;
import com.laboratorio.proyectos.dto.request.SubirVersionRequest;
import com.laboratorio.proyectos.dto.response.DocumentoResponse;
import com.laboratorio.proyectos.dto.response.VersionDocumentoResponse;
import com.laboratorio.proyectos.service.DocumentoService;
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
@Tag(name = "Documentación Versionada", description = "Gestión de documentos versionados por categoría")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping("/api/proyectos/{proyectoId}/documentos")
    @Operation(summary = "Listar documentos de un proyecto (filtrable por categoría)")
    public ResponseEntity<List<DocumentoResponse>> listarPorProyecto(
            @PathVariable UUID proyectoId,
            @RequestParam(required = false) CategoriaDocumento categoria) {
        return ResponseEntity.ok(documentoService.listarPorProyecto(proyectoId, categoria));
    }

    @GetMapping("/api/documentos/{id}")
    @Operation(summary = "Obtener documento con su historial completo de versiones")
    public ResponseEntity<DocumentoResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id));
    }

    @PostMapping(value = "/api/proyectos/{proyectoId}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir un nuevo documento con su primera versión")
    public ResponseEntity<DocumentoResponse> crear(
            @PathVariable UUID proyectoId,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("categoria") CategoriaDocumento categoria,
            @RequestParam(value = "resumenCambios", required = false) String resumenCambios,
            @RequestPart("archivo") MultipartFile archivo,
            HttpServletRequest httpRequest,
            UriComponentsBuilder uriBuilder) {

        CrearDocumentoRequest request = new CrearDocumentoRequest(titulo, descripcion, categoria, resumenCambios);
        DocumentoResponse creado = documentoService.crear(proyectoId, request, archivo, httpRequest);
        URI uri = uriBuilder.path("/api/documentos/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(uri).body(creado);
    }

    @PostMapping(value = "/api/documentos/{id}/versiones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir una nueva versión de un documento existente (sin sobrescribir)")
    public ResponseEntity<VersionDocumentoResponse> agregarVersion(
            @PathVariable UUID id,
            @RequestParam(value = "resumenCambios", required = false) String resumenCambios,
            @RequestPart("archivo") MultipartFile archivo,
            HttpServletRequest httpRequest) {

        SubirVersionRequest request = new SubirVersionRequest(resumenCambios);
        return ResponseEntity.ok(documentoService.agregarVersion(id, request, archivo, httpRequest));
    }
}
