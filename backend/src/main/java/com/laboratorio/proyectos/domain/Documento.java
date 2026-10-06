package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "documentos")
public class Documento {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false, length = 35)
    private CategoriaDocumento categoria;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @OneToMany(mappedBy = "documento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VersionDocumento> versiones = new ArrayList<>();

    protected Documento() {
    }

    public Documento(Proyecto proyecto, String titulo, String descripcion, CategoriaDocumento categoria) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto es obligatorio para crear un documento");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del documento es obligatorio");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría del documento es obligatoria");
        }

        this.id = UUID.randomUUID();
        this.proyecto = proyecto;
        this.titulo = titulo.trim();
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaCreacion = LocalDate.now();
    }

    public VersionDocumento agregarVersion(Usuario autor, String archivoUrl, String nombreOriginal,
                                           String resumenCambios, Long tamanoBytes, String tipoContenido) {
        int proximoNumero = this.versiones.size() + 1;
        VersionDocumento nuevaVersion = new VersionDocumento(
                this, proximoNumero, autor, archivoUrl, nombreOriginal, resumenCambios, tamanoBytes, tipoContenido);
        this.versiones.add(nuevaVersion);
        return nuevaVersion;
    }

    public Optional<VersionDocumento> obtenerUltimaVersion() {
        return this.versiones.stream().max(Comparator.comparingInt(VersionDocumento::getNumeroVersion));
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

    public CategoriaDocumento getCategoria() {
        return categoria;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public List<VersionDocumento> getVersiones() {
        return Collections.unmodifiableList(versiones);
    }
}
