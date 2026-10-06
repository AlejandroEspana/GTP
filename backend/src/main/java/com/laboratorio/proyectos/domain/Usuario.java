package com.laboratorio.proyectos.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo", length = 30)
    private String codigo;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "programa_academico", length = 120)
    private String programaAcademico;

    @Column(name = "semestre")
    private Integer semestre;

    @Column(name = "fecha_ingreso_laboratorio", nullable = false)
    private LocalDate fechaIngresoLaboratorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 30)
    private RolUsuario rol;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MiembroProyecto> membresias = new ArrayList<>();

    protected Usuario() {
    }

    public Usuario(String codigo, String nombres, String apellidos, String correo, String password,
                   String programaAcademico, Integer semestre, LocalDate fechaIngresoLaboratorio, RolUsuario rol) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios");
        }
        if (correo == null || correo.isBlank() || !correo.contains("@")) {
            throw new IllegalArgumentException("El correo institucional es inválido o no está presente");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol de usuario es obligatorio");
        }

        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.nombres = nombres.trim();
        this.apellidos = apellidos.trim();
        this.correo = correo.trim().toLowerCase();
        this.password = password;
        this.programaAcademico = programaAcademico;
        this.semestre = semestre;
        this.fechaIngresoLaboratorio = (fechaIngresoLaboratorio != null) ? fechaIngresoLaboratorio : LocalDate.now();
        this.rol = rol;
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    public void cambiarRol(RolUsuario nuevoRol) {
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        this.rol = nuevoRol;
    }

    public void actualizarPerfil(String nombres, String apellidos, String programaAcademico, Integer semestre) {
        if (nombres != null && !nombres.isBlank()) {
            this.nombres = nombres.trim();
        }
        if (apellidos != null && !apellidos.isBlank()) {
            this.apellidos = apellidos.trim();
        }
        this.programaAcademico = programaAcademico;
        this.semestre = semestre;
    }

    public void cambiarPassword(String nuevoPasswordHash) {
        if (nuevoPasswordHash == null || nuevoPasswordHash.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede ser vacía");
        }
        this.password = nuevoPasswordHash;
    }

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }

    public UUID getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public String getProgramaAcademico() {
        return programaAcademico;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public LocalDate getFechaIngresoLaboratorio() {
        return fechaIngresoLaboratorio;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public List<MiembroProyecto> getMembresias() {
        return Collections.unmodifiableList(membresias);
    }
}
