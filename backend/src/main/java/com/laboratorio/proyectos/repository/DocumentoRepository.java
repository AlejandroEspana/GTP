package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.CategoriaDocumento;
import com.laboratorio.proyectos.domain.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {
    List<Documento> findByProyectoId(UUID proyectoId);
    List<Documento> findByProyectoIdAndCategoria(UUID proyectoId, CategoriaDocumento categoria);
    long countByProyectoId(UUID proyectoId);
}
