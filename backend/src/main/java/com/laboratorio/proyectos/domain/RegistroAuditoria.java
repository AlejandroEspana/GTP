package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registros_auditoria")
public class RegistroAuditoria {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "correo_usuario", nullable = false, length = 150)
    private String correoUsuario;

    @Column(name = "rol_usuario", nullable = false, length = 30)
    private String rolUsuario;

    @Column(name = "entidad_afectada", nullable = false, length = 80)
    private String entidadAfectada;

    @Column(name = "entidad_id", length = 80)
    private String entidadId;

    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(name = "ip_origen", length = 50)
    private String ipOrigen;

    protected RegistroAuditoria() {
    }

    public RegistroAuditoria(String accion, UUID usuarioId, String correoUsuario, String rolUsuario,
                             String entidadAfectada, String entidadId, String detalles, String ipOrigen) {
        if (accion == null || accion.isBlank()) {
            throw new IllegalArgumentException("La acción de auditoría es obligatoria");
        }
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new IllegalArgumentException("El correo del usuario responsable es obligatorio");
        }
        if (entidadAfectada == null || entidadAfectada.isBlank()) {
            throw new IllegalArgumentException("La entidad afectada es obligatoria");
        }

        this.id = UUID.randomUUID();
        this.accion = accion;
        this.usuarioId = usuarioId;
        this.correoUsuario = correoUsuario;
        this.rolUsuario = (rolUsuario != null) ? rolUsuario : "DESCONOCIDO";
        this.entidadAfectada = entidadAfectada;
        this.entidadId = entidadId;
        this.detalles = detalles;
        this.fechaHora = LocalDateTime.now();
        this.ipOrigen = ipOrigen;
    }

    public UUID getId() {
        return id;
    }

    public String getAccion() {
        return accion;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public String getEntidadAfectada() {
        return entidadAfectada;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public String getDetalles() {
        return detalles;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }
}
