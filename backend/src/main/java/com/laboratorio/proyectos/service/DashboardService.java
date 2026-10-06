package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.*;
import com.laboratorio.proyectos.dto.response.DashboardGlobalResponse;
import com.laboratorio.proyectos.dto.response.DashboardProyectoResponse;
import com.laboratorio.proyectos.dto.response.ProyectoResponse;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.ProyectoMapper;
import com.laboratorio.proyectos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {

    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaRepository tareaRepository;
    private final DocumentoRepository documentoRepository;
    private final EvidenciaMultimediaRepository evidenciaRepository;
    private final EntradaBitacoraRepository bitacoraRepository;
    private final ProyectoMapper proyectoMapper;
    private final ProyectoService proyectoService;

    public DashboardService(ProyectoRepository proyectoRepository,
                            UsuarioRepository usuarioRepository,
                            TareaRepository tareaRepository,
                            DocumentoRepository documentoRepository,
                            EvidenciaMultimediaRepository evidenciaRepository,
                            EntradaBitacoraRepository bitacoraRepository,
                            ProyectoMapper proyectoMapper,
                            ProyectoService proyectoService) {
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tareaRepository = tareaRepository;
        this.documentoRepository = documentoRepository;
        this.evidenciaRepository = evidenciaRepository;
        this.bitacoraRepository = bitacoraRepository;
        this.proyectoMapper = proyectoMapper;
        this.proyectoService = proyectoService;
    }

    @Transactional(readOnly = true)
    public DashboardGlobalResponse obtenerDashboardGlobal() {
        long totalProyectos = proyectoRepository.count();
        long proyectosActivos = proyectoRepository.countByEstado(EstadoProyecto.EN_DESARROLLO);
        long proyectosFinalizados = proyectoRepository.countByEstado(EstadoProyecto.FINALIZADO);
        long proyectosPlaneacion = proyectoRepository.countByEstado(EstadoProyecto.PLANEACION);

        long totalEstudiantes = usuarioRepository.countByRol(RolUsuario.ESTUDIANTE);
        long totalAsesores = usuarioRepository.countByRol(RolUsuario.ASESOR);

        long totalDocumentos = documentoRepository.count();
        long totalTareas = tareaRepository.count();
        long tareasCompletadas = tareaRepository.countByEstado(EstadoTarea.COMPLETADA);

        List<Proyecto> todosProyectos = proyectoRepository.findAll();
        List<ProyectoResponse> proyectosSinActividad = new ArrayList<>();

        for (Proyecto p : todosProyectos) {
            if (p.getEstado() == EstadoProyecto.EN_DESARROLLO) {
                List<EntradaBitacora> bitacoras = bitacoraRepository.findByProyectoIdOrderByFechaHoraDesc(p.getId());
                if (bitacoras.isEmpty()) {
                    proyectosSinActividad.add(proyectoMapper.toResponse(p));
                } else {
                    LocalDateTime ultima = bitacoras.get(0).getFechaHora();
                    if (ultima.isBefore(LocalDateTime.now().minusDays(15))) {
                        proyectosSinActividad.add(proyectoMapper.toResponse(p));
                    }
                }
            }
        }

        return new DashboardGlobalResponse(
                totalProyectos,
                proyectosActivos,
                proyectosFinalizados,
                proyectosPlaneacion,
                totalEstudiantes,
                totalAsesores,
                totalDocumentos,
                totalTareas,
                tareasCompletadas,
                proyectosSinActividad
        );
    }

    @Transactional(readOnly = true)
    public DashboardProyectoResponse obtenerDashboardProyecto(UUID proyectoId) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        long totalTareas = tareaRepository.countByProyectoId(proyectoId);
        long tareasPendientes = tareaRepository.countByProyectoIdAndEstado(proyectoId, EstadoTarea.PENDIENTE);
        long tareasEnDesarrollo = tareaRepository.countByProyectoIdAndEstado(proyectoId, EstadoTarea.EN_DESARROLLO);
        long tareasCompletadas = tareaRepository.countByProyectoIdAndEstado(proyectoId, EstadoTarea.COMPLETADA);

        double progreso = totalTareas > 0
                ? Math.round(((double) tareasCompletadas / totalTareas) * 100.0 * 10.0) / 10.0
                : 0.0;

        long totalDocs = documentoRepository.countByProyectoId(proyectoId);
        long totalEvidencias = evidenciaRepository.findByProyectoId(proyectoId).size();
        long totalMiembros = proyecto.getMiembros().stream().filter(MiembroProyecto::isActivo).count();

        List<EntradaBitacora> bitacoras = bitacoraRepository.findByProyectoIdOrderByFechaHoraDesc(proyectoId);
        LocalDateTime ultimaActividad = bitacoras.isEmpty() ? null : bitacoras.get(0).getFechaHora();

        return new DashboardProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getEstado(),
                totalTareas,
                tareasPendientes,
                tareasEnDesarrollo,
                tareasCompletadas,
                progreso,
                totalDocs,
                totalEvidencias,
                totalMiembros,
                ultimaActividad
        );
    }
}
