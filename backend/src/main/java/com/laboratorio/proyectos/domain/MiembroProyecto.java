package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "miembros_proyecto")
public class MiembroProyecto {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_proyecto", nullable = false, length = 35)
    private RolEnProyecto rolEnProyecto;

    @Embedded
    private RangoFechas periodo;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    protected MiembroProyecto() {
    }

    public MiembroProyecto(Proyecto proyecto, Usuario usuario, RolEnProyecto rolEnProyecto, LocalDate fechaInicio) {
        if (proyecto == null) {
            throw new IllegalArgumentException("El proyecto es obligatorio");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (rolEnProyecto == null) {
            throw new IllegalArgumentException("El rol en el proyecto es obligatorio");
        }

        this.id = UUID.randomUUID();
        this.proyecto = proyecto;
        this.usuario = usuario;
        this.rolEnProyecto = rolEnProyecto;
        this.periodo = new RangoFechas((fechaInicio != null) ? fechaInicio : LocalDate.now(), null);
        this.activo = true;
    }

    public void finalizarVinculacion(LocalDate fechaFin) {
        LocalDate fin = (fechaFin != null) ? fechaFin : LocalDate.now();
        this.periodo = new RangoFechas(this.periodo.fechaInicio(), fin);
        this.activo = false;
    }

    public void reactivar() {
        this.periodo = new RangoFechas(LocalDate.now(), null);
        this.activo = true;
    }

    public UUID getId() {
        return id;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public RolEnProyecto getRolEnProyecto() {
        return rolEnProyecto;
    }

    public RangoFechas getPeriodo() {
        return periodo;
    }

    public boolean isActivo() {
        return activo;
    }
}
