
package br.com.inventarioti.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

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

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getNrp() { return nrp; }
    public void setNrp(String nrp) { this.nrp = nrp; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getServidor() { return servidor; }
    public void setServidor(String servidor) { this.servidor = servidor; }

    public String getNumeroSwitch() { return numeroSwitch; }
    public void setNumeroSwitch(String numeroSwitch) { this.numeroSwitch = numeroSwitch; }

    public String getPortaSwitch() { return portaSwitch; }
    public void setPortaSwitch(String portaSwitch) { this.portaSwitch = portaSwitch; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCriadoPorId() { return criadoPorId; }
    public void setCriadoPorId(Long criadoPorId) { this.criadoPorId = criadoPorId; }

    public Long getAtualizadoPorId() { return atualizadoPorId; }
    public void setAtualizadoPorId(Long atualizadoPorId) { this.atualizadoPorId = atualizadoPorId; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
