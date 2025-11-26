package com.mapadavida.mdvBackend.models.dto;

/**
 * Simple response wrapper for authentication responses.
 * Contains the JWT token and a safe DTO representation of the user.
 */
public record AuthResponse(String token, UsuarioDTO usuario) {}


