-- Seed admin user
-- Email: admin@talkie.local
-- Password: 123456 (BCrypt hash with 10 rounds)
INSERT INTO public.users (id, name, email, password, cpf, role, created_at)
VALUES (
    '550e8400-e29b-41d4-a716-446655440000'::uuid,
    'Administrador',
    'admin@talkie.local',
    '$2a$10$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5YmMxSUqrnH7m',
    '000.000.000-00',
    'ADMIN',
    now()
)
ON CONFLICT (email) DO NOTHING;
