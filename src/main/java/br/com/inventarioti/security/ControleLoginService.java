package br.com.inventarioti.security;

import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ControleLoginService {

    private static final int LIMITE_TENTATIVAS = 5;
    private static final int MINUTOS_BLOQUEIO = 15;

    private final UsuarioRepository usuarioRepository;

    public ControleLoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void registrarFalha(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<Usuario> resultado =
                usuarioRepository.buscarPorEmailParaAtualizacao(
                        email.trim()
                );

        if (resultado.isEmpty()) {
            return;
        }

        Usuario usuario = resultado.get();

        LocalDateTime agora = LocalDateTime.now();

        if (usuario.getBloqueadoAte() != null) {
    if (usuario.getBloqueadoAte().isAfter(agora)) {
        return;
    }

    // O bloqueio expirou: iniciar uma nova contagem.
    usuario.setBloqueadoAte(null);
    usuario.setTentativasFalhas(0);
}

usuario.setTentativasFalhas(
        usuario.getTentativasFalhas() + 1
);

        if (usuario.getTentativasFalhas() >= LIMITE_TENTATIVAS) {
            usuario.setBloqueadoAte(
                    agora.plusMinutes(MINUTOS_BLOQUEIO)
            );
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void registrarSucesso(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        usuarioRepository.buscarPorEmailParaAtualizacao(email.trim())
                .ifPresent(usuario -> {
                    usuario.setTentativasFalhas(0);
                    usuario.setBloqueadoAte(null);
                    usuario.setUltimoLogin(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                });
    }
}
