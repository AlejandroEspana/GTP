package com.laboratorio.proyectos.mapper;

import com.laboratorio.proyectos.domain.EntradaBitacora;
import com.laboratorio.proyectos.dto.response.BitacoraResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BitacoraMapper {

    public BitacoraResponse toResponse(EntradaBitacora bitacora) {
        if (bitacora == null) return null;

        return new BitacoraResponse(
                bitacora.getId(),
                bitacora.getProyecto().getId(),
                bitacora.getAutor().getId(),
                bitacora.getAutor().getNombreCompleto(),
                bitacora.getFechaHora(),
                bitacora.getTitulo(),
                bitacora.getContenido(),
                bitacora.getTipo()
        );
    }

    public List<BitacoraResponse> toResponseList(List<EntradaBitacora> lista) {
        if (lista == null) return List.of();
        return lista.stream().map(this::toResponse).toList();
    }
}
