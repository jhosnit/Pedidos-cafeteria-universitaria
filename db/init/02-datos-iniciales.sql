-- Datos iniciales para la demostracion (SRS 2.4).
-- Las credenciales estan en el manual de instalacion. Las contrasenas
-- se guardan con BCrypt, nunca en texto plano (RNF-02).

INSERT INTO usuario (nombre, apellido, correo, contrasena_hash, rol) VALUES
    ('Administrador', 'Cafeteria', 'admin.cafeteria@epn.edu.ec',
     '$2a$10$/rkwqwlSwJujZt1Raisgw.8CFxHj6YH5Gu4NGQZHbXSTVm1nEDUnG', 'ADMINISTRADOR'),
    ('Carlos', 'Mena', 'carlos.mena@epn.edu.ec',
     '$2a$10$bFkL7gq40h8HOtjlXhZUpOD6n0MBA/wTu7L06.G0buMP8.tJKHh9i', 'PERSONAL'),
    ('Ana', 'Torres', 'ana.torres@epn.edu.ec',
     '$2a$10$goBd9Y36NaZ6fZ63Ha.9leI2rpIiqQDYF/ywOX8F3/H45HXzgQYy6', 'CLIENTE');

INSERT INTO cliente (usuario_id, codigo_cliente, cedula_ruc, celular)
SELECT id, 'CLI-' || LPAD(id::TEXT, 5, '0'), '1710034065', '0991234567'
FROM usuario WHERE correo = 'ana.torres@epn.edu.ec';

INSERT INTO producto (nombre, precio, disponible) VALUES
    ('Café americano', 1.00, TRUE),
    ('Capuchino', 1.75, TRUE),
    ('Sánduche de pollo', 2.50, TRUE),
    ('Empanada de verde', 1.25, TRUE),
    ('Humita', 1.50, TRUE),
    ('Bolón de queso', 2.00, TRUE),
    ('Ensalada de frutas', 2.25, TRUE),
    ('Agua sin gas 500 ml', 0.75, TRUE),
    ('Jugo natural de mora', 1.50, FALSE);

UPDATE producto SET codigo = 'PRD-' || LPAD(id::TEXT, 4, '0');
