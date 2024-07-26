INSERT INTO public.tb_endereco ("rua", "cidade", "estado", "cep", "coordenada", "data_criacao")
VALUES ('Rua Exemplo', 'Cidade Exemplo', 'Estado Exemplo', '12345-678', '40.7128° N, 74.0060° W', '2024-03-14 10:00:00');
INSERT INTO public.tb_endereco ("rua", "cidade", "estado", "cep", "coordenada", "data_criacao")
VALUES ('Avenida Central', 'São Paulo', 'SP', '01234-567', '23.5505° S, 46.6333° W', '2024-03-14 10:00:00');
INSERT INTO public.tb_endereco ("rua", "cidade", "estado", "cep", "coordenada", "data_criacao")
VALUES ('Rua dos Sonhos', 'Rio de Janeiro', 'RJ', '89012-345', '22.9068° S, 43.1729° W', '2024-03-14 10:00:00');

INSERT INTO public.tb_usuario ("nome_usuario", "idade_usuario", "sexo_usuario", "tipo_usuario", "senha_usuario", "token_usuario", "email_usuario", "id_endereco")
VALUES ('Maria', 80, 'Feminino', 'CADASTRADO', 'd9015ac639774caa148c8860f06f0d0092a0dfb3a851a8e04bc5887e79c2ae2b', 'TOKENDAMENINAALI', 'maria@email.com', 1);

INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Caminhada');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Corrida');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Ciclismo');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Natacao');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Hidroginastica');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Musculacao');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Pilates');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Futebol');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES  ('Lutas e Artes Marciais');
INSERT INTO public.tb_tipo_atividade ("nome") VALUES ('Danca');

INSERT INTO public.tipo_local ("nome") VALUES ('Parque');
INSERT INTO public.tipo_local ("nome") VALUES ('Área verde');
INSERT INTO public.tipo_local ("nome") VALUES ('Atividades de condicionamento físico');
INSERT INTO public.tipo_local ("nome") VALUES ('Dança');
INSERT INTO public.tipo_local ("nome") VALUES ('Esporte');

INSERT INTO public.tipo_acesso ("nome") VALUES ('Gratuito');
INSERT INTO public.tipo_acesso ("nome") VALUES ('Pago');
