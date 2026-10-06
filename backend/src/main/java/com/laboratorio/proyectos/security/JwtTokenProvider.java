package com.laboratorio.proyectos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.expirationMs = expirationMs;
        SecretKey secretKey;
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            secretKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.key = secretKey;
    }

    public String generarToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getId().toString())
                .claim("rol", userDetails.getRol().name())
                .claim("nombreCompleto", userDetails.getNombreCompleto())
                .issuedAt(ahora)
                .expiration(fechaExpiracion)
                .signWith(key)
                .compact();
    }

    public String generarToken(UUID usuarioId, String correo, String rol, String nombreCompleto) {
        Date ahora = new Date();
        Date fechaExpiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(correo)
                .claim("userId", usuarioId.toString())
                .claim("rol", rol)
                .claim("nombreCompleto", nombreCompleto)
                .issuedAt(ahora)
                .expiration(fechaExpiracion)
                .signWith(key)
                .compact();
    }

    public String obtenerCorreoDelToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
