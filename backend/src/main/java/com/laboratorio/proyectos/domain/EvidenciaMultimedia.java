package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidencias_multimedia")
public class EvidenciaMultimedia {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 35)
    private CategoriaEvidencia categoria;

    @Column(name = "archivo_url", nullable = false, length = 300)
    private String archivoUrl;

    @Column(name = "tipo_archivo", length = 100)
    private String tipoArchivo;

    protected EvidenciaMultimedia() {
    }

    public EvidenciaMultimedia(Proyecto proyecto, String titulo, String descripcion, Usuario autor,
                               CategoriaEvidencia categoria, String archivoUrl, String tipoArchivo) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto es obligatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la evidencia es obligatorio");
        }
        if (autor == null) {
            throw new IllegalArgumentException("El autor de la evidencia es obligatorio");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (archivoUrl == null || archivoUrl.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        this.id = UUID.randomUUID();
        this.proyecto = proyecto;
        this.titulo = titulo.trim();
        this.descripcion = descripcion;
        this.autor = autor;
        this.fechaSubida = LocalDateTime.now();
        this.categoria = categoria;
        this.archivoUrl = archivoUrl;
        this.tipoArchivo = tipoArchivo;
    }

    public UUID getId() {
        return id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Usuario getAutor() {
        return autor;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public CategoriaEvidencia getCategoria() {
        return categoria;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }
}
