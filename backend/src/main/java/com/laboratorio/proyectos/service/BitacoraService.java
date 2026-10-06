package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.EntradaBitacora;
import com.laboratorio.proyectos.domain.Proyecto;
import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.request.CrearBitacoraRequest;
import com.laboratorio.proyectos.dto.response.BitacoraResponse;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.BitacoraMapper;
import com.laboratorio.proyectos.repository.EntradaBitacoraRepository;
import com.laboratorio.proyectos.repository.ProyectoRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BitacoraService {

    private final EntradaBitacoraRepository bitacoraRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BitacoraMapper bitacoraMapper;
    private final ProyectoService proyectoService;
    private final AuditoriaService auditoriaService;

    public BitacoraService(EntradaBitacoraRepository bitacoraRepository,
                           ProyectoRepository proyectoRepository,
                           UsuarioRepository usuarioRepository,
                           BitacoraMapper bitacoraMapper,
                           ProyectoService proyectoService,
                           AuditoriaService auditoriaService) {
        this.bitacoraRepository = bitacoraRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.bitacoraMapper = bitacoraMapper;
        this.proyectoService = proyectoService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public List<BitacoraResponse> listarPorProyecto(UUID proyectoId) {
        proyectoService.validarAccesoAProyecto(proyectoId);
        return bitacoraMapper.toResponseList(bitacoraRepository.findByProyectoIdOrderByFechaHoraDesc(proyectoId));
    }

    @Transactional
    public BitacoraResponse crear(UUID proyectoId, CrearBitacoraRequest request, HttpServletRequest httpRequest) {
        proyectoService.validarAccesoAProyecto(proyectoId);

        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        UUID usuarioId = SecurityUtils.getCurrentUserId();
        Usuario autor = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autor no encontrado"));

        EntradaBitacora entrada = new EntradaBitacora(
                proyecto,
                autor,
                request.titulo(),
                request.contenido(),
                request.tipo()
        );

        EntradaBitacora guardada = bitacoraRepository.save(entrada);

        auditoriaService.registrar("REGISTRO_BITACORA", "EntradaBitacora", guardada.getId().toString(),
                "Entrada registrada en bitácora (" + guardada.getTipo() + "): " + guardada.getTitulo(), httpRequest);

        return bitacoraMapper.toResponse(guardada);
    }
}
