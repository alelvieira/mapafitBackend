-- Define explicitamente o schema para evitar problemas de qualificação
SET search_path TO public;

INSERT INTO tb_endereco (rua, cidade, estado, numero, cep, data_criacao)
VALUES
    ('Rua Exemplo','Cidade Exemplo','SP',100,'12345678',2024-03-14 10:00:00.000000),
    ('Avenida Central','São Paulo','SP',200,'01234567',2024-03-14 10:00:00.000000),
    ('Rua dos Sonhos','Rio de Janeiro','RJ',300,'89012345',2024-03-14 10:00:00.000000),
    ('Rua Jardim Botânico','Curitiba','PR',400,'80210391',2025-11-18 23:40:57.266812),
    ('Avenida Afonso Pena','Belo Horizonte','MG',500,'30130001',2025-11-18 23:40:57.266812),
    ('Avenida Oceânica','Salvador','BA',600,'40140130',2025-11-18 23:40:57.266812),
    ('Avenida Ipiranga','Porto Alegre','RS',700,'90160090',2025-11-18 23:40:57.266812),
    ('Rua Melchiades Silveira do Valle', 'Curitiba', 'PR', 451, '80000000', now()),
    ('Rua Mateus Leme', 'Curitiba', 'PR', 4672, '00000000', now()),
    ('Rua Rodrigo de Freitas', 'Curitiba', 'PR', 391, '00000000', now()),
    ('Rua Guilherme Ihlenfeldt', 'Curitiba', 'PR', 233, '00000000', now()),
    ('Rua Rio Jari', 'Curitiba', 'PR', 1527, '00000000', now()),
    ('Avenida Luiza Gulin', 'Curitiba', 'PR', 115, '00000000', now()),
    ('Rua Pintor Ricardo Krieger', 'Curitiba', 'PR', 550, '00000000', now()),
    ('Rua Arno Feliciano de Castilho', 'Curitiba', 'PR', 13271, '00000000', now()),
    ('Rua Guilherme de Souza Valente', 'Curitiba', 'PR', 145, '00000000', now()),
    ('Rua Cel. Domingos Soares', 'Curitiba', 'PR', 1896, '00000000', now()),
    ('Rua Paulo Friebe', 'Curitiba', 'PR', 20, '00000000', now()),
    ('Rua Laranjeiras', 'Curitiba', 'PR', 170, '00000000', now()),
    ('Rua Antônio Cândido Cavalim', 'Curitiba', 'PR', 29, '00000000', now()),
    ('Rua Prof. Guilherme Butler', 'Curitiba', 'PR', 538, '00000000', now()),
    ('Rua Amaury Lange Silvério', 'Curitiba', 'PR', 639, '00000000', now()),
    ('Rua Rosa Sanson da Silva', 'Curitiba', 'PR', 20, '00000000', now()),
    ('Rua Rodrigo de Freitas', 'Curitiba', 'PR', 391, '00000000', now()),
    ('Rua Cel. Ary Pinho', 'Curitiba', 'PR', 680, '00000000', now()),
    ('Rua Erasmo Maeder', 'Curitiba', 'PR', 222, '00000000', now()),
    ('Rua Egas Rosa Sampaio', 'Curitiba', 'PR', 40, '00000000', now()),
    ('Rua Manife Tacla', 'Curitiba', 'PR', 1246, '00000000', now()),
    ('Rua Avicena', 'Curitiba', 'PR', 90, '00000000', now()),
    ('Rua Santo Afonso de Ligório', 'Curitiba', 'PR', 290, '00000000', now()),
    ('Rua Paulo Mader Bittencourt', 'Curitiba', 'PR', 110, '00000000', now()),
    ('Rua Idalino Francisco Túlio', 'Curitiba', 'PR', 575, '00000000', now()),
    ('Rua Rio Guaíba', 'Curitiba', 'PR', 1250, '00000000', now()),
    ('Rua Costa Rica', 'Curitiba', 'PR', 1614, '00000000', now()),
    ('Rua Howell Lewis Fry', 'Curitiba', 'PR', 181, '00000000', now()),
    ('Rua Guido Scotti', 'Curitiba', 'PR', 352, '00000000', now()),
    ('Rua Doutor Pedro Augusto Menna Barreto Monclaro', 'Curitiba', 'PR', 3020, '80250040', now()),
    ('Rua Daniel Cesário Pereira', 'Curitiba', 'PR', 681, '82410180', now()),
    ('Avenida Paraná', 'Curitiba', 'PR', 1371, '82510000', now());



