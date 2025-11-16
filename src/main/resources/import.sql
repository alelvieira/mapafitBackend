INSERT INTO public.tb_endereco (rua, cidade, estado, bairro, cep, data_criacao)
VALUES
    ('Rua Melchiades Silveira do Valle, 451', 'Curitiba', 'PR', null, now()),
    ('Rua Mateus Leme, 4672', 'Curitiba', 'PR', null, now()),
    ('Rua Rodrigo de Freitas, 391', 'Curitiba', 'PR', null, now()),
    ('Rua Guilherme Ihlenfeldt, 233', 'Curitiba', 'PR', null, now()),
    ('Rua Rio Jari, 1527', 'Curitiba', 'PR', null, now()),
    ('Avenida Luiza Gulin, 115', 'Curitiba', 'PR', null, now()),
    ('Rua Pintor Ricardo Krieger, 550', 'Curitiba', 'PR', null, now()),
    ('Rua Arno Feliciano de Castilho, 13271', 'Curitiba', 'PR', null, now()),
    ('Rua Guilherme de Souza Valente, 145', 'Curitiba', 'PR', null, now()),
    ('Rua Cel. Domingos Soares, 1896', 'Curitiba', 'PR', null, now()),
    ('Rua Paulo Friebe, 20', 'Curitiba', 'PR', null, now()),
    ('Rua Laranjeiras, 170', 'Curitiba', 'PR', null, now()),
    ('Rua Antônio Cândido Cavalim, 29', 'Curitiba', 'PR', null, now()),
    ('Rua Prof. Guilherme Butler, 538', 'Curitiba', 'PR', null, now()),
    ('Rua Amaury Lange Silvério, 639', 'Curitiba', 'PR', null, now()),
    ('Rua Rosa Sanson da Silva, 20', 'Curitiba', 'PR', null, now()),
    ('Rua Rodrigo de Freitas, 391', 'Curitiba', 'PR', null, now()),
    ('Rua Cel. Ary Pinho, 680', 'Curitiba', 'PR', null, now()),
    ('Rua Erasmo Maeder, 222', 'Curitiba', 'PR', null, now()),
    ('Rua Egas Rosa Sampaio, 40', 'Curitiba', 'PR', null, now()),
    ('Rua Manife Tacla, 1246', 'Curitiba', 'PR', null, now()),
    ('Rua Avicena, 90', 'Curitiba', 'PR', null, now()),
    ('Rua Santo Afonso de Ligório, 290', 'Curitiba', 'PR', null, now()),
    ('Rua Paulo Mader Bittencourt, 110', 'Curitiba', 'PR', null, now()),
    ('Rua Idalino Francisco Túlio, 575', 'Curitiba', 'PR', null, now()),
    ('Rua Rio Guaíba, 1250', 'Curitiba', 'PR', null, now()),
    ('Rua Costa Rica, 1614', 'Curitiba', 'PR', null, now()),
    ('Rua Howell Lewis Fry, 181', 'Curitiba', 'PR', null, now()),
    ('Rua Guido Scotti, 352', 'Curitiba', 'PR', null, now()),
    ('Rua Doutor Pedro Augusto Menna Barreto Monclaro, 3020', 'Curitiba', 'PR', 80.250-040, now()),
    ('Rua Daniel Cesário Pereira, 681', 'Curitiba', 'PR', 82.410-180, now()),
    ('Avenida Paraná, 1371', 'Curitiba', 'PR', 82.510-000, now());



INSERT INTO public.tb_usuario (nome_usuario, idade_usuario, sexo_usuario, tipo_usuario, senha_usuario, email_usuario, id_endereco)
VALUES
    ('Maria', 80, 'Feminino', 'CADASTRADO', 'd9015ac639774caa148c8860f06f0d0092a0dfb3a851a8e04bc5887e79c2ae2b',  'maria@email.com', 1),
    ('Admin', 75, 'Masculino', 'ADMINISTRADOR', '12345678', 'admin@email.com', 2);


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

INSERT INTO public.tb_local (nome, aprovado, endereco_id, tipo_atividade_id, informacoes_adicionais, horarios_funcionamento, tipo_acesso_id)
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
    ('Erasmo Maeder', true, 19, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Anibal Afonso', true, 20, 1, 'Academia ao Ar Livre', '24h', 1),
    ('São Marcos', true, 21, 1, 'Academia ao Ar Livre', '24h', 1),
    ('Cruzeiro do Sul', true, 22, 1, 'Academia ao Ar Livre', '24h', 1),
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