package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.MiembroProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MiembroProyectoRepository extends JpaRepository<MiembroProyecto, UUID> {
    List<MiembroProyecto> findByProyectoId(UUID proyectoId);
    List<MiembroProyecto> findByUsuarioId(UUID usuarioId);
    Optional<MiembroProyecto> findByProyectoIdAndUsuarioIdAndActivoTrue(UUID proyectoId, UUID usuarioId);
}
