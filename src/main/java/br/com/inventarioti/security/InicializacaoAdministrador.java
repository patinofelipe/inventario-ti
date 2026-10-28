package br.com.inventarioti.security;

import br.com.inventarioti.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class InicializacaoAdministrador implements ApplicationRunner {

    private final CadastroInicialService cadastroInicialService;
    private final UsuarioRepository usuarioRepository;
    private final Environment environment;

    public InicializacaoAdministrador(
            CadastroInicialService cadastroInicialService,
            UsuarioRepository usuarioRepository,
            Environment environment) {
        this.cadastroInicialService = cadastroInicialService;
        this.usuarioRepository = usuarioRepository;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        String nome = environment.getProperty("ADMIN_INICIAL_NOME");
        String email = environment.getProperty("ADMIN_INICIAL_EMAIL");
        String senha = environment.getProperty("ADMIN_INICIAL_SENHA");

        if (nome == null || email == null || senha == null) {
            System.out.println(
                "Nenhum usuário cadastrado. Configure as variáveis "
                + "ADMIN_INICIAL_NOME, ADMIN_INICIAL_EMAIL e "
                + "ADMIN_INICIAL_SENHA para criar o administrador inicial."
            );
            return;
        }

        cadastroInicialService.criarAdministrador(nome, email, senha);

        System.out.println(
            "Administrador inicial criado. Remova as variáveis "
            + "de inicialização antes de disponibilizar a aplicação."
        );
    }
}

