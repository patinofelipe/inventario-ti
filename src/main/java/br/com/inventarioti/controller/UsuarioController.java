
package br.com.inventarioti.controller;

import br.com.inventarioti.repository.UsuarioRepository;
import br.com.inventarioti.service.CadastroUsuarioService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final CadastroUsuarioService cadastroUsuarioService;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            CadastroUsuarioService cadastroUsuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.cadastroUsuarioService = cadastroUsuarioService;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios";
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario() {
        return "usuario-formulario";
    }

    @PostMapping("/usuarios/novo")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String perfil,
            RedirectAttributes redirectAttributes) {

        try {
            cadastroUsuarioService.cadastrar(
                    nome, email, senha, perfil
            );

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Usuário cadastrado com sucesso."
            );

            return "redirect:/usuarios";

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage()
            );

            return "redirect:/usuarios/novo";
        }
    }
}
