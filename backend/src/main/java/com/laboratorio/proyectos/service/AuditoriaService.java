package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.RegistroAuditoria;
import com.laboratorio.proyectos.domain.RolUsuario;
import com.laboratorio.proyectos.dto.response.AuditoriaResponse;
import com.laboratorio.proyectos.repository.RegistroAuditoriaRepository;
import com.laboratorio.proyectos.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository auditoriaRepository;

    public AuditoriaService(RegistroAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String accion, String entidadAfectada, String entidadId, String detalles, HttpServletRequest request) {
        try {
            UUID usuarioId = null;
            try {
                usuarioId = SecurityUtils.getCurrentUserId();
            } catch (Exception ignored) {}

            String correo = SecurityUtils.getCurrentUserEmail();
            RolUsuario rol = SecurityUtils.getCurrentUserRole();
            String rolStr = (rol != null) ? rol.name() : "ANONIMO";
            String ip = (request != null) ? request.getRemoteAddr() : "127.0.0.1";

            RegistroAuditoria audit = new RegistroAuditoria(
                    accion, usuarioId, correo, rolStr, entidadAfectada, entidadId, detalles, ip);
            auditoriaRepository.save(audit);
        } catch (Exception e) {
            // No interrumpir la transacción principal si falla la auditoría
        }
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarTodas() {
        return auditoriaRepository.findTop100ByOrderByFechaHoraDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarPorUsuario(UUID usuarioId) {
        return auditoriaRepository.findByUsuarioIdOrderByFechaHoraDesc(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarPorEntidad(String entidadAfectada) {
        return auditoriaRepository.findByEntidadAfectadaOrderByFechaHoraDesc(entidadAfectada).stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditoriaResponse toResponse(RegistroAuditoria a) {
        return new AuditoriaResponse(
                a.getId(),
                a.getAccion(),
                a.getUsuarioId(),
                a.getCorreoUsuario(),
                a.getRolUsuario(),
                a.getEntidadAfectada(),
                a.getEntidadId(),
                a.getDetalles(),
                a.getFechaHora(),
                a.getIpOrigen()
        );
    }
}
