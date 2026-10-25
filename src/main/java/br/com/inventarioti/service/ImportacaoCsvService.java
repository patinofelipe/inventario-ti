
package br.com.inventarioti.service;

import br.com.inventarioti.model.Equipamento;
import br.com.inventarioti.repository.EquipamentoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class ImportacaoCsvService {

    private final EquipamentoRepository equipamentoRepository;

    public ImportacaoCsvService(
            EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    public String importar(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Selecione um arquivo CSV não vazio.");
        }

        if (arquivo.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "O arquivo não pode ultrapassar 10 MB.");
        }

        String conteudo = new String(
                arquivo.getBytes(), StandardCharsets.UTF_8);

        if (!conteudo.isEmpty() && conteudo.charAt(0) == '\uFEFF') {
            conteudo = conteudo.substring(1);
        }

        char separador = detectarSeparador(conteudo);
        List<List<String>> linhas = analisarCsv(conteudo, separador);

        if (linhas.isEmpty()) {
            throw new IllegalArgumentException(
                    "O arquivo CSV está vazio.");
        }

        validarCabecalho(linhas.get(0));

        int processadas = 0;
        int criadas = 0;
        int rejeitadas = 0;
        int avisos = 0;

        Set<String> numerosNoArquivo = new HashSet<>();
        Set<String> nrpsNoArquivo = new HashSet<>();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("RELATÓRIO DE IMPORTAÇÃO DE EQUIPAMENTOS\n");
        relatorio.append("========================================\n\n");

        for (int i = 1; i < linhas.size(); i++) {
            List<String> campos = linhas.get(i);

            if (campos.stream().allMatch(String::isBlank)) {
                continue;
            }

            processadas++;
            int numeroLinha = i + 1;

            if (campos.size() != 9) {
                rejeitadas++;
                relatorio.append("Linha ").append(numeroLinha)
                        .append(" - REJEITADA: esperadas 9 colunas, recebidas ")
                        .append(campos.size()).append(".\n");
                continue;
            }

            String numero = campo(campos, 0);
            String nrpOriginal = campo(campos, 1);
            String nrp = normalizarNrp(nrpOriginal);
            String localidade = campo(campos, 2);
            String mac = campo(campos, 3);
            String ip = campo(campos, 4);
            String servidor = campo(campos, 5);
            String numeroSwitchOriginal = campo(campos, 6);
            String numeroSwitch = normalizarNumeroSwitch(numeroSwitchOriginal);
            String portaSwitch = campo(campos, 7);
            String status = campo(campos, 8).toUpperCase(Locale.ROOT);

            List<String> erros = new ArrayList<>();
            List<String> avisosLinha = new ArrayList<>();

            if (numero.isBlank()) {
                erros.add("Numero obrigatório");
            }
            if (nrp.isBlank()) {
                erros.add("NRP obrigatório");
            }
            if (localidade.isBlank()) {
                erros.add("Localidade obrigatória");
            }

            validarTamanho(numero, 50, "Numero", erros);
            if (!numeroSwitch.isBlank() && !numeroSwitch.matches("\\d{6}")) {
    erros.add("Numero Switch inválido: informe exatamente seis dígitos, "
            + "com ou sem ponto (exemplo: 123456 ou 123.456)");
}
            validarTamanho(localidade, 150, "Localidade", erros);
            validarTamanho(mac, 17, "MAC Address", erros);
            validarTamanho(ip, 45, "IP", erros);
            validarTamanho(servidor, 150, "Servidor", erros);
            validarTamanho(numeroSwitch, 50, "Numero Switch", erros);
            validarTamanho(portaSwitch, 30, "Porta Switch", erros);

            if (!status.isBlank()
                    && !Set.of("ATIVO", "INATIVO", "MANUTENCAO")
                            .contains(status)) {
                erros.add("Status inválido: " + status);
            }

            if (status.isBlank()) {
                status = "ATIVO";
            }

            String numeroNormalizado = normalizarIdentificador(numero);
            String nrpNormalizado = normalizarIdentificador(nrp);

            if (!numero.isBlank()
                    && numerosNoArquivo.contains(numeroNormalizado)) {
                erros.add("Numero repetido no arquivo");
            }

            if (!nrp.isBlank()
                    && nrpsNoArquivo.contains(nrpNormalizado)) {
                erros.add("NRP repetido no arquivo");
            }

            if (!numero.isBlank()
                    && equipamentoRepository.findByNumero(numero).isPresent()) {
                erros.add("Numero já cadastrado no sistema");
            }

            if (!nrp.isBlank()
                    && equipamentoRepository
                            .findByNrpIgnoreCase(nrp).isPresent()) {
                erros.add("NRP já cadastrado no sistema");
            }

            if (erros.isEmpty()) {
                if (mac.isBlank()) {
                    avisosLinha.add("MAC Address ausente");
                }
                if (ip.isBlank()) {
                    avisosLinha.add("IP ausente");
                }

                Equipamento equipamento = new Equipamento();
                equipamento.setNumero(numero);
                equipamento.setNrp(nrp);
                equipamento.setLocalidade(localidade);
                equipamento.setMacAddress(mac.isBlank() ? null : mac);
                equipamento.setIp(ip.isBlank() ? null : ip);
                equipamento.setServidor(servidor.isBlank() ? null : servidor);
                equipamento.setNumeroSwitch(
                        numeroSwitch.isBlank() ? null : numeroSwitch);
                equipamento.setPortaSwitch(
                        portaSwitch.isBlank() ? null : portaSwitch);
                equipamento.setStatus(status);

                try {
                    equipamentoRepository.saveAndFlush(equipamento);

                    numerosNoArquivo.add(numeroNormalizado);
                    nrpsNoArquivo.add(nrpNormalizado);
                    criadas++;

                    relatorio.append("Linha ").append(numeroLinha)
                            .append(" - CRIADA: Numero=").append(numero)
                            .append(", NRP=").append(nrp).append(".\n");

                    for (String aviso : avisosLinha) {
                        avisos++;
                        relatorio.append("  AVISO: ")
                                .append(aviso).append(".\n");
                    }
                } catch (DataIntegrityViolationException e) {
                    rejeitadas++;
                    relatorio.append("Linha ").append(numeroLinha)
                            .append(" - REJEITADA: conflito com uma restrição ")
                            .append("do banco de dados.\n");
                }
            } else {
                rejeitadas++;
                relatorio.append("Linha ").append(numeroLinha)
                        .append(" - REJEITADA: ")
                        .append(String.join("; ", erros))
                        .append(".\n");
            }
        }

        relatorio.append("\nRESUMO\n");
        relatorio.append("------\n");
        relatorio.append("Linhas processadas: ").append(processadas).append("\n");
        relatorio.append("Equipamentos criados: ").append(criadas).append("\n");
        relatorio.append("Linhas rejeitadas: ").append(rejeitadas).append("\n");
        relatorio.append("Avisos: ").append(avisos).append("\n");

        return relatorio.toString();
    }

    private String campo(List<String> campos, int indice) {
        return campos.get(indice).trim();
    }

    private void validarTamanho(
            String valor, int maximo, String nome, List<String> erros) {
        if (valor.length() > maximo) {
            erros.add(nome + " ultrapassa " + maximo + " caracteres");
        }
    }

    private String normalizarIdentificador(String valor) {
        return valor.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarNrp(String nrp) {
    String valor = nrp == null ? "" : nrp.trim();

    if (valor.matches("\\d{3}\\.\\d{3}")) {
        return valor.replace(".", "");
    }

    return valor;
    }

    private String normalizarNumeroSwitch(String numeroSwitch) {
    String valor = numeroSwitch == null ? "" : numeroSwitch.trim();

    if (valor.matches("\\d{3}\\.\\d{3}")) {
        return valor.replace(".", "");
    }

    return valor;
}

    private void validarCabecalho(List<String> cabecalho) {
        List<String> esperado = List.of(
                "numero", "nrp", "localidade", "macaddress",
                "ip", "servidor", "numeroswitch", "portaswitch", "status"
        );

        List<String> recebido = cabecalho.stream()
                .map(this::normalizarCabecalho)
                .toList();

        if (!recebido.equals(esperado)) {
            throw new IllegalArgumentException(
                    "Cabeçalho inválido. Use as colunas exportadas pelo sistema: "
                    + "Numero;NRP;Localidade;MAC Address;IP;Servidor;"
                    + "Numero Switch;Porta Switch;Status");
        }
    }

    private String normalizarCabecalho(String valor) {
        String semAcentos = Normalizer.normalize(
                valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return semAcentos.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
    }

    private char detectarSeparador(String conteudo) {
        boolean aspas = false;
        int pontoVirgula = 0;
        int virgula = 0;

        for (int i = 0; i < conteudo.length(); i++) {
            char c = conteudo.charAt(i);

            if (c == '"') {
                if (aspas && i + 1 < conteudo.length()
                        && conteudo.charAt(i + 1) == '"') {
                    i++;
                } else {
                    aspas = !aspas;
                }
            } else if (!aspas && (c == '\n' || c == '\r')) {
                break;
            } else if (!aspas && c == ';') {
                pontoVirgula++;
            } else if (!aspas && c == ',') {
                virgula++;
            }
        }

        return pontoVirgula >= virgula ? ';' : ',';
    }

    private List<List<String>> analisarCsv(
            String conteudo, char separador) {

        List<List<String>> linhas = new ArrayList<>();
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean aspas = false;

        for (int i = 0; i < conteudo.length(); i++) {
            char c = conteudo.charAt(i);

            if (c == '"') {
                if (aspas && i + 1 < conteudo.length()
                        && conteudo.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    aspas = !aspas;
                }
            } else if (!aspas && c == separador) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else if (!aspas && (c == '\n' || c == '\r')) {
                if (c == '\r' && i + 1 < conteudo.length()
                        && conteudo.charAt(i + 1) == '\n') {
                    i++;
                }

                campos.add(campo.toString());
                linhas.add(new ArrayList<>(campos));
                campos.clear();
                campo.setLength(0);
            } else {
                campo.append(c);
            }
        }

        if (aspas) {
            throw new IllegalArgumentException(
                    "CSV inválido: existe um campo com aspas não fechadas.");
        }

        if (campo.length() > 0 || !campos.isEmpty()) {
            campos.add(campo.toString());
            linhas.add(new ArrayList<>(campos));
        }

        return linhas;
    }
}
