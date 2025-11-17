-- Update the check constraint to include 'ADMIN' as a valid value
ALTER TABLE tb_usuario 
DROP CONSTRAINT IF EXISTS tb_usuario_tipo_usuario_check;

ALTER TABLE tb_usuario 
ADD CONSTRAINT tb_usuario_tipo_usuario_check 
CHECK (tipo_usuario IN ('VISITANTE', 'CADASTRADO', 'ADMINISTRADOR'));
