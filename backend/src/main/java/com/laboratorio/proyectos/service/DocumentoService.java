package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.*;
import com.laboratorio.proyectos.dto.request.CrearDocumentoRequest;
import com.laboratorio.proyectos.dto.request.SubirVersionRequest;
import com.laboratorio.proyectos.dto.response.DocumentoResponse;
import com.laboratorio.proyectos.dto.response.VersionDocumentoResponse;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.DocumentoMapper;
import com.laboratorio.proyectos.repository.DocumentoRepository;
import com.laboratorio.proyectos.repository.ProyectoRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.repository.VersionDocumentoRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final VersionDocumentoRepository versionDocumentoRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;
    private final DocumentoMapper documentoMapper;
    private final ProyectoService proyectoService;
    private final AuditoriaService auditoriaService;

    public DocumentoService(DocumentoRepository documentoRepository,
                            VersionDocumentoRepository versionDocumentoRepository,
                            ProyectoRepository proyectoRepository,
                            UsuarioRepository usuarioRepository,
                            FileStorageService fileStorageService,
                            DocumentoMapper documentoMapper,
                            ProyectoService proyectoService,
                            AuditoriaService auditoriaService) {
        this.documentoRepository = documentoRepository;
        this.versionDocumentoRepository = versionDocumentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
        this.documentoMapper = documentoMapper;
        this.proyectoService = proyectoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarPorProyecto(UUID proyectoId, CategoriaDocumento categoria) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        List<Documento> docs = (categoria != null)
                ? documentoRepository.findByProyectoIdAndCategoria(proyectoId, categoria)
                : documentoRepository.findByProyectoId(proyectoId);

        return documentoMapper.toResponseList(docs);
    }

    @Transactional(readOnly = true)
    public DocumentoResponse obtenerPorId(UUID id) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));
        proyectoService.validarAccesoAProyecto(documento.getProyecto().getId());
        return documentoMapper.toResponse(documento);
    }

    @Transactional
    public DocumentoResponse crear(UUID proyectoId, CrearDocumentoRequest request, MultipartFile archivo, HttpServletRequest httpRequest) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autor no encontrado"));

        FileStorageService.ArchivoGuardado archivoGuardado = fileStorageService.guardarArchivo(archivo);

        Documento documento = new Documento(
                proyecto,
                request.titulo(),
                request.descripcion(),
                request.categoria()
        );

        VersionDocumento versionInicial = documento.agregarVersion(
                autor,
                archivoGuardado.urlDescarga(),
                archivoGuardado.nombreOriginal(),
                request.resumenCambios() != null ? request.resumenCambios() : "Versión inicial",
                archivoGuardado.tamanoBytes(),
                archivoGuardado.tipoContenido()
        );

        Documento guardado = documentoRepository.save(documento);

        auditoriaService.registrar("SUBIDA_DOCUMENTO", "Documento", guardado.getId().toString(),
                "Documento subido: " + guardado.getTitulo() + " (v1) en proyecto " + proyecto.getNombre(), httpRequest);

        return documentoMapper.toResponse(guardado);
    }

    @Transactional
    public VersionDocumentoResponse agregarVersion(UUID documentoId, SubirVersionRequest request, MultipartFile archivo, HttpServletRequest httpRequest) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado"));

        proyectoService.validarAccesoAProyecto(documento.getProyecto().getId());

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autor no encontrado"));

        FileStorageService.ArchivoGuardado archivoGuardado = fileStorageService.guardarArchivo(archivo);

        VersionDocumento nuevaVersion = documento.agregarVersion(
                autor,
                archivoGuardado.urlDescarga(),
                archivoGuardado.nombreOriginal(),
                request.resumenCambios(),
                archivoGuardado.tamanoBytes(),
                archivoGuardado.tipoContenido()
        );

        VersionDocumento versionGuardada = versionDocumentoRepository.save(nuevaVersion);

        auditoriaService.registrar("NUEVA_VERSION_DOCUMENTO", "Documento", documentoId.toString(),
                "Nueva versión v" + nuevaVersion.getNumeroVersion() + " subida para documento: " + documento.getTitulo(), httpRequest);

        return documentoMapper.toVersionResponse(versionGuardada);
    }
}
