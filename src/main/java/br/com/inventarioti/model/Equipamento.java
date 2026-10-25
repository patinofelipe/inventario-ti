package br.com.inventarioti.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipamentos")
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O número do equipamento é obrigatório.")
    @Column(name = "numero", nullable = false, unique = true, length = 50)
    private String numero;

    @NotBlank(message = "O NRP é obrigatório.")
    @Pattern(
        regexp = "\\d{6}",
        message = "O NRP deve conter exatamente seis dígitos. Exemplo: 123456."
    )
    @Column(name = "nrp", nullable = false, unique = true, length = 80)
    private String nrp;

    @NotBlank(message = "A localidade é obrigatória.")
    @Column(name = "localidade", nullable = false, length = 150)
    private String localidade;

    @Column(name = "mac_address", length = 17)
    private String macAddress;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "servidor", length = 150)
    private String servidor;

    @Pattern(
        regexp = "^$|\\d{6}",
        message = "O número do switch deve conter seis dígitos."
    )
    @Column(name = "numero_switch", length = 50)
    private String numeroSwitch;

    @Column(name = "porta_switch", length = 30)
    private String portaSwitch;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "ATIVO";

    @Column(name = "criado_por")
    private Long criadoPorId;

    @Column(name = "atualizado_por")
    private Long atualizadoPorId;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", insertable = false, updatable = false)
    private LocalDateTime atualizadoEm;

    public Equipamento() {
    }

    // ID
    public Long getId() {
        return id;
    }

    // Número do equipamento
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    // NRP: armazenamento sem ponto
    public String getNrp() {
        return nrp;
    }

    // NRP: apresentação com ponto
    public String getNrpFormatado() {
        if (nrp != null && nrp.matches("\\d{6}")) {
            return nrp.substring(0, 3) + "." + nrp.substring(3);
        }

        return nrp;
    }

    // Aceita 123456 ou 123.456
    public void setNrp(String nrp) {
        if (nrp == null) {
            this.nrp = null;
            return;
        }

        String valor = nrp.trim();

        if (valor.matches("\\d{3}\\.\\d{3}")) {
            valor = valor.replace(".", "");
        }

        this.nrp = valor;
    }

    // Localidade
    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    // Endereço MAC
    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    // Endereço IP
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    // Servidor
    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    // Número do switch: armazenamento sem ponto
    public String getNumeroSwitch() {
        return numeroSwitch;
    }

    // Número do switch: apresentação com ponto
    public String getNumeroSwitchFormatado() {
        if (numeroSwitch != null && numeroSwitch.matches("\\d{6}")) {
            return numeroSwitch.substring(0, 3)
                    + "." + numeroSwitch.substring(3);
        }

        return numeroSwitch;
    }

    // Aceita 654321 ou 654.321; campo opcional
    public void setNumeroSwitch(String numeroSwitch) {
        if (numeroSwitch == null) {
            this.numeroSwitch = null;
            return;
        }

        String valor = numeroSwitch.trim();

        if (valor.matches("\\d{3}\\.\\d{3}")) {
            valor = valor.replace(".", "");
        }

        this.numeroSwitch = valor;
    }

    // Porta do switch
    public String getPortaSwitch() {
        return portaSwitch;
    }

    public void setPortaSwitch(String portaSwitch) {
        this.portaSwitch = portaSwitch;
    }

    // Status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Auditoria
    public Long getCriadoPorId() {
        return criadoPorId;
    }

    public void setCriadoPorId(Long criadoPorId) {
        this.criadoPorId = criadoPorId;
    }

    public Long getAtualizadoPorId() {
        return atualizadoPorId;
    }

    public void setAtualizadoPorId(Long atualizadoPorId) {
        this.atualizadoPorId = atualizadoPorId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}