INSERT INTO tb_usuario (nome_usuario, idade_usuario, sexo_usuario, tipo_usuario, senha_usuario, email_usuario, id_endereco)
VALUES
('Admin', 75, 'Masculino', 'ADMINISTRADOR', '12345678', 'admin@email.com', 2);


INSERT INTO tb_tipo_atividade (nome)
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

INSERT INTO tb_tipo_local (nome)
VALUES
    ('Parque'),
    ('Área verde'),
    ('Atividades de condicionamento físico'),
    ('Dança'),
    ('Esporte');

INSERT INTO tb_tipo_acesso (nome)
VALUES
    ('Gratuito'),
    ('Pago');

INSERT INTO tb_local (nome, aprovado, endereco_id, tipo_atividade_id, informacoes_adicionais, horarios_funcionamento, tipo_acesso_id)
VALUES
    ('Parque Tingui', true, 1, 1, 'Parque', '24h', 1),
    ('Parque São Lourenço', true, 2, 1, 'Parque', '24h', 1),
    ('Parque Bacacheri', true, 3, 1, 'Parque', '24h', 1),
    ('Centro de Esporte e Lazer Avelino Vieira', true, 4, 1, 'Centro de esportes', '24h', 1),
    ('Praça Liberdade', true, 5, 1, 'Praça', '24h', 1),
    ('Solar Coronel Adélio Conti', true, 6, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Parque Atuba', true, 7, 1, 'Parque', '24h', 1),
    ('Vilinha / Max Sesselmeir', true, 8, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Praça Acir Macedo Guimarães', true, 9, 1, 'Praça', '24h', 1),
    ('Praça Bernardo Manuel Hostin', true, 10, 1, 'Praça', '24h', 1),
    ('Bosque Irmã Clementina', true, 11, 1, 'Área verde', '24h', 1),
    ('Jardinete Habib Taherzadeh', true, 12, 1, 'Área verde', '24h', 1),
    ('Largo dos Colonizadores', true, 13, 1, 'Praça', '24h', 1),
    ('Santa Efigênia', true, 14, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Padre Giovanni Graceffa', true, 15, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Estribo Ahu / João Túlio', true, 16, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Parque Bacacheri (2ª Unidade)', true, 17, 1, 'Parque', '24h', 1),
    ('Praça Anna Maurer', true, 18, 1, 'Praça', '24h', 1),
    ('Erasmo Maeder', false 19, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Anibal Afonso', false, 20, 1, 'Academia ao Ar Livre', '24h', 1),
    ('São Marcos', false, 21, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Cruzeiro do Sul', false, 22, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Praça Cap. Joviniano P. de Camargo', true, 23, 1, 'Praça', '24h', 1),
    ('Praça Estevão Mussak', true, 24, 1, 'Praça com futebol', '24h', 1),
    ('Faxinal', true, 25, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Bairro Alto', true, 26, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Aurora Lambertucci Geronasso', true, 27, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Leonice', true, 28, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Guido Scotti', true, 29, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Centro de Esporte e Lazer Rua da Cidadania Boa Vista', true, 32, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Clube da Gente Santa Felicidade', true, 31, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Praça Afonso Botelho', true, 30, 1, 'Parque', '24h', 1);


select * from tb_local