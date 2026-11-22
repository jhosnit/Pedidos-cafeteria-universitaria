-- Esquema de la base de datos de la cafeteria (Release 1.0)
-- Se ejecuta solo la primera vez que se crea el volumen dbdata.

CREATE TABLE usuario (
    id               SERIAL PRIMARY KEY,
    nombre           VARCHAR(60)  NOT NULL,
    apellido         VARCHAR(60)  NOT NULL,
    correo           VARCHAR(120) NOT NULL,
    contrasena_hash  VARCHAR(100) NOT NULL,
    rol              VARCHAR(15)  NOT NULL CHECK (rol IN ('CLIENTE', 'PERSONAL', 'ADMINISTRADOR')),
    fecha_registro   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- El correo se compara sin importar mayusculas (RNF-05)
CREATE UNIQUE INDEX ux_usuario_correo ON usuario (LOWER(correo));

-- Datos propios del cliente (HU-01). El personal y el administrador no tienen fila aqui.
CREATE TABLE cliente (
    usuario_id      INTEGER PRIMARY KEY REFERENCES usuario (id),
    codigo_cliente  VARCHAR(12) NOT NULL UNIQUE,
    cedula_ruc      VARCHAR(13) NOT NULL UNIQUE,
    celular         CHAR(10)    NOT NULL
);

CREATE TABLE producto (
    id          SERIAL PRIMARY KEY,
    codigo      VARCHAR(10)   UNIQUE,
    nombre      VARCHAR(70)   NOT NULL,
    precio      NUMERIC(8, 2) NOT NULL CHECK (precio > 0),
    disponible  BOOLEAN       NOT NULL DEFAULT TRUE
);
-- Nombre unico sin distinguir mayusculas (SUP-11)
CREATE UNIQUE INDEX ux_producto_nombre ON producto (LOWER(nombre));

CREATE TABLE pedido (
    id              SERIAL PRIMARY KEY,
    codigo_retiro   CHAR(5)        NOT NULL CHECK (codigo_retiro ~ '^[A-Z]{2}-[0-9]{2}$'),
    cliente_id      INTEGER        NOT NULL REFERENCES usuario (id),
    fecha_registro  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    importe_total   NUMERIC(10, 2) NOT NULL CHECK (importe_total > 0),
    estado          VARCHAR(20)    NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (estado IN ('PENDIENTE', 'EN_PREPARACION', 'LISTO_PARA_RETIRAR', 'ENTREGADO')),
    CONSTRAINT uq_pedido_codigo_retiro UNIQUE (codigo_retiro)
);
CREATE INDEX ix_pedido_estado ON pedido (estado);
CREATE INDEX ix_pedido_cliente ON pedido (cliente_id);

-- El precio se copia al confirmar para que no cambie despues (RN-NEG-04)
CREATE TABLE detalle_pedido (
    id                 SERIAL PRIMARY KEY,
    pedido_id          INTEGER       NOT NULL REFERENCES pedido (id),
    producto_id        INTEGER       NOT NULL REFERENCES producto (id),
    cantidad           INTEGER       NOT NULL CHECK (cantidad BETWEEN 1 AND 10),
    precio_registrado  NUMERIC(8, 2) NOT NULL CHECK (precio_registrado > 0),
    UNIQUE (pedido_id, producto_id)
);
