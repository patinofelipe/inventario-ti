
package br.com.inventarioti.service;

import br.com.inventarioti.model.Perfil;
import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CadastroUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    public CadastroUsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            EntityManager entityManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
    }

    @Transactional
    public void cadastrar(
            String nome,
            String email,
            String senha,
            String nomePerfil) {

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

        if (nomePerfil == null
                || (!nomePerfil.equals("USUARIO")
                && !nomePerfil.equals("CLIENTE"))) {
            throw new IllegalArgumentException(
                    "Perfil de acesso inválido."
            );
        }

        String emailNormalizado =
                email.trim().toLowerCase(Locale.ROOT);

        if (usuarioRepository
                .findByEmailIgnoreCase(emailNormalizado)
                .isPresent()) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        Long idPerfil = nomePerfil.equals("USUARIO") ? 2L : 3L;

        Perfil perfil = entityManager.find(Perfil.class, idPerfil);

        if (perfil == null
                || !nomePerfil.equals(perfil.getNome())) {
            throw new IllegalStateException(
                    "O perfil selecionado não está configurado."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        usuario.getPerfis().add(perfil);

        usuarioRepository.saveAndFlush(usuario);
    }
}
