package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.VersionDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VersionDocumentoRepository extends JpaRepository<VersionDocumento, UUID> {
    List<VersionDocumento> findByDocumentoIdOrderByNumeroVersionDesc(UUID documentoId);
}
