package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoProyecto estado;

    @Embedded
    private RangoFechas periodo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MiembroProyecto> miembros = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvidenciaMultimedia> evidencias = new ArrayList<>();

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaBitacora> bitacoras = new ArrayList<>();

    protected Proyecto() {
    }

    public Proyecto(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        this.id = UUID.randomUUID();
        this.nombre = nombre.trim();
        this.descripcion = descripcion;
        this.estado = EstadoProyecto.PLANEACION;
        this.periodo = new RangoFechas((fechaInicio != null) ? fechaInicio : LocalDate.now(), fechaFin);
        this.fechaCreacion = LocalDate.now();
    }

    public void cambiarEstado(EstadoProyecto nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
    }

    public void cerrarProyecto() {
        this.estado = EstadoProyecto.FINALIZADO;
        LocalDate hoy = LocalDate.now();
        this.periodo = new RangoFechas(this.periodo.fechaInicio(), hoy);
    }

    public void actualizarDatos(String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
        }
        this.nombre = nombre.trim();
        this.descripcion = descripcion;
        if (fechaInicio != null) {
            this.periodo = new RangoFechas(fechaInicio, fechaFin);
        }
    }

    public MiembroProyecto vincularMiembro(Usuario usuario, RolEnProyecto rol, LocalDate fechaInicio) {
        for (MiembroProyecto m : this.miembros) {
            if (m.getUsuario().getId().equals(usuario.getId()) && m.isActivo()) {
                throw new IllegalStateException("El usuario ya se encuentra vinculado activamente a este proyecto");
            }
        }
        MiembroProyecto nuevoMiembro = new MiembroProyecto(this, usuario, rol, fechaInicio);
        this.miembros.add(nuevoMiembro);
        return nuevoMiembro;
    }

    public void desvincularMiembro(Usuario usuario) {
        for (MiembroProyecto m : this.miembros) {
            if (m.getUsuario().getId().equals(usuario.getId()) && m.isActivo()) {
                m.finalizarVinculacion(LocalDate.now());
                return;
            }
        }
        throw new IllegalStateException("El usuario no es un miembro activo del proyecto");
    }

    public boolean esMiembroActivo(UUID usuarioId) {
        return this.miembros.stream()
                .anyMatch(m -> m.getUsuario().getId().equals(usuarioId) && m.isActivo());
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoProyecto getEstado() {
        return estado;
    }

    public RangoFechas getPeriodo() {
        return periodo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public List<MiembroProyecto> getMiembros() {
        return Collections.unmodifiableList(miembros);
    }

    public List<Tarea> getTareas() {
        return Collections.unmodifiableList(tareas);
    }

    public List<Documento> getDocumentos() {
        return Collections.unmodifiableList(documentos);
    }

    public List<EvidenciaMultimedia> getEvidencias() {
        return Collections.unmodifiableList(evidencias);
    }

    public List<EntradaBitacora> getBitacoras() {
        return Collections.unmodifiableList(bitacoras);
    }
}
