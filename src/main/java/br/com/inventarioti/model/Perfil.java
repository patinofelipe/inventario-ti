package br.com.inventarioti.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfis")
public class Perfil {

    @Id
    private Long id;

    @Column(name = "nome", nullable = false, length = 30)
    private String nome;

    @Column(name = "descricao", length = 255)
    private String descricao;

    public Perfil() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
