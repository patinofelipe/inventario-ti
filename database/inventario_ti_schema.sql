USE inventario_ti;

CREATE TABLE perfis (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(30) NOT NULL UNIQUE,
    descricao VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuarios (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    tentativas_falhas INT NOT NULL DEFAULT 0,
    bloqueado_ate DATETIME NULL,
    ultimo_login DATETIME NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuario_perfis (
    usuario_id BIGINT UNSIGNED NOT NULL,
    perfil_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    CONSTRAINT fk_usuario_perfis_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_usuario_perfis_perfil
        FOREIGN KEY (perfil_id) REFERENCES perfis(id)
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE desafios_autenticacao (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    finalidade VARCHAR(40) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expira_em DATETIME NOT NULL,
    tentativas INT NOT NULL DEFAULT 0,
    usado_em DATETIME NULL,
    INDEX idx_desafios_usuario_expira (usuario_id, expira_em),
    CONSTRAINT fk_desafios_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE equipamentos (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    nrp VARCHAR(80),
    localidade VARCHAR(150) NOT NULL,
    mac_address VARCHAR(17),
    ip VARCHAR(45),
    servidor VARCHAR(150),
    numero_switch VARCHAR(50),
    porta_switch VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'ATIVO',
    criado_por BIGINT UNSIGNED NULL,
    atualizado_por BIGINT UNSIGNED NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_equipamentos_nrp (nrp),
    INDEX idx_equipamentos_localidade (localidade),
    INDEX idx_equipamentos_ip (ip),
    INDEX idx_equipamentos_status (status),
    CONSTRAINT fk_equipamentos_criado_por
        FOREIGN KEY (criado_por) REFERENCES usuarios(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_equipamentos_atualizado_por
        FOREIGN KEY (atualizado_por) REFERENCES usuarios(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE auditoria (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNSIGNED NULL,
    acao VARCHAR(80) NOT NULL,
    entidade VARCHAR(80) NOT NULL,
    entidade_id VARCHAR(80),
    detalhes JSON NULL,
    ip_origem VARCHAR(45),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_auditoria_usuario (usuario_id),
    INDEX idx_auditoria_data (criado_em),
    INDEX idx_auditoria_entidade (entidade, entidade_id),
    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO perfis (nome, descricao) VALUES
('ADM', 'Administração de usuários e equipamentos'),
('USUARIO', 'Cadastro, edição, consulta e relatórios'),
('CLIENTE', 'Consulta de equipamentos e relatórios');
