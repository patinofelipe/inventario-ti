package br.com.inventarioti.security;

import br.com.inventarioti.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@Import(ConfiguracaoSeguranca.class)
class ConfiguracaoSegurancaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ControleLoginService controleLoginService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deveExibirPaginaDeLoginSemAutenticacao() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void deveRejeitarLoginSemTokenCsrf() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "teste@example.com")
                        .param("password", "senha-incorreta"))
                .andExpect(status().isForbidden());
    }

    @Test
    void formularioDeLoginDeveConterCamposEsperados() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("name=\"username\"")
                ))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString("name=\"password\"")
                ));
    }

    @Test
void deveRedirecionarUsuarioAnonimoParaLoginAoAcessarPainel()
        throws Exception {
    mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
}

    @Test
    void deveRejeitarLogoutSemTokenCsrf() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveAceitarLogoutComTokenCsrf() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }

    @Test
    void perfilClienteNaoPodeExcluirEquipamento() throws Exception {
        mockMvc.perform(post("/equipamentos/1/excluir")
                        .with(user("cliente").roles("CLIENTE"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void perfilUsuarioNaoPodeExcluirEquipamento() throws Exception {
        mockMvc.perform(post("/equipamentos/1/excluir")
                        .with(user("usuario").roles("USUARIO"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

@Test
void deveAutenticarUsuarioComSenhaCorreta() throws Exception {
    String senha = "SenhaTeste123!";

    var usuario = User.withUsername("teste@example.com")
            .password(passwordEncoder.encode(senha))
            .roles("USUARIO")
            .build();

    org.mockito.Mockito.when(
            userDetailsService.loadUserByUsername("teste@example.com")
    ).thenReturn(usuario);

    mockMvc.perform(post("/login")
                    .with(csrf())
                    .param("username", "teste@example.com")
                    .param("password", senha))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));

    org.mockito.Mockito.verify(controleLoginService)
            .registrarSucesso("teste@example.com");
}

@Test
void deveRegistrarFalhaQuandoSenhaEstiverIncorreta() throws Exception {
    String senhaCorreta = "SenhaTeste123!";

    var usuario = User.withUsername("teste@example.com")
            .password(passwordEncoder.encode(senhaCorreta))
            .roles("USUARIO")
            .build();

    org.mockito.Mockito.when(
            userDetailsService.loadUserByUsername("teste@example.com")
    ).thenReturn(usuario);

    mockMvc.perform(post("/login")
                    .with(csrf())
                    .param("username", "teste@example.com")
                    .param("password", "SenhaErrada123!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?error"));

    org.mockito.Mockito.verify(controleLoginService)
            .registrarFalha("teste@example.com");
}

}
