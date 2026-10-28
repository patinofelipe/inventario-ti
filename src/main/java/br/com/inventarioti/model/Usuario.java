package br.com.inventarioti.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    @Column(name = "email_verificado", nullable = false)
private boolean emailVerificado;

@Column(name = "tentativas_falhas", nullable = false)
private int tentativasFalhas;

@Column(name = "bloqueado_ate")
private LocalDateTime bloqueadoAte;

@Column(name = "ultimo_login")
private LocalDateTime ultimoLogin;

@Column(name = "criado_em", insertable = false, updatable = false)
private java.time.LocalDateTime criadoEm;

@Column(name = "atualizado_em", insertable = false, updatable = false)
private java.time.LocalDateTime atualizadoEm;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_perfis",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<Perfil> perfis = new HashSet<>();

    public Usuario() {
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

public void setNome(String nome) {
    this.nome = nome;
}

public void setEmail(String email) {
    this.email = email;
}

public void setSenhaHash(String senhaHash) {
    this.senhaHash = senhaHash;
}

public void setAtivo(boolean ativo) {
    this.ativo = ativo;
}

public boolean isEmailVerificado() {
    return emailVerificado;
}

public void setEmailVerificado(boolean emailVerificado) {
    this.emailVerificado = emailVerificado;
}

public int getTentativasFalhas() {
    return tentativasFalhas;
}

public void setTentativasFalhas(int tentativasFalhas) {
    this.tentativasFalhas = tentativasFalhas;
}

public LocalDateTime getBloqueadoAte() {
    return bloqueadoAte;
}

public void setBloqueadoAte(LocalDateTime bloqueadoAte) {
    this.bloqueadoAte = bloqueadoAte;
}

public LocalDateTime getUltimoLogin() {
    return ultimoLogin;
}

public void setUltimoLogin(LocalDateTime ultimoLogin) {
    this.ultimoLogin = ultimoLogin;
}

}
