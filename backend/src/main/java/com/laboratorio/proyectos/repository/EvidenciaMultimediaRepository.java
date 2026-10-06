package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.CategoriaEvidencia;
import com.laboratorio.proyectos.domain.EvidenciaMultimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvidenciaMultimediaRepository extends JpaRepository<EvidenciaMultimedia, UUID> {
    List<EvidenciaMultimedia> findByProyectoId(UUID proyectoId);
    List<EvidenciaMultimedia> findByProyectoIdAndCategoria(UUID proyectoId, CategoriaEvidencia categoria);
}
