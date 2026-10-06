package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.RegistroAuditoria;
import com.laboratorio.proyectos.domain.RolUsuario;
import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.request.ActualizarUsuarioRequest;
import com.laboratorio.proyectos.dto.request.RegistroUsuarioRequest;
import com.laboratorio.proyectos.dto.response.FichaIntegranteResponse;
import com.laboratorio.proyectos.dto.response.UsuarioResponse;
import com.laboratorio.proyectos.exception.BadRequestException;
import com.laboratorio.proyectos.exception.ForbiddenException;
import com.laboratorio.proyectos.exception.ResourceNotFoundException;
import com.laboratorio.proyectos.mapper.UsuarioMapper;
import com.laboratorio.proyectos.repository.RegistroAuditoriaRepository;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;
    private final RegistroAuditoriaRepository auditoriaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder,
                          AuditoriaService auditoriaService,
                          RegistroAuditoriaRepository auditoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioMapper.toResponseList(usuarioRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPorRol(RolUsuario rol) {
        return usuarioMapper.toResponseList(usuarioRepository.findByRol(rol));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public FichaIntegranteResponse obtenerFichaIntegrante(UUID id, HttpServletRequest httpRequest) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        RolUsuario currentUserRole = SecurityUtils.getCurrentUserRole();

        // Si es estudiante solo puede ver su propia ficha completa
        if (currentUserRole == RolUsuario.ESTUDIANTE && !currentUserId.equals(id)) {
            throw new ForbiddenException("No tiene permisos para ver la ficha completa de otro estudiante");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Integrante no encontrado"));

        auditoriaService.registrar("CONSULTA_FICHA_PERSONAL", "Usuario", id.toString(),
                "Consulta de ficha personal del integrante " + usuario.getCorreo(), httpRequest);

        List<FichaIntegranteResponse.ParticipacionProyectoDto> participaciones = usuario.getMembresias().stream()
                .map(m -> new FichaIntegranteResponse.ParticipacionProyectoDto(
                        m.getProyecto().getId(),
                        m.getProyecto().getNombre(),
                        m.getRolEnProyecto(),
                        m.getPeriodo() != null ? m.getPeriodo().fechaInicio() : null,
                        m.getPeriodo() != null ? m.getPeriodo().fechaFin() : null,
                        m.isActivo()
                ))
                .toList();

        List<RegistroAuditoria> logs = auditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(id);
        List<FichaIntegranteResponse.ActividadRecienteDto> actividadesRecientes = logs.stream()
                .limit(10)
                .map(l -> new FichaIntegranteResponse.ActividadRecienteDto(
                        l.getAccion(),
                        l.getEntidadAfectada(),
                        l.getDetalles(),
                        l.getFechaHora()
                ))
                .toList();

        return new FichaIntegranteResponse(
                usuario.getId(),
                usuario.getCodigo(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getProgramaAcademico(),
                usuario.getSemestre(),
                usuario.getFechaIngresoLaboratorio(),
                usuario.getRol(),
                usuario.isActivo(),
                participaciones,
                actividadesRecientes
        );
    }

    @Transactional
    public UsuarioResponse crearUsuario(RegistroUsuarioRequest request, HttpServletRequest httpRequest) {
        String correoLimpio = request.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new BadRequestException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario(
                request.codigo(),
                request.nombres(),
                request.apellidos(),
                correoLimpio,
                passwordEncoder.encode(request.password()),
                request.programaAcademico(),
                request.semestre(),
                request.fechaIngresoLaboratorio(),
                request.rol()
        );

        Usuario guardado = usuarioRepository.save(usuario);
        auditoriaService.registrar("CREACION_USUARIO", "Usuario", guardado.getId().toString(),
                "Creación de usuario " + guardado.getCorreo() + " con rol " + guardado.getRol(), httpRequest);

        return usuarioMapper.toResponse(guardado);
    }

    @Transactional
    public UsuarioResponse actualizarUsuario(UUID id, ActualizarUsuarioRequest request, HttpServletRequest httpRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.actualizarPerfil(request.nombres(), request.apellidos(), request.programaAcademico(), request.semestre());

        if (request.rol() != null) {
            usuario.cambiarRol(request.rol());
        }

        if (request.activo() != null) {
            if (request.activo()) {
                usuario.activar();
            } else {
                usuario.desactivar();
            }
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        auditoriaService.registrar("ACTUALIZACION_USUARIO", "Usuario", id.toString(),
                "Actualización de datos para " + actualizado.getCorreo(), httpRequest);

        return usuarioMapper.toResponse(actualizado);
    }

    @Transactional
    public void cambiarEstadoActivo(UUID id, boolean activar, HttpServletRequest httpRequest) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (activar) {
            usuario.activar();
        } else {
            usuario.desactivar();
        }

        usuarioRepository.save(usuario);
        auditoriaService.registrar(activar ? "ACTIVACION_USUARIO" : "DESACTIVACION_USUARIO",
                "Usuario", id.toString(), "Estado activo cambiado a " + activar, httpRequest);
    }
}
