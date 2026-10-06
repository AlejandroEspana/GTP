package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.EntradaBitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntradaBitacoraRepository extends JpaRepository<EntradaBitacora, UUID> {
    List<EntradaBitacora> findByProyectoIdOrderByFechaHoraDesc(UUID proyectoId);
}
