package br.com.inventarioti.security;

import br.com.inventarioti.dto.UsuarioResumo;
import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.stream.Collectors;

@ControllerAdvice
public class UsuarioLogadoAdvice {

    private final UsuarioRepository usuarioRepository;

    public UsuarioLogadoAdvice(
            UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioLogado")
    public UsuarioResumo usuarioLogado(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        Usuario usuario = usuarioRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElse(null);

        if (usuario == null) {
            return null;
        }

        String perfil = usuario.getPerfis()
                .stream()
                .map(p -> switch (p.getNome()) {
                    case "ADM" -> "Administrador";
                    case "USUARIO" -> "Usuário";
                    case "CLIENTE" -> "Cliente";
                    default -> p.getNome();
                })
                .collect(Collectors.joining(", "));

        boolean administrador = usuario.getPerfis()
                .stream()
                .anyMatch(p -> "ADM".equals(p.getNome()));

        return new UsuarioResumo(
                usuario.getNome(),
                usuario.getEmail(),
                perfil,
                administrador
        );
    }
}
