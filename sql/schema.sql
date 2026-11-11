-- Gestão de Manutenção Preventiva de Veículos (MySQL)
-- Execute o script uma vez. Ele recria as tabelas e apaga os dados existentes.

CREATE DATABASE IF NOT EXISTS manutencao_veiculos;
USE manutencao_veiculos;

-- Apaga primeiro as tabelas que têm chave estrangeira (agenda e carro),
-- senão o MySQL não deixa apagar as tabelas que elas apontam.
DROP TABLE IF EXISTS agenda;
DROP TABLE IF EXISTS carro;
DROP TABLE IF EXISTS servico;
DROP TABLE IF EXISTS mecanico;
DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(30) NOT NULL UNIQUE,
    senha CHAR(64)    NOT NULL   -- hash SHA-256 da senha, sempre com 64 caracteres
);

CREATE TABLE servico (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(80)   NOT NULL,
    descricao    VARCHAR(200)  NOT NULL DEFAULT '',
    preco        DECIMAL(10,2) NOT NULL,   -- DECIMAL é exato, bom para dinheiro
    intervalo_km INT           NOT NULL DEFAULT 0
);

CREATE TABLE mecanico (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(80) NOT NULL,
    especialidade VARCHAR(60) NOT NULL DEFAULT '',
    telefone      VARCHAR(20) NOT NULL,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE carro (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    placa       VARCHAR(7)   NOT NULL UNIQUE,   -- gravada sem hífen
    modelo      VARCHAR(60)  NOT NULL,
    ano         INT          NOT NULL,
    km          INT          NOT NULL DEFAULT 0,
    problema    VARCHAR(200) NOT NULL DEFAULT '',
    mecanico_id INT NULL,   -- NULL = carro sem mecânico
    -- RESTRICT: não deixa excluir um mecânico que está em algum carro
    CONSTRAINT fk_carro_mecanico FOREIGN KEY (mecanico_id)
        REFERENCES mecanico(id) ON DELETE RESTRICT
);

-- Tabela de relação (associativa): um carro tem vários serviços e um serviço
-- é feito em vários carros (muitos para muitos).
CREATE TABLE agenda (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    carro_id      INT         NOT NULL,
    servico_id    INT         NOT NULL,
    data_agendada DATE        NOT NULL,
    status        ENUM('AGENDADO', 'CONCLUIDO', 'CANCELADO') NOT NULL DEFAULT 'AGENDADO',
    -- CASCADE: excluir o carro apaga a agenda dele junto
    CONSTRAINT fk_agenda_carro FOREIGN KEY (carro_id)
        REFERENCES carro(id) ON DELETE CASCADE,
    -- RESTRICT: serviço que está na agenda não pode ser excluído
    CONSTRAINT fk_agenda_servico FOREIGN KEY (servico_id)
        REFERENCES servico(id) ON DELETE RESTRICT,
    -- o mesmo serviço não pode ser agendado duas vezes no mesmo dia para o mesmo carro
    CONSTRAINT uq_agenda UNIQUE (carro_id, servico_id, data_agendada)
);

INSERT INTO servico (nome, descricao, preco, intervalo_km) VALUES
    ('Troca de óleo', 'Óleo do motor e filtro de óleo', 180.00, 10000),
    ('Alinhamento e balanceamento', 'Alinhamento das 4 rodas', 120.00, 10000),
    ('Troca de pastilhas', 'Pastilhas de freio dianteiras', 250.00, 30000),
    ('Revisão geral', 'Checklist completo de 30 itens', 450.00, 20000);

INSERT INTO mecanico (nome, especialidade, telefone, ativo) VALUES
    ('Carlos Silva', 'Motor', '(19) 99999-1111', TRUE),
    ('Marcos Souza', 'Freios', '(19) 98888-2222', TRUE),
    ('Pedro Lima', 'Suspensão', '(19) 97777-3333', FALSE);

INSERT INTO carro (placa, modelo, ano, km, problema, mecanico_id) VALUES
    ('ABC1D23', 'Fiat Argo', 2020, 45000, 'Barulho ao frear', 2),
    ('XYZ9K88', 'VW Gol', 2018, 82000, 'Revisão preventiva', 1);

INSERT INTO agenda (carro_id, servico_id, data_agendada, status) VALUES
    (1, 3, '2026-11-10', 'AGENDADO'),
    (2, 1, '2026-11-12', 'AGENDADO'),
    (2, 4, '2026-11-12', 'AGENDADO');

-- usuário admin, senha admin123 (aqui vai só o hash)
INSERT INTO usuario (login, senha)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');
