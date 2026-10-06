package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "entradas_bitacora")
public class EntradaBitacora {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "contenido", nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private TipoEntradaBitacora tipo;

    protected EntradaBitacora() {
    }

    public EntradaBitacora(Proyecto proyecto, Usuario autor, String titulo, String contenido, TipoEntradaBitacora tipo) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto es obligatorio");
        }
        if (autor == null) {
            throw new IllegalArgumentException("El autor es obligatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la bitácora es obligatorio");
        }
        if (contenido == null || contenido.isBlank()) {
            throw new IllegalArgumentException("El contenido de la entrada es obligatorio");
        }

        this.id = UUID.randomUUID();
        this.proyecto = proyecto;
        this.autor = autor;
        this.fechaHora = LocalDateTime.now();
        this.titulo = titulo.trim();
        this.contenido = contenido.trim();
        this.tipo = (tipo != null) ? tipo : TipoEntradaBitacora.AVANCE;
    }

    public UUID getId() {
        return id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public TipoEntradaBitacora getTipo() {
        return tipo;
    }
}
