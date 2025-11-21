INSERT INTO public.tb_tipo_atividade (nome)
VALUES
    ('Caminhada'),
    ('Corrida'),
    ('Ciclismo'),
    ('Natacao'),
    ('Hidroginastica'),
    ('Musculacao'),
    ('Pilates'),
    ('Futebol'),
    ('LutasArtesMarciais'),
    ('Danca');

INSERT INTO public.tb_tipo_local (nome)
VALUES
    ('Parque'),
    ('Área verde'),
    ('Atividades de condicionamento físico'),
    ('Dança'),
    ('Esporte');

INSERT INTO public.tb_tipo_acesso (nome)
VALUES
    ('Gratuito'),
    ('Pago');

INSERT INTO public.tb_endereco (id_endereco, rua, cidade, estado, numero, cep, data_criacao, localizacao)
VALUES
    (1, 'Rua Exemplo', 'Cidade Exemplo', 'SP', 100, '12345678', '2024-03-14 10:00:00', ST_SetSRID(ST_MakePoint(-46.6333, -23.5505), 4326)),
    (2, 'Avenida Central', 'São Paulo', 'SP', 200, '01234567', '2024-03-14 10:00:00', ST_SetSRID(ST_MakePoint(-46.6333, -23.5505), 4326)),
    (3, 'Rua dos Sonhos', 'Rio de Janeiro', 'RJ', 300, '89012345', '2024-03-14 10:00:00', ST_SetSRID(ST_MakePoint(-43.1729, -22.9068), 4326));

-- Endereços para os novos locais
INSERT INTO public.tb_endereco (id_endereco, rua, cidade, estado, numero, cep, data_criacao, localizacao)
VALUES
    (4, 'Rua Jardim Botânico', 'Curitiba', 'PR', 400, '80210391', now(), ST_SetSRID(ST_MakePoint(-49.23810, -25.44306), 4326)),
    (5, 'Avenida Afonso Pena', 'Belo Horizonte', 'MG', 500, '30130001', now(), ST_SetSRID(ST_MakePoint(-43.9345, -19.9167), 4326)),
    (6, 'Avenida Oceânica', 'Salvador', 'BA', 600, '40140130', now(), ST_SetSRID(ST_MakePoint(-38.5267, -12.9714), 4326)),
    (7, 'Avenida Ipiranga', 'Porto Alegre', 'RS', 700, '90160090', now(), ST_SetSRID(ST_MakePoint(-51.2264, -30.0346), 4326));

-- ATENÇÃO: A senha abaixo é um exemplo de hash BCrypt para a senha '12345'. Substitua se necessário.
INSERT INTO public.tb_usuario (nome_usuario, idade_usuario, sexo_usuario, tipo_usuario, senha_usuario, email_usuario, id_endereco)
VALUES
    ('Maria', 80, 'Feminino', 'CADASTRADO', '$2a$10$3A.ADi3hJ.3r2tA.e.1s9uUaL5uFz8r.1iJg.h.k.9s8uUaL5uFz', 'maria@email.com', 1),
    ('João Silva', 25, 'Masculino', 'CADASTRADO', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'joao.silva@email.com', 2),
    ('Ana Pereira', 32, 'Feminino', 'CADASTRADO', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ana.pereira@email.com', 3);

INSERT INTO public.tb_local (nome, aprovado, endereco_id, tipo_atividade_id, informacoes_adicionais, horarios_funcionamento, tipo_acesso_id, tipo_local_id)
VALUES
    ('Jardim Botânico de Curitiba', true, 4, 1, 'Parque ao ar livre para caminhadas.', '06:00 - 20:00', 1, 1),
    ('Academia Corpo em Forma', true, 5, 6, 'Academia completa com equipamentos modernos.', '06:00 - 23:00', 2, 3),
    ('Campo de Futebol do Bairro', true, 6, 8, 'Campo público, aberto a todos.', 'Aberto 24h', 1, 5),
    ('Clube de Natação Águas Claras', true, 7, 4, 'Piscina olímpica e aulas para todas as idades.', '07:00 - 21:00', 2, 5);

-- Definições de Conquistas
INSERT INTO public.tb_conquista (id, titulo, descricao, icone)
VALUES
    (1, 'Pioneiro', 'Você fez seu primeiro check-in no Mapa da Vida!', 'checkin'),
    (5, 'Crítico Construtivo', 'Você fez sua primeira avaliação de um local.', 'avaliacao'),
    (10, 'Explorador', 'Você fez 10 check-ins.', 'explorador');

-- Avaliações Iniciais
INSERT INTO public.tb_avaliacao (id_usuario, id_local, nota, comentario)
VALUES
    (2, 2, 5, 'Ótima academia, equipamentos novos e ambiente agradável!'), -- João avalia a Academia Corpo em Forma
    (3, 4, 4, 'O clube é muito bom, mas poderia ter mais horários disponíveis.'); -- Ana avalia o Clube de Natação