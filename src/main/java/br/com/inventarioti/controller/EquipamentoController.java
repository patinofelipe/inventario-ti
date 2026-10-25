
package br.com.inventarioti.controller;

import br.com.inventarioti.model.Equipamento;
import br.com.inventarioti.repository.EquipamentoRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import br.com.inventarioti.service.ImportacaoCsvService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private final EquipamentoRepository equipamentoRepository;
    private final ImportacaoCsvService importacaoCsvService;

    public EquipamentoController(
        EquipamentoRepository equipamentoRepository,
        ImportacaoCsvService importacaoCsvService) {
    this.equipamentoRepository = equipamentoRepository;
    this.importacaoCsvService = importacaoCsvService;
}

    @GetMapping
    public String listar(
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String nrp,
            @RequestParam(required = false) String localidade,
            @RequestParam(required = false) String ip,
            Model model) {

        Stream<Equipamento> consulta =
                equipamentoRepository.findAll().stream();

        if (numero != null && !numero.isBlank()) {
            String filtro = numero.trim().toLowerCase();
            consulta = consulta.filter(e ->
                    e.getNumero() != null &&
                    e.getNumero().toLowerCase().contains(filtro));
        }

        if (nrp != null && !nrp.isBlank()) {
            String filtro = nrp.trim().toLowerCase();
            consulta = consulta.filter(e ->
                    e.getNrp() != null &&
                    e.getNrp().toLowerCase().contains(filtro));
        }

        if (localidade != null && !localidade.isBlank()) {
            String filtro = localidade.trim().toLowerCase();
            consulta = consulta.filter(e ->
                    e.getLocalidade() != null &&
                    e.getLocalidade().toLowerCase().contains(filtro));
        }

        if (ip != null && !ip.isBlank()) {
            String filtro = ip.trim();
            consulta = consulta.filter(e ->
                    e.getIp() != null &&
                    e.getIp().startsWith(filtro));
        }

        List<Equipamento> equipamentos = consulta.toList();

        model.addAttribute("equipamentos", equipamentos);
        model.addAttribute("numero", numero);
        model.addAttribute("nrp", nrp);
        model.addAttribute("localidade", localidade);
        model.addAttribute("ip", ip);

        return "equipamentos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("equipamento", new Equipamento());
        model.addAttribute("modoEdicao", false);
        return "equipamento-form";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes atributos) {

        return equipamentoRepository.findById(id)
                .map(equipamento -> {
                    model.addAttribute("equipamento", equipamento);
                    model.addAttribute("modoEdicao", true);
                    model.addAttribute("idEdicao", id);
                    return "equipamento-form";
                })
                .orElseGet(() -> {
                    atributos.addFlashAttribute(
                            "erroGeral",
                            "Equipamento não encontrado."
                    );
                    return "redirect:/equipamentos";
                });
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid Equipamento equipamento,
            BindingResult resultado,
            Model model,
            RedirectAttributes atributos) {

        if (resultado.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "equipamento-form";
        }

        if (equipamentoRepository
                .findByNumero(equipamento.getNumero())
                .isPresent()) {
            model.addAttribute(
                    "erro",
                    "Já existe um equipamento com esse número."
            );
            model.addAttribute("modoEdicao", false);
            return "equipamento-form";
        }
        if (equipamentoRepository
        .findByNrpIgnoreCase(equipamento.getNrp().trim())
        .isPresent()) {

    model.addAttribute(
            "erro",
            "Já existe um equipamento cadastrado com esse NRP."
    );
    model.addAttribute("modoEdicao", false);
    return "equipamento-form";
}
        try {
            equipamentoRepository.save(equipamento);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute(
                    "erro",
                    "Não foi possível salvar. Verifique se o número já existe."
            );
            model.addAttribute("modoEdicao", false);
            return "equipamento-form";
        }

        atributos.addFlashAttribute(
                "sucesso",
                "Equipamento cadastrado com sucesso!"
        );

        return "redirect:/equipamentos";
    }

    @PostMapping("/{id}/atualizar")
    public String atualizar(
            @PathVariable Long id,
            @Valid Equipamento dados,
            BindingResult resultado,
            Model model,
            RedirectAttributes atributos) {

        Equipamento existente = equipamentoRepository
                .findById(id)
                .orElse(null);

        if (existente == null) {
            atributos.addFlashAttribute(
                    "erroGeral",
                    "Equipamento não encontrado."
            );
            return "redirect:/equipamentos";
        }

        if (resultado.hasErrors()) {
            model.addAttribute("equipamento", dados);
            model.addAttribute("modoEdicao", true);
            model.addAttribute("idEdicao", id);
            return "equipamento-form";
        }

        var mesmoNumero = equipamentoRepository
                .findByNumero(dados.getNumero());
        var mesmoNrp = equipamentoRepository
        .findByNrpIgnoreCase(dados.getNrp().trim());

if (mesmoNrp.isPresent()
        && !mesmoNrp.get().getId().equals(id)) {

    model.addAttribute("equipamento", dados);
    model.addAttribute(
            "erro",
            "Esse NRP já pertence a outro equipamento."
    );
    model.addAttribute("modoEdicao", true);
    model.addAttribute("idEdicao", id);
    return "equipamento-form";
}
        if (mesmoNumero.isPresent()
                && !mesmoNumero.get().getId().equals(id)) {
            model.addAttribute(
                    "equipamento", dados);
            model.addAttribute(
                    "erro",
                    "Esse número já pertence a outro equipamento."
            );
            model.addAttribute("modoEdicao", true);
            model.addAttribute("idEdicao", id);
            return "equipamento-form";
        }

        existente.setNumero(dados.getNumero());
        existente.setNrp(dados.getNrp());
        existente.setLocalidade(dados.getLocalidade());
        existente.setMacAddress(dados.getMacAddress());
        existente.setIp(dados.getIp());
        existente.setServidor(dados.getServidor());
        existente.setNumeroSwitch(dados.getNumeroSwitch());
        existente.setPortaSwitch(dados.getPortaSwitch());
        existente.setStatus(dados.getStatus());

        try {
            equipamentoRepository.save(existente);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("equipamento", dados);
            model.addAttribute(
                    "erro",
                    "Não foi possível atualizar. Verifique os dados."
            );
            model.addAttribute("modoEdicao", true);
            model.addAttribute("idEdicao", id);
            return "equipamento-form";
        }

        atributos.addFlashAttribute(
                "sucesso",
                "Equipamento atualizado com sucesso!"
        );

        return "redirect:/equipamentos";
    }
    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes atributos) {

        var equipamento = equipamentoRepository.findById(id);

        if (equipamento.isEmpty()) {
            atributos.addFlashAttribute(
                    "erroGeral",
                    "Equipamento não encontrado."
            );
            return "redirect:/equipamentos";
        }

        try {
            equipamentoRepository.delete(equipamento.get());

            atributos.addFlashAttribute(
                    "sucesso",
                    "Equipamento excluído com sucesso!"
            );

        } catch (DataIntegrityViolationException e) {
            atributos.addFlashAttribute(
                    "erroGeral",
                    "Não foi possível excluir este equipamento porque existem dados relacionados."
            );
        }

        return "redirect:/equipamentos";
    }


    @GetMapping("/exportar")
    public void exportar(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"inventario_equipamentos.csv\""
        );

        response.setHeader("Cache-Control", "no-store");

        response.getOutputStream().write(new byte[] {
                (byte) 0xEF, (byte) 0xBB, (byte) 0xBF
        });

        try (PrintWriter writer = new PrintWriter(
                response.getOutputStream(), false, StandardCharsets.UTF_8)) {

            writer.println(
                    "Numero;NRP;Localidade;MAC Address;IP;Servidor;"
                    + "Numero Switch;Porta Switch;Status"
            );

            for (Equipamento e : equipamentoRepository.findAll()) {
                writer.println(
                        csv(e.getNumero()) + ";" +
                        csv(e.getNrp()) + ";" +
                        csv(e.getLocalidade()) + ";" +
                        csv(e.getMacAddress()) + ";" +
                        csv(e.getIp()) + ";" +
                        csv(e.getServidor()) + ";" +
                        csv(e.getNumeroSwitch()) + ";" +
                        csv(e.getPortaSwitch()) + ";" +
                        csv(e.getStatus())
                );
            }

            writer.flush();
        }
    }
    @PostMapping(
        value = "/importar",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public void importar(
        @RequestParam("arquivo") MultipartFile arquivo,
        HttpServletResponse response) throws IOException {

    String relatorio;

    try {
        relatorio = importacaoCsvService.importar(arquivo);
    } catch (IllegalArgumentException e) {
        relatorio = "IMPORTAÇÃO NÃO EXECUTADA\n\nMotivo: "
                + e.getMessage() + "\n";
    }

    response.setContentType("text/plain; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"relatorio_importacao.txt\""
    );

    response.getOutputStream().write(
            relatorio.getBytes(StandardCharsets.UTF_8)
    );
}
    private String csv(String valor) {
        if (valor == null) {
            return "";
        }

        String protegido = valor.replace("\"", "\"\"");
        return "\"" + protegido + "\"";
    }


}
