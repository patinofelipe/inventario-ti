
package br.com.inventarioti.security;

import br.com.inventarioti.model.Perfil;
import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.Locale;
import jakarta.persistence.LockModeType;

@Service
public class CadastroInicialService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public CadastroInicialService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            EntityManager entityManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Transactional
    public void criarAdministrador(
            String nome, String email, String senha) {

        Perfil perfilAdm = entityManager.find(
        Perfil.class,
        1L,
        LockModeType.PESSIMISTIC_WRITE
);

if (perfilAdm == null || !"ADM".equals(perfilAdm.getNome())) {
    throw new IllegalStateException(
            "O perfil ADM não está configurado corretamente."
    );
}

if (usuarioRepository.count() > 0) {
    throw new IllegalStateException(
            "O cadastro inicial já foi realizado."
    );
}

        if (nome == null || nome.isBlank()
                || nome.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "Informe um nome válido."
            );
        }

        if (email == null || email.isBlank()
                || email.trim().length() > 254
                || !email.trim().matches(
                    "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Informe um e-mail válido."
            );
        }

        if (senha == null || senha.length() < 12
                || senha.length() > 72) {
            throw new IllegalArgumentException(
                    "A senha deve ter entre 12 e 72 caracteres."
            );
        }


        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim().toLowerCase(Locale.ROOT));
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        usuario.getPerfis().add(perfilAdm);

        usuarioRepository.saveAndFlush(usuario);
    }
}
