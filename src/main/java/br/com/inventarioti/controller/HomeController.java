
package br.com.inventarioti.controller;

import br.com.inventarioti.repository.EquipamentoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final EquipamentoRepository equipamentoRepository;

    public HomeController(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("nomeSistema", "Inventário TI");

        long total = equipamentoRepository.count();

        long ativos =
                equipamentoRepository.countByStatusIgnoreCase("ATIVO");

        long inativos =
                equipamentoRepository.countByStatusIgnoreCase("INATIVO");

        double percentualAtivos = total > 0
                ? ativos * 100.0 / total
                : 0;

        model.addAttribute("totalEquipamentos", total);
        model.addAttribute("equipamentosAtivos", ativos);
        model.addAttribute("equipamentosInativos", inativos);
        model.addAttribute("percentualAtivos", percentualAtivos);
                long equipamentosSemIp =
                equipamentoRepository.contarSemIp();

        long equipamentosSemMac =
                equipamentoRepository.contarSemMacAddress();

        model.addAttribute("equipamentosSemIp", equipamentosSemIp);
        model.addAttribute("equipamentosSemMac", equipamentosSemMac);

        List<Object[]> dadosLocalidade =
                equipamentoRepository.contarPorLocalidade();

        model.addAttribute("dadosLocalidade", dadosLocalidade);

        return "index";
    }
}

