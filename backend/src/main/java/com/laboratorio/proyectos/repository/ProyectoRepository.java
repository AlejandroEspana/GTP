package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.EstadoProyecto;
import com.laboratorio.proyectos.domain.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, UUID> {
    List<Proyecto> findByEstado(EstadoProyecto estado);

    @Query("SELECT DISTINCT p FROM Proyecto p JOIN p.miembros m WHERE m.usuario.id = :usuarioId AND m.activo = true")
    List<Proyecto> findProyectosByUsuarioId(@Param("usuarioId") UUID usuarioId);

    long countByEstado(EstadoProyecto estado);
}
