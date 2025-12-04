-- Tabela para armazenar os tipos de acesso (Gratuito, Pago)
CREATE TABLE tb_tipo_acesso (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255)
);

-- Tabela para armazenar os tipos de atividade (Caminhada, Corrida, etc.)
CREATE TABLE tb_tipo_atividade (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255)
);

-- Tabela para armazenar os tipos de local (Parque, Academia, etc.)
CREATE TABLE tb_tipo_local (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255)
);

-- Tabela de Endereços com campo de geolocalização
CREATE TABLE tb_endereco (
    id_endereco BIGSERIAL PRIMARY KEY,
    data_criacao TIMESTAMP,
    rua VARCHAR(255) NOT NULL,
    cidade VARCHAR(255) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    numero INTEGER NOT NULL,
    cep VARCHAR(8) NOT NULL,
    localizacao GEOMETRY(Point, 4326)
);

-- Tabela de Definições de Conquistas
CREATE TABLE tb_conquista (
    id BIGINT PRIMARY KEY,
    titulo VARCHAR(255),
    descricao VARCHAR(255),
    icone VARCHAR(255)
);

-- Tabela de Usuários
CREATE TABLE tb_usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome_usuario VARCHAR(255) NOT NULL,
    email_usuario VARCHAR(255) UNIQUE,
    sexo_usuario VARCHAR(255),
    idade_usuario INTEGER,
    senha_usuario VARCHAR(255) NOT NULL,
    foto_url VARCHAR(255),
    pontos INTEGER DEFAULT 0,
    tipo_usuario VARCHAR(255) NOT NULL,
    id_endereco BIGINT,
    CONSTRAINT fk_usuario_endereco FOREIGN KEY (id_endereco) REFERENCES tb_endereco(id_endereco)
);

-- Tabela de Junção para Usuários e Conquistas (ManyToMany)
CREATE TABLE tb_usuario_conquista (
    id_usuario BIGINT NOT NULL,
    id_conquista BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_conquista),
    CONSTRAINT fk_usuario_conquista_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT fk_usuario_conquista_conquista FOREIGN KEY (id_conquista) REFERENCES tb_conquista(id)
);

-- Tabela de Locais
CREATE TABLE tb_local (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    aprovado BOOLEAN NOT NULL DEFAULT FALSE,
    horarios_funcionamento VARCHAR(255),
    informacoes_adicionais VARCHAR(255),
    endereco_id BIGINT,
    tipo_atividade_id BIGINT,
    tipo_acesso_id BIGINT,
    tipo_local_id BIGINT,
    CONSTRAINT fk_local_endereco FOREIGN KEY (endereco_id) REFERENCES tb_endereco(id_endereco),
    CONSTRAINT fk_local_tipo_atividade FOREIGN KEY (tipo_atividade_id) REFERENCES tb_tipo_atividade(id),
    CONSTRAINT fk_local_tipo_acesso FOREIGN KEY (tipo_acesso_id) REFERENCES tb_tipo_acesso(id),
    CONSTRAINT fk_local_tipo_local FOREIGN KEY (tipo_local_id) REFERENCES tb_tipo_local(id)
);

-- Tabela de Avaliações
CREATE TABLE IF NOT EXISTS tb_avaliacao (
    id BIGSERIAL PRIMARY KEY,
    comentario TEXT,
    nota INTEGER,
    id_usuario BIGINT,
    id_local BIGINT,
    data_avaliacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT fk_avaliacao_local FOREIGN KEY (id_local) REFERENCES tb_local(id)
);

-- Índices para melhorar consultas de avaliações
CREATE INDEX IF NOT EXISTS idx_avaliacao_usuario ON tb_avaliacao(id_usuario);
CREATE INDEX IF NOT EXISTS idx_avaliacao_local ON tb_avaliacao(id_local);

-- Tabela de Check-ins
CREATE TABLE tb_checkin (
    id BIGSERIAL PRIMARY KEY,
    inicio TIMESTAMP,
    fim TIMESTAMP,
    id_local BIGINT,
    id_usuario BIGINT,
    id_tipo_atividade BIGINT,
    CONSTRAINT fk_checkin_local FOREIGN KEY (id_local) REFERENCES tb_local(id),
    CONSTRAINT fk_checkin_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT fk_checkin_tipo_atividade FOREIGN KEY (id_tipo_atividade) REFERENCES tb_tipo_atividade(id)
);

-- Tabela de Pontuação de Conquistas
CREATE TABLE IF NOT EXISTS conquista_pontuacao (
                                                   id BIGSERIAL PRIMARY KEY,
                                                   nome VARCHAR(255) NOT NULL,
                                                   id_conquista BIGINT NOT NULL,
                                                   qtd_pontos INTEGER NOT NULL,
                                                   data_alcancada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                   id_usuario BIGINT NOT NULL,
                                                   CONSTRAINT fk_conquista_pontuacao_conquista FOREIGN KEY (id_conquista) REFERENCES tb_conquista(id),
                                                   CONSTRAINT fk_conquista_pontuacao_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario)
);

-- Adiciona índice para melhorar consultas por usuário
CREATE INDEX IF NOT EXISTS idx_conquista_pontuacao_usuario ON conquista_pontuacao(id_usuario);

-- Adiciona índice para melhorar consultas por conquista
CREATE INDEX IF NOT EXISTS idx_conquista_pontuacao_conquista ON conquista_pontuacao(id_conquista);

-- Tabela de Tipos de Detalhe do Usuário
CREATE TABLE IF NOT EXISTS tipo_detalhe (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL
);

-- Tabela de Detalhes do Usuário
CREATE TABLE IF NOT EXISTS tb_detalhes_usuario (
    id_usuario BIGINT NOT NULL,
    id_tipo_detalhe BIGINT NOT NULL,
    valor TEXT,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_usuario, id_tipo_detalhe),
    CONSTRAINT fk_detalhes_usuario_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario),
    CONSTRAINT fk_detalhes_tipo_detalhe FOREIGN KEY (id_tipo_detalhe) REFERENCES tipo_detalhe(id)
);

-- Índices para melhorar consultas
CREATE INDEX IF NOT EXISTS idx_detalhes_usuario ON tb_detalhes_usuario(id_usuario);
CREATE INDEX IF NOT EXISTS idx_detalhes_tipo_detalhe ON tb_detalhes_usuario(id_tipo_detalhe);
