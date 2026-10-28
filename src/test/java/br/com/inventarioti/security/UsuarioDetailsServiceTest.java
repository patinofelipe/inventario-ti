package br.com.inventarioti.security;

import br.com.inventarioti.model.Perfil;
import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioDetailsServiceTest {

    private UsuarioRepository usuarioRepository;
    private UsuarioDetailsService service;
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        usuarioRepository = mock(UsuarioRepository.class);
        service = new UsuarioDetailsService(usuarioRepository);

        Perfil perfil = new Perfil();
        try {
            var campoNome = Perfil.class.getDeclaredField("nome");
            campoNome.setAccessible(true);
            campoNome.set(perfil, "USUARIO");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Não foi possível configurar o perfil de teste.", e);
        }

        usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail("teste@example.com");
        usuario.setSenhaHash("$2a$10$hashDeTeste");
        usuario.setAtivo(true);
        usuario.setEmailVerificado(true);
        usuario.getPerfis().add(perfil);

        when(usuarioRepository.findByEmailIgnoreCase("teste@example.com"))
                .thenReturn(Optional.of(usuario));
    }

    @Test
    void deveCarregarUsuarioAtivoComEmailVerificado() {
        UserDetails detalhes =
                service.loadUserByUsername("teste@example.com");

        assertEquals("teste@example.com", detalhes.getUsername());
        assertTrue(detalhes.isEnabled());
        assertTrue(detalhes.isAccountNonLocked());
        assertTrue(detalhes.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
    }

    @Test
    void deveMarcarUsuarioComEmailNaoVerificadoComoDesabilitado() {
        usuario.setEmailVerificado(false);

        UserDetails detalhes =
                service.loadUserByUsername("teste@example.com");

        assertFalse(detalhes.isEnabled(),
                "Usuário com e-mail não verificado não deve poder autenticar.");
    }

    @Test
    void deveRecusarUsuarioInexistente() {
        when(usuarioRepository.findByEmailIgnoreCase("ausente@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("ausente@example.com")
        );
    }

    @Test
    void deveMarcarUsuarioInativoComoDesabilitado() {
        usuario.setAtivo(false);

        UserDetails detalhes =
                service.loadUserByUsername("teste@example.com");

        assertFalse(detalhes.isEnabled());
    }

    @Test
    void deveMarcarUsuarioComBloqueioAtivoComoBloqueado() {
        usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(10));

        UserDetails detalhes =
                service.loadUserByUsername("teste@example.com");

        assertFalse(detalhes.isAccountNonLocked());
    }

    @Test
    void deveNormalizarEmailInformado() {
        service.loadUserByUsername(" TESTE@EXAMPLE.COM ");

        verify(usuarioRepository)
                .findByEmailIgnoreCase("teste@example.com");
    }
}
