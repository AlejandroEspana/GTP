package com.laboratorio.proyectos.controller;

import com.laboratorio.proyectos.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/archivos")
@Tag(name = "Archivos", description = "Descarga y visualización de archivos adjuntos y evidencias")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{nombreArchivo:.+}")
    @Operation(summary = "Descargar o visualizar un archivo almacenado")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String nombreArchivo, HttpServletRequest request) {
        Resource recurso = fileStorageService.cargarArchivoComoRecurso(nombreArchivo);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(recurso.getFile().getAbsolutePath());
        } catch (IOException ignored) {}

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}
