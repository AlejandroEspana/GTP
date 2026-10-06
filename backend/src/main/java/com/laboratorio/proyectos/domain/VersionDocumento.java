package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "versiones_documento")
public class VersionDocumento {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id", nullable = false)
    private Documento documento;

    @Column(name = "numero_version", nullable = false)
    private int numeroVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "archivo_url", nullable = false, length = 300)
    private String archivoUrl;

    @Column(name = "nombre_archivo_original", length = 200)
    private String nombreArchivoOriginal;

    @Column(name = "resumen_cambios", columnDefinition = "TEXT")
    private String resumenCambios;

    @Column(name = "tamano_bytes")
    private Long tamanoBytes;

    @Column(name = "tipo_contenido", length = 100)
    private String tipoContenido;

    protected VersionDocumento() {
    }

    public VersionDocumento(Documento documento, int numeroVersion, Usuario autor, String archivoUrl,
                            String nombreArchivoOriginal, String resumenCambios, Long tamanoBytes, String tipoContenido) {
        if (documento == null) {
            throw new IllegalArgumentException("El documento asociado es obligatorio");
        }
        if (autor == null) {
            throw new IllegalArgumentException("El autor de la versión es obligatorio");
        }
        if (archivoUrl == null || archivoUrl.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        this.id = UUID.randomUUID();
        this.documento = documento;
        this.numeroVersion = numeroVersion;
        this.autor = autor;
        this.fechaSubida = LocalDateTime.now();
        this.archivoUrl = archivoUrl;
        this.nombreArchivoOriginal = nombreArchivoOriginal;
        this.resumenCambios = (resumenCambios != null && !resumenCambios.isBlank()) ? resumenCambios : "Versión " + numeroVersion;
        this.tamanoBytes = tamanoBytes;
        this.tipoContenido = tipoContenido;
    }

    public UUID getId() {
        return id;
    }

    public Documento getDocumento() {
        return documento;
    }

    public int getNumeroVersion() {
        return numeroVersion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public String getNombreArchivoOriginal() {
        return nombreArchivoOriginal;
    }

    public String getResumenCambios() {
        return resumenCambios;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }
}
