package com.laboratorio.proyectos.service;

import com.laboratorio.proyectos.domain.Usuario;
import com.laboratorio.proyectos.dto.request.LoginRequest;
import com.laboratorio.proyectos.dto.request.RegistroUsuarioRequest;
import com.laboratorio.proyectos.dto.response.AuthResponse;
import com.laboratorio.proyectos.exception.BadRequestException;
import com.laboratorio.proyectos.exception.ForbiddenException;
import com.laboratorio.proyectos.repository.UsuarioRepository;
import com.laboratorio.proyectos.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo().trim().toLowerCase(), request.password())
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.correo().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new ForbiddenException("El usuario se encuentra inactivo. Comuníquese con la coordinación.");
        }

        String token = tokenProvider.generarToken(authentication);

        auditoriaService.registrar("INICIO_SESION", "Usuario", usuario.getId().toString(),
                "Inicio de sesión exitoso de " + usuario.getCorreo(), httpRequest);

        return new AuthResponse(
                token,
                usuario.getId(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.getCodigo()
        );
    }

    @Transactional
    public AuthResponse registrar(RegistroUsuarioRequest request, HttpServletRequest httpRequest) {
        String correoLimpio = request.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correoLimpio)) {
            throw new BadRequestException("El correo ya está registrado en la plataforma");
        }

        Usuario usuario = new Usuario(
                request.codigo(),
                request.nombres(),
                request.apellidos(),
                correoLimpio,
                passwordEncoder.encode(request.password()),
                request.programaAcademico(),
                request.semestre(),
                request.fechaIngresoLaboratorio(),
                request.rol()
        );

        Usuario guardado = usuarioRepository.save(usuario);

        auditoriaService.registrar("REGISTRO_USUARIO", "Usuario", guardado.getId().toString(),
                "Nuevo usuario registrado con rol " + guardado.getRol(), httpRequest);

        String token = tokenProvider.generarToken(
                guardado.getId(),
                guardado.getCorreo(),
                guardado.getRol().name(),
                guardado.getNombreCompleto()
        );

        return new AuthResponse(
                token,
                guardado.getId(),
                guardado.getCorreo(),
                guardado.getNombreCompleto(),
                guardado.getRol(),
                guardado.getCodigo()
        );
    }
}
