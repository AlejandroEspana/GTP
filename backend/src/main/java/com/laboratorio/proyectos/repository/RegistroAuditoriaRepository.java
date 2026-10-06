package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, UUID> {
    List<RegistroAuditoria> findAllByOrderByFechaHoraDesc();
    List<RegistroAuditoria> findTop100ByOrderByFechaHoraDesc();
    List<RegistroAuditoria> findByUsuarioIdOrderByFechaHoraDesc(UUID usuarioId);
    List<RegistroAuditoria> findByEntidadAfectadaOrderByFechaHoraDesc(String entidadAfectada);
    List<RegistroAuditoria> findByEntidadAfectadaAndEntidadIdOrderByFechaHoraDesc(String entidadAfectada, String entidadId);
}
