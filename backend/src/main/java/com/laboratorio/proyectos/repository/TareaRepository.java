package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.EstadoTarea;
import com.laboratorio.proyectos.domain.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, UUID> {
    List<Tarea> findByProyectoId(UUID proyectoId);
    List<Tarea> findByResponsableId(UUID responsableId);
    long countByProyectoIdAndEstado(UUID proyectoId, EstadoTarea estado);
    long countByProyectoId(UUID proyectoId);
    long countByEstado(EstadoTarea estado);
}
