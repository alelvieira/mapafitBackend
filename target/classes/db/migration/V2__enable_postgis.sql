-- Cria a extensão PostGIS necessária para tipos geometry
-- Observação: o usuário do banco precisa ter permissão para criar extensões (normalmente superuser).
CREATE EXTENSION IF NOT EXISTS postgis;
-- Para garantir versão específica ou criar no schema public, use:
-- CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;

