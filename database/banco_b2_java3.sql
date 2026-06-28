DROP DATABASE IF EXISTS gdi_db;
CREATE DATABASE gdi_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gdi_db;

CREATE TABLE setores (
    id_setor INT NOT NULL AUTO_INCREMENT,
    nome_setor VARCHAR(120) NOT NULL,
    descricao_setor TEXT,
    id_gerente_setor INT NULL,
    PRIMARY KEY (id_setor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE categorias (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nome_categoria VARCHAR(120) NOT NULL,
    descricao_categoria TEXT,
    PRIMARY KEY (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE status (
    id_status INT NOT NULL AUTO_INCREMENT,
    nome_status VARCHAR(80) NOT NULL,
    ordem_status INT,
    PRIMARY KEY (id_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nome_usuario VARCHAR(150) NOT NULL,
    email_usuario VARCHAR(180) NOT NULL,
    senha_hash_usuario VARCHAR(255) NOT NULL,
    perfil_usuario ENUM('admin','gerente','usuario') NOT NULL DEFAULT 'usuario',
    ativo_usuario TINYINT(1) NOT NULL DEFAULT 1,
    id_setor_usuario INT,
    PRIMARY KEY (id_usuario),
    UNIQUE KEY uk_usuarios_email (email_usuario),
    CONSTRAINT fk_usuarios_setores
        FOREIGN KEY (id_setor_usuario) REFERENCES setores(id_setor)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE setores
    ADD CONSTRAINT fk_setor_gerente
        FOREIGN KEY (id_gerente_setor) REFERENCES usuarios(id_usuario)
        ON DELETE SET NULL ON UPDATE CASCADE;

CREATE TABLE demandas (
    id_demanda INT NOT NULL AUTO_INCREMENT,
    titulo_demanda VARCHAR(180) NOT NULL,
    descricao_demanda TEXT,
    prioridade_demanda INT NOT NULL DEFAULT 2,
    sla_data_limite_demanda DATETIME,
    aberta_em_demanda DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    concluida_em_demanda DATETIME,
    id_solicitante_demanda INT NOT NULL,
    id_setor_destino_demanda INT,
    id_categoria_demanda INT,
    id_status_demanda INT NOT NULL,
    PRIMARY KEY (id_demanda),
    CONSTRAINT fk_demandas_solicitante
        FOREIGN KEY (id_solicitante_demanda) REFERENCES usuarios(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_demandas_setor_destino
        FOREIGN KEY (id_setor_destino_demanda) REFERENCES setores(id_setor)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_demandas_categoria
        FOREIGN KEY (id_categoria_demanda) REFERENCES categorias(id_categoria)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_demandas_status
        FOREIGN KEY (id_status_demanda) REFERENCES status(id_status)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comentarios (
    id_comentario INT NOT NULL AUTO_INCREMENT,
    id_demanda_comentario INT NOT NULL,
    id_usuario_comentario INT NOT NULL,
    mensagem_comentario TEXT NOT NULL,
    criado_em_comentario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_comentario),
    CONSTRAINT fk_comentarios_demandas
        FOREIGN KEY (id_demanda_comentario) REFERENCES demandas(id_demanda)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comentarios_usuarios
        FOREIGN KEY (id_usuario_comentario) REFERENCES usuarios(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE anexos (
    id_anexo INT NOT NULL AUTO_INCREMENT,
    id_demanda_anexo INT NOT NULL,
    id_usuario_upload_anexo INT NOT NULL,
    nome_arquivo_anexo VARCHAR(255) NOT NULL,
    tipo_mime_anexo VARCHAR(120),
    tamanho_bytes_anexo BIGINT,
    url_armazenamento_anexo VARCHAR(255),
    criado_em_anexo DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_anexo),
    CONSTRAINT fk_anexos_demandas
        FOREIGN KEY (id_demanda_anexo) REFERENCES demandas(id_demanda)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_anexos_usuario_upload
        FOREIGN KEY (id_usuario_upload_anexo) REFERENCES usuarios(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE demandas_usuarios (
    id_demanda_du INT NOT NULL,
    id_usuario_du INT NOT NULL,
    papel_du VARCHAR(30) NOT NULL DEFAULT 'responsavel',
    vinculado_em_du DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_demanda_du, id_usuario_du),
    CONSTRAINT fk_du_demandas
        FOREIGN KEY (id_demanda_du) REFERENCES demandas(id_demanda)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_du_usuarios
        FOREIGN KEY (id_usuario_du) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE historico_status (
    id_historico INT NOT NULL AUTO_INCREMENT,
    id_demanda_hist INT NOT NULL,
    id_usuario_hist INT NOT NULL,
    status_anterior INT NULL,
    status_novo INT NOT NULL,
    observacao_hist VARCHAR(500) NULL,
    alterado_em_hist TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_historico),
    CONSTRAINT fk_hist_demanda
        FOREIGN KEY (id_demanda_hist) REFERENCES demandas(id_demanda)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_hist_usuario
        FOREIGN KEY (id_usuario_hist) REFERENCES usuarios(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_hist_status_ant
        FOREIGN KEY (status_anterior) REFERENCES status(id_status)
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_hist_status_novo
        FOREIGN KEY (status_novo) REFERENCES status(id_status)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE notificacoes_lidas (
    id_notificacao_lida INT NOT NULL AUTO_INCREMENT,
    id_usuario_notificacao INT NOT NULL,
    chave_notificacao VARCHAR(100) NOT NULL,
    lida_em_notificacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_notificacao_lida),
    UNIQUE KEY uk_notificacao_usuario_chave (id_usuario_notificacao, chave_notificacao),
    CONSTRAINT fk_notificacao_usuario
        FOREIGN KEY (id_usuario_notificacao) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE login_tokens (
    id_token INT NOT NULL AUTO_INCREMENT,
    id_usuario_token INT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    criado_em_token TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_uso_token TIMESTAMP NULL,
    expira_em_token TIMESTAMP NOT NULL,
    PRIMARY KEY (id_token),
    UNIQUE KEY uk_login_tokens_hash (token_hash),
    KEY idx_login_tokens_usuario (id_usuario_token),
    CONSTRAINT fk_login_tokens_usuario
        FOREIGN KEY (id_usuario_token) REFERENCES usuarios(id_usuario)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;