package com.laboratorio.proyectos.repository;

import com.laboratorio.proyectos.domain.RolUsuario;
import com.laboratorio.proyectos.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Usuario> findByRol(RolUsuario rol);
    List<Usuario> findByActivoTrue();
    long countByRol(RolUsuario rol);
}
