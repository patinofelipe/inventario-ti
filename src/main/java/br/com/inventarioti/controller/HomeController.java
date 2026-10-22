package br.com.inventarioti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nomeSistema", "Inventário TI");
        model.addAttribute("mensagem", "Projeto iniciado. Próximo passo: configurar o banco de dados.");
        return "index";
    }
}
