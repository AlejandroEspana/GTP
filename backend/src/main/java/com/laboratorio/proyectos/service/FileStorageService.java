package com.laboratorio.proyectos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(@Value("${app.upload.dir:./uploads}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo crear el directorio de subida de archivos", ex);
        }
    }

    public record ArchivoGuardado(
            String nombreArchivoGuardado,
            String nombreOriginal,
            String urlDescarga,
            long tamanoBytes,
            String tipoContenido
    ) {}

    public ArchivoGuardado guardarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No se ha enviado ningún archivo o el archivo está vacío");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "archivo"));
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String nombreGuardado = UUID.randomUUID() + extension;

        try {
            Path targetLocation = this.storageLocation.resolve(nombreGuardado);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String urlDescarga = "/api/archivos/" + nombreGuardado;
            return new ArchivoGuardado(
                    nombreGuardado,
                    originalFilename,
                    urlDescarga,
                    file.getSize(),
                    file.getContentType()
            );
        } catch (IOException ex) {
            throw new RuntimeException("Error al almacenar el archivo en disco: " + originalFilename, ex);
        }
    }

    public Resource cargarArchivoComoRecurso(String nombreArchivo) {
        try {
            Path filePath = this.storageLocation.resolve(nombreArchivo).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo no encontrado: " + nombreArchivo);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Ruta de archivo inválida: " + nombreArchivo, ex);
        }
    }
}
