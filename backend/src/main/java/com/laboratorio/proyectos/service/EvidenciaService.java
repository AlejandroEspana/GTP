package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.CategoriaEvidencia;
import com.laboratorio.proyectos.domain.EvidenciaMultimedia;
import com.laboratorio.proyectos.domain.Proyecto;
import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.request.CrearEvidenciaRequest;
import com.laboratorio.proyectos.dto.response.EvidenciaResponse;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.EvidenciaMapper;
import com.laboratorio.proyectos.repository.EvidenciaMultimediaRepository;
import com.laboratorio.proyectos.repository.ProyectoRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class EvidenciaService {

    private final EvidenciaMultimediaRepository evidenciaRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;
    private final EvidenciaMapper evidenciaMapper;
    private final ProyectoService proyectoService;
    private final AuditoriaService auditoriaService;

    public EvidenciaService(EvidenciaMultimediaRepository evidenciaRepository,
                            ProyectoRepository proyectoRepository,
                            UsuarioRepository usuarioRepository,
                            FileStorageService fileStorageService,
                            EvidenciaMapper evidenciaMapper,
                            ProyectoService proyectoService,
                            AuditoriaService auditoriaService) {
        this.evidenciaRepository = evidenciaRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
        this.evidenciaMapper = evidenciaMapper;
        this.proyectoService = proyectoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaResponse> listarPorProyecto(UUID proyectoId, CategoriaEvidencia categoria) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        List<EvidenciaMultimedia> list = (categoria != null)
                ? evidenciaRepository.findByProyectoIdAndCategoria(proyectoId, categoria)
                : evidenciaRepository.findByProyectoId(proyectoId);

        return evidenciaMapper.toResponseList(list);
    }

    @Transactional
    public EvidenciaResponse crear(UUID proyectoId, CrearEvidenciaRequest request, MultipartFile archivo, HttpServletRequest httpRequest) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autor no encontrado"));

        FileStorageService.ArchivoGuardado guardado = fileStorageService.guardarArchivo(archivo);

        EvidenciaMultimedia evidencia = new EvidenciaMultimedia(
                proyecto,
                request.titulo(),
                request.descripcion(),
                autor,
                request.categoria(),
                guardado.urlDescarga(),
                guardado.tipoContenido()
        );

        EvidenciaMultimedia guardada = evidenciaRepository.save(evidencia);

        auditoriaService.registrar("SUBIDA_EVIDENCIA", "EvidenciaMultimedia", guardada.getId().toString(),
                "Evidencia subida: " + guardada.getTitulo() + " en proyecto " + proyecto.getNombre(), httpRequest);

        return evidenciaMapper.toResponse(guardada);
    }

    @Transactional
    public void eliminar(UUID id, HttpServletRequest httpRequest) {
        EvidenciaMultimedia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia no encontrada"));

        proyectoService.validarAccesoAProyecto(evidencia.getProyecto().getId());

        evidenciaRepository.delete(evidencia);

        auditoriaService.registrar("ELIMINACION_EVIDENCIA", "EvidenciaMultimedia", id.toString(),
                "Evidencia eliminada: " + evidencia.getTitulo(), httpRequest);
    }
}
