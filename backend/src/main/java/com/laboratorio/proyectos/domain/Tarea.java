package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tareas")
public class Tarea {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoTarea estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 30)
    private Prioridad prioridad;

    @Column(name = "fecha_limite")
    private LocalDate fechaLimite;

    @Column(name = "etiquetas", length = 200)
    private String etiquetas;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    protected Tarea() {
    }

    public Tarea(Proyecto proyecto, String titulo, String descripcion, Usuario responsable,
                 Prioridad prioridad, LocalDate fechaLimite, String etiquetas) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto es obligatorio para crear una tarea");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la tarea es obligatorio");
        }

        this.id = UUID.randomUUID();
        this.proyecto = proyecto;
        this.titulo = titulo.trim();
        this.descripcion = descripcion;
        this.responsable = responsable;
        this.estado = EstadoTarea.PENDIENTE;
        this.prioridad = (prioridad != null) ? prioridad : Prioridad.MEDIA;
        this.fechaLimite = fechaLimite;
        this.etiquetas = etiquetas;
        this.fechaCreacion = LocalDate.now();
    }

    public void iniciarDesarrollo() {
        this.estado = EstadoTarea.EN_DESARROLLO;
    }

    public void completar() {
        this.estado = EstadoTarea.COMPLETADA;
    }

    public void marcarPendiente() {
        this.estado = EstadoTarea.PENDIENTE;
    }

    public void cambiarEstado(EstadoTarea nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }

    public void asignarResponsable(Usuario usuario) {
        this.responsable = usuario;
    }

    public void actualizarDetalles(String titulo, String descripcion, Prioridad prioridad, LocalDate fechaLimite, String etiquetas) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        this.titulo = titulo.trim();
        this.descripcion = descripcion;
        if (prioridad != null) {
            this.prioridad = prioridad;
        }
        this.fechaLimite = fechaLimite;
        this.etiquetas = etiquetas;
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

    public Usuario getResponsable() {
        return responsable;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public String getEtiquetas() {
        return etiquetas;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
}
