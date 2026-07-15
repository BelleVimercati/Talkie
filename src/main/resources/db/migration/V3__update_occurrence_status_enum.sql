-- Atualizar valores de status para os novos valores do enum
UPDATE occurrences SET status = 'ABERTO' WHERE status = 'PENDENTE';
UPDATE occurrences SET status = 'FECHADO' WHERE status = 'DECLINADO';

-- Alterar o padrão do status para ABERTO
ALTER TABLE occurrences
ALTER COLUMN status SET DEFAULT 'ABERTO';
