package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.Documento;
import com.laboratorio.proyectos.domain.VersionDocumento;
import com.laboratorio.proyectos.dto.response.DocumentoResponse;
import com.laboratorio.proyectos.dto.response.VersionDocumentoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentoMapper {

    public VersionDocumentoResponse toVersionResponse(VersionDocumento version) {
        if (version == null) return null;

        return new VersionDocumentoResponse(
                version.getId(),
                version.getNumeroVersion(),
                version.getAutor().getId(),
                version.getAutor().getNombreCompleto(),
                version.getFechaSubida(),
                version.getArchivoUrl(),
                version.getNombreArchivoOriginal(),
                version.getResumenCambios(),
                version.getTamanoBytes(),
                version.getTipoContenido()
        );
    }

    public List<VersionDocumentoResponse> toVersionResponseList(List<VersionDocumento> versiones) {
        if (versiones == null) return List.of();
        return versiones.stream().map(this::toVersionResponse).toList();
    }

    public DocumentoResponse toResponse(Documento documento) {
        if (documento == null) return null;

        List<VersionDocumentoResponse> versionesDto = toVersionResponseList(documento.getVersiones());
        VersionDocumentoResponse ultimaVersionDto = documento.obtenerUltimaVersion()
                .map(this::toVersionResponse)
                .orElse(null);

        return new DocumentoResponse(
                documento.getId(),
                documento.getProyecto().getId(),
                documento.getTitulo(),
                documento.getDescripcion(),
                documento.getCategoria(),
                documento.getFechaCreacion(),
                versionesDto,
                documento.getVersiones().size(),
                ultimaVersionDto
        );
    }

    public List<DocumentoResponse> toResponseList(List<Documento> documentos) {
        if (documentos == null) return List.of();
        return documentos.stream().map(this::toResponse).toList();
    }
}
