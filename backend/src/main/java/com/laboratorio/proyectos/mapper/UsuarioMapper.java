package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
