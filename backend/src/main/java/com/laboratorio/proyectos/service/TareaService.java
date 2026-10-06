package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.Proyecto;
import com.laboratorio.proyectos.domain.Tarea;
import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.request.ActualizarTareaRequest;
import com.laboratorio.proyectos.dto.request.CambiarEstadoTareaRequest;
import com.laboratorio.proyectos.dto.request.CrearTareaRequest;
import com.laboratorio.proyectos.dto.response.TareaResponse;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.TareaMapper;
import com.laboratorio.proyectos.repository.ProyectoRepository;
import com.laboratorio.proyectos.repository.TareaRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaMapper tareaMapper;
    private final ProyectoService proyectoService;
    private final AuditoriaService auditoriaService;

    public TareaService(TareaRepository tareaRepository,
                        ProyectoRepository proyectoRepository,
                        UsuarioRepository usuarioRepository,
                        TareaMapper tareaMapper,
                        ProyectoService proyectoService,
                        AuditoriaService auditoriaService) {
        this.tareaRepository = tareaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaMapper = tareaMapper;
        this.proyectoService = proyectoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<TareaResponse> listarPorProyecto(UUID proyectoId) {
        proyectoService.validarAccesoAProyecto(proyectoId);
        return tareaMapper.toResponseList(tareaRepository.findByProyectoId(proyectoId));
    }

    @Transactional(readOnly = true)
    public TareaResponse obtenerPorId(UUID id) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));
        proyectoService.validarAccesoAProyecto(tarea.getProyecto().getId());
        return tareaMapper.toResponse(tarea);
    }

    @Transactional
    public TareaResponse crear(UUID proyectoId, CrearTareaRequest request, HttpServletRequest httpRequest) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        Usuario responsable = null;
        if (request.responsableId() != null) {
            responsable = usuarioRepository.findById(request.responsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));
        }

        Tarea tarea = new Tarea(
                proyecto,
                request.titulo(),
                request.descripcion(),
                responsable,
                request.prioridad(),
                request.fechaLimite(),
                request.etiquetas()
        );

        Tarea guardada = tareaRepository.save(tarea);

        auditoriaService.registrar("CREACION_TAREA", "Tarea", guardada.getId().toString(),
                "Tarea creada '" + guardada.getTitulo() + "' en el proyecto " + proyecto.getNombre(), httpRequest);

        return tareaMapper.toResponse(guardada);
    }

    @Transactional
    public TareaResponse actualizar(UUID id, ActualizarTareaRequest request, HttpServletRequest httpRequest) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        proyectoService.validarAccesoAProyecto(tarea.getProyecto().getId());

        Usuario responsable = null;
        if (request.responsableId() != null) {
            responsable = usuarioRepository.findById(request.responsableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Responsable no encontrado"));
        }

        tarea.asignarResponsable(responsable);
        tarea.actualizarDetalles(request.titulo(), request.descripcion(), request.prioridad(), request.fechaLimite(), request.etiquetas());

        Tarea actualizada = tareaRepository.save(tarea);

        auditoriaService.registrar("ACTUALIZACION_TAREA", "Tarea", id.toString(),
                "Tarea actualizada: " + actualizada.getTitulo(), httpRequest);

        return tareaMapper.toResponse(actualizada);
    }

    @Transactional
    public TareaResponse cambiarEstado(UUID id, CambiarEstadoTareaRequest request, HttpServletRequest httpRequest) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        proyectoService.validarAccesoAProyecto(tarea.getProyecto().getId());

        tarea.cambiarEstado(request.estado());
        Tarea actualizada = tareaRepository.save(tarea);

        auditoriaService.registrar("CAMBIO_ESTADO_TAREA", "Tarea", id.toString(),
                "Estado de tarea '" + tarea.getTitulo() + "' cambiado a " + request.estado(), httpRequest);

        return tareaMapper.toResponse(actualizada);
    }

    @Transactional
    public void eliminar(UUID id, HttpServletRequest httpRequest) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada"));

        proyectoService.validarAccesoAProyecto(tarea.getProyecto().getId());

        tareaRepository.delete(tarea);

        auditoriaService.registrar("ELIMINACION_TAREA", "Tarea", id.toString(),
                "Tarea eliminada: " + tarea.getTitulo(), httpRequest);
    }
}
