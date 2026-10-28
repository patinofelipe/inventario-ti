package br.com.inventarioti.security;

import br.com.inventarioti.model.Usuario;
import br.com.inventarioti.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControleLoginServiceTest {

    private UsuarioRepository usuarioRepository;
    private ControleLoginService controleLoginService;
    private Usuario usuario;

    @BeforeEach
    void configurar() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        controleLoginService = new ControleLoginService(usuarioRepository);

        usuario = new Usuario();
        usuario.setEmail("teste@example.com");
        usuario.setAtivo(true);
        usuario.setTentativasFalhas(0);
        usuario.setBloqueadoAte(null);
    }

    @Test
    void deveRegistrarUmaFalha() {
        when(usuarioRepository.buscarPorEmailParaAtualizacao("teste@example.com"))
                .thenReturn(Optional.of(usuario));

        controleLoginService.registrarFalha("teste@example.com");

        assertEquals(1, usuario.getTentativasFalhas());
        assertNull(usuario.getBloqueadoAte());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveBloquearNaQuintaFalha() {
        usuario.setTentativasFalhas(4);

        when(usuarioRepository.buscarPorEmailParaAtualizacao("teste@example.com"))
                .thenReturn(Optional.of(usuario));

        controleLoginService.registrarFalha("teste@example.com");

        assertEquals(5, usuario.getTentativasFalhas());
        assertNotNull(usuario.getBloqueadoAte());
        assertTrue(usuario.getBloqueadoAte().isAfter(LocalDateTime.now()));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLimparContadorNoLoginBemSucedido() {
        usuario.setTentativasFalhas(3);
        usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(10));

        when(usuarioRepository.buscarPorEmailParaAtualizacao("teste@example.com"))
                .thenReturn(Optional.of(usuario));

        controleLoginService.registrarSucesso("teste@example.com");

        assertEquals(0, usuario.getTentativasFalhas());
        assertNull(usuario.getBloqueadoAte());
        assertNotNull(usuario.getUltimoLogin());
        verify(usuarioRepository).save(usuario);
    }
@Test
void naoDeveContarNovaFalhaDuranteBloqueioAtivo() {
    usuario.setTentativasFalhas(5);
    usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(10));

    when(usuarioRepository.buscarPorEmailParaAtualizacao("teste@example.com"))
            .thenReturn(Optional.of(usuario));

    controleLoginService.registrarFalha("teste@example.com");

    assertEquals(5, usuario.getTentativasFalhas());
    verify(usuarioRepository, never()).save(usuario);
}

@Test
void deveReiniciarContagemQuandoBloqueioExpirar() {
    usuario.setTentativasFalhas(5);
    usuario.setBloqueadoAte(LocalDateTime.now().minusSeconds(1));

    when(usuarioRepository.buscarPorEmailParaAtualizacao("teste@example.com"))
            .thenReturn(Optional.of(usuario));

    controleLoginService.registrarFalha("teste@example.com");

    assertEquals(1, usuario.getTentativasFalhas());
    assertNull(usuario.getBloqueadoAte());
    verify(usuarioRepository).save(usuario);
}
}
