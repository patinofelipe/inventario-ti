package br.com.inventarioti.dto;

public record UsuarioResumo(
        String nome,
        String email,
        String perfil,
        boolean administrador
) {
}
