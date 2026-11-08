-- Exclusão lógica de categorias e subcategorias: desativadas somem dos formulários,
-- mas continuam referenciadas pelas ocorrências e assinaturas existentes.
ALTER TABLE types ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE subcategories ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
