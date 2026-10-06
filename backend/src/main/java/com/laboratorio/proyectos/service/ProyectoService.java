package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.*;
import com.laboratorio.proyectos.dto.request.ActualizarProyectoRequest;
import com.laboratorio.proyectos.dto.request.CrearProyectoRequest;
import com.laboratorio.proyectos.dto.request.VincularMiembroRequest;
import com.laboratorio.proyectos.dto.response.MiembroProyectoResponse;
import com.laboratorio.proyectos.dto.response.ProyectoResponse;
import com.laboratorio.proyectos.exception.ForbiddenException;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.ProyectoMapper;
import com.laboratorio.proyectos.repository.MiembroProyectoRepository;
import com.laboratorio.proyectos.repository.ProyectoRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MiembroProyectoRepository miembroProyectoRepository;
    private final ProyectoMapper proyectoMapper;
    private final AuditoriaService auditoriaService;

    public ProyectoService(ProyectoRepository proyectoRepository,
                           UsuarioRepository usuarioRepository,
                           MiembroProyectoRepository miembroProyectoRepository,
                           ProyectoMapper proyectoMapper,
                           AuditoriaService auditoriaService) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.miembroProyectoRepository = miembroProyectoRepository;
        this.proyectoMapper = proyectoMapper;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listarProyectosUsuarioActual() {
        RolUsuario rol = SecurityUtils.getCurrentUserRole();
        if (rol == RolUsuario.COORDINADOR) {
            return proyectoMapper.toResponseList(proyectoRepository.findAll());
        }

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        return proyectoMapper.toResponseList(proyectoRepository.findProyectosByUsuarioId(usuarioId));
    }

    @Transactional(readOnly = true)
    public List<ProyectoResponse> listarTodos() {
        return proyectoMapper.toResponseList(proyectoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ProyectoResponse obtenerPorId(UUID id) {
        validarAccesoAProyecto(id);
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        return proyectoMapper.toResponse(proyecto);
    }

    @Transactional
    public ProyectoResponse crear(CrearProyectoRequest request, HttpServletRequest httpRequest) {
        Proyecto proyecto = new Proyecto(
                request.nombre(),
                request.descripcion(),
                request.fechaInicio(),
                request.fechaFin()
        );

        Proyecto guardado = proyectoRepository.save(proyecto);

        // Si se asignó un asesor líder inicialmente
        if (request.asesorLiderId() != null) {
            Usuario asesor = usuarioRepository.findById(request.asesorLiderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asesor seleccionado no encontrado"));
            MiembroProyecto miembro = guardado.vincularMiembro(asesor, RolEnProyecto.ASESOR_LIDER, request.fechaInicio());
            miembroProyectoRepository.save(miembro);
        }

        auditoriaService.registrar("CREACION_PROYECTO", "Proyecto", guardado.getId().toString(),
                "Proyecto creado: " + guardado.getNombre(), httpRequest);

        return proyectoMapper.toResponse(guardado);
    }

    @Transactional
    public ProyectoResponse actualizar(UUID id, ActualizarProyectoRequest request, HttpServletRequest httpRequest) {
        validarPermisoEdicionProyecto(id);

        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        proyecto.actualizarDatos(request.nombre(), request.descripcion(), request.fechaInicio(), request.fechaFin());
        if (request.estado() != null) {
            proyecto.cambiarEstado(request.estado());
        }

        Proyecto actualizado = proyectoRepository.save(proyecto);
        auditoriaService.registrar("ACTUALIZACION_PROYECTO", "Proyecto", id.toString(),
                "Proyecto actualizado: " + actualizado.getNombre(), httpRequest);

        return proyectoMapper.toResponse(actualizado);
    }

    @Transactional
    public ProyectoResponse cerrarProyecto(UUID id, HttpServletRequest httpRequest) {
        Proyecto proyecto = proyectoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        proyecto.cerrarProyecto();
        Proyecto guardado = proyectoRepository.save(proyecto);

        auditoriaService.registrar("CIERRE_PROYECTO", "Proyecto", id.toString(),
                "Proyecto finalizado y cerrado: " + guardado.getNombre(), httpRequest);

        return proyectoMapper.toResponse(guardado);
    }

    @Transactional
    public MiembroProyectoResponse vincularMiembro(UUID proyectoId, VincularMiembroRequest request, HttpServletRequest httpRequest) {
        validarPermisoGestionMiembros(proyectoId, request.rolEnProyecto());

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        MiembroProyecto miembro = proyecto.vincularMiembro(usuario, request.rolEnProyecto(), request.fechaInicio());
        MiembroProyecto guardado = miembroProyectoRepository.save(miembro);

        auditoriaService.registrar("VINCULACION_MIEMBRO", "Proyecto", proyectoId.toString(),
                "Vinculado integrante " + usuario.getCorreo() + " como " + request.rolEnProyecto(), httpRequest);

        return proyectoMapper.toMiembroResponse(guardado);
    }

    @Transactional
    public void desvincularMiembro(UUID proyectoId, UUID usuarioId, HttpServletRequest httpRequest) {
        validarPermisoGestionMiembros(proyectoId, null);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        proyecto.desvincularMiembro(usuario);
        proyectoRepository.save(proyecto);

        auditoriaService.registrar("DESVINCULACION_MIEMBRO", "Proyecto", proyectoId.toString(),
                "Desvinculado integrante " + usuario.getCorreo() + " del proyecto " + proyecto.getNombre(), httpRequest);
    }

    @Transactional(readOnly = true)
    public List<MiembroProyectoResponse> listarMiembros(UUID proyectoId) {
        validarAccesoAProyecto(proyectoId);
        List<MiembroProyecto> miembros = miembroProyectoRepository.findByProyectoId(proyectoId);
        return proyectoMapper.toMiembroResponseList(miembros);
    }

    public void validarAccesoAProyecto(UUID proyectoId) {
        RolUsuario rol = SecurityUtils.getCurrentUserRole();
        if (rol == RolUsuario.COORDINADOR) return;

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        boolean pertenece = miembroProyectoRepository.findByProyectoIdAndUsuarioIdAndActivoTrue(proyectoId, usuarioId).isPresent();
        if (!pertenece) {
            throw new ForbiddenException("No tiene permisos para acceder a este proyecto");
        }
    }

    private void validarPermisoEdicionProyecto(UUID proyectoId) {
        RolUsuario rol = SecurityUtils.getCurrentUserRole();
        if (rol == RolUsuario.COORDINADOR) return;

        if (rol == RolUsuario.ASESOR) {
            UUID usuarioId = SecurityUtils.getCurrentUserId();
            boolean esAsesor = miembroProyectoRepository.findByProyectoIdAndUsuarioIdAndActivoTrue(proyectoId, usuarioId)
                    .map(m -> m.getRolEnProyecto() == RolEnProyecto.ASESOR_LIDER || m.getRolEnProyecto() == RolEnProyecto.ASESOR_APOYO)
                    .orElse(false);
            if (esAsesor) return;
        }

        throw new ForbiddenException("No tiene permisos para editar la información de este proyecto");
    }

    private void validarPermisoGestionMiembros(UUID proyectoId, RolEnProyecto rolAAsignar) {
        RolUsuario rol = SecurityUtils.getCurrentUserRole();
        if (rol == RolUsuario.COORDINADOR) return;

        // Solo coordinador puede asignar asesores
        if (rolAAsignar == RolEnProyecto.ASESOR_LIDER || rolAAsignar == RolEnProyecto.ASESOR_APOYO) {
            throw new ForbiddenException("Solo el coordinador puede asignar o gestionar asesores");
        }

        // Asesor puede vincular estudiantes en sus proyectos
        if (rol == RolUsuario.ASESOR) {
            UUID usuarioId = SecurityUtils.getCurrentUserId();
            boolean esAsesor = miembroProyectoRepository.findByProyectoIdAndUsuarioIdAndActivoTrue(proyectoId, usuarioId)
                    .map(m -> m.getRolEnProyecto() == RolEnProyecto.ASESOR_LIDER || m.getRolEnProyecto() == RolEnProyecto.ASESOR_APOYO)
                    .orElse(false);
            if (esAsesor) return;
        }

        throw new ForbiddenException("No tiene permisos para gestionar miembros en este proyecto");
    }
}
