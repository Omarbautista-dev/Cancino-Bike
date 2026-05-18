SET FOREIGN_KEY_CHECKS=0;
DROP DATABASE IF EXISTS sig_cb;
SET FOREIGN_KEY_CHECKS=1;
CREATE DATABASE IF NOT EXISTS sig_cb;
USE sig_cb;

-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

-- =========================
-- USUARIOS / SEGURIDAD
-- =========================
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    id_rol INT NOT NULL,
    estado TINYINT DEFAULT 1,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- =========================
-- PRIVILEGIOS
-- =========================
CREATE TABLE privilegios (
    id_privilegio INT AUTO_INCREMENT PRIMARY KEY,
    modulo VARCHAR(50) NOT NULL,
    accion VARCHAR(50) NOT NULL
);

CREATE TABLE rol_privilegios (
    id_rol_privilegio INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    id_privilegio INT NOT NULL,

    FOREIGN KEY (id_rol) REFERENCES roles(id_rol),
    FOREIGN KEY (id_privilegio) REFERENCES privilegios(id_privilegio)
);

-- =========================
-- ACCESOS / HISTORIAL LOGIN
-- =========================
CREATE TABLE accesos (
    id_acceso INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_acceso DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado_acceso VARCHAR(20) NOT NULL,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- =========================
-- PROVEEDORES
-- =========================
CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    nombre_empresa VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(100),
    rfc VARCHAR(20),
    direccion VARCHAR(150),
    estado TINYINT DEFAULT 1
);

-- =========================
-- PRODUCTOS / INVENTARIO
-- =========================
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50) UNIQUE,
    modelo VARCHAR(80),
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(150),
    precio_compra DECIMAL(10,2) NOT NULL,
    precio_menudeo DECIMAL(10,2) NOT NULL,
    precio_mayoreo DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT DEFAULT 1,
    id_proveedor INT,
    estado TINYINT DEFAULT 1,

    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
);

-- =========================
-- LOTES
-- =========================
CREATE TABLE lotes (
    id_lote INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_proveedor INT,
    cantidad INT NOT NULL,
    fecha_entrada DATETIME DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(150),

    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
);

-- =========================
-- AJUSTES DE INVENTARIO
-- =========================
CREATE TABLE ajustes_inventario (
    id_ajuste INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_usuario INT NOT NULL,
    tipo_ajuste ENUM('ENTRADA', 'SALIDA', 'CORRECCION') NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(150),
    fecha_ajuste DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- =========================
-- COMPRAS / HISTORIAL DE COMPRAS
-- =========================
CREATE TABLE compras (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) DEFAULT 0,

    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE detalle_compras (
    id_detalle_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_compra INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_compra) REFERENCES compras(id_compra),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- =========================
-- ÓRDENES DE COMPRA
-- =========================
CREATE TABLE ordenes_compra (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_orden DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'RECIBIDA', 'CANCELADA') DEFAULT 'PENDIENTE',
    total_estimado DECIMAL(10,2) DEFAULT 0,

    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE detalle_orden_compra (
    id_detalle_orden INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_estimado DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_orden) REFERENCES ordenes_compra(id_orden),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- =========================
-- CLIENTES
-- =========================
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion VARCHAR(150),
    estado TINYINT DEFAULT 1
);

-- =========================
-- VENTAS
-- =========================
CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    folio VARCHAR(30) NOT NULL UNIQUE,
    id_usuario INT NOT NULL,
    id_cliente INT,
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo_venta ENUM('CONTADO', 'CREDITO') NOT NULL,
    tipo_precio ENUM('MENUDEO', 'MAYOREO') NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado ENUM('PAGADA', 'PENDIENTE', 'CANCELADA') DEFAULT 'PAGADA',

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE detalle_ventas (
    id_detalle_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

-- =========================
-- CRÉDITOS
-- =========================
CREATE TABLE creditos (
    id_credito INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_cliente INT NOT NULL,
    monto_total DECIMAL(10,2) NOT NULL,
    monto_pagado DECIMAL(10,2) DEFAULT 0,
    saldo_pendiente DECIMAL(10,2) NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADO', 'VENCIDO') DEFAULT 'PENDIENTE',
    fecha_limite DATE,

    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
);

CREATE TABLE pagos_credito (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_credito INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_credito) REFERENCES creditos(id_credito)
);

-- =========================
-- TICKETS DIGITALES
-- =========================
CREATE TABLE tickets (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    folio_ticket VARCHAR(30) NOT NULL UNIQUE,
    fecha_emision DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (id_venta) REFERENCES ventas(id_venta)
);

-- =========================
-- CORTE DE CAJA
-- =========================
CREATE TABLE cortes_caja (
    id_corte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha_corte DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_ventas DECIMAL(10,2) DEFAULT 0,
    total_contado DECIMAL(10,2) DEFAULT 0,
    total_credito DECIMAL(10,2) DEFAULT 0,
    utilidad DECIMAL(10,2) DEFAULT 0,

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- =========================
-- REPORTES
-- =========================
CREATE TABLE reportes (
    id_reporte INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    tipo_reporte ENUM('SEMANAL', 'VENTAS', 'INVENTARIO', 'UTILIDAD', 'CORTE_CAJA') NOT NULL,
    fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(150),

    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- =========================
-- DATOS INICIALES PARA ENTREGA
-- =========================
INSERT INTO roles (nombre_rol, descripcion) VALUES
('ADMIN','Administrador del sistema'),
('VENDEDOR','Usuario encargado de ventas'),
('CAJERO','Usuario encargado de caja y tickets');

INSERT INTO usuarios (usuario,password,nombre_completo,id_rol) VALUES
('admin','12345','Administrador General',1),
('vendedor','12345','Usuario de Ventas',2),
('cajero','12345','Cajero General',3);

INSERT INTO proveedores (nombre_empresa, contacto, telefono, email, rfc, direccion, estado) VALUES
('Shimano México','Carlos Hernández','5551234567','ventas@shimano.com.mx','SHI850101AB1','Av. Industria 120, Ciudad de México',1),
('Mercurio Bicicletas','Ana López','4772233445','contacto@mercurio.com.mx','MER760215CD2','Blvd. Torres Landa 450, León, Guanajuato',1),
('Benotto México','Luis Ramírez','5559876543','ventas@benotto.com','BEN800310EF3','Calz. Ignacio Zaragoza 890, Ciudad de México',1),
('Trek Bikes México','Mariana Torres','8112345678','mexico@trekbikes.com','TRE900522GH4','Av. San Pedro 210, Monterrey, Nuevo León',1),
('Giant Bicycles México','Roberto García','3337654321','ventas@giant-bicycles.mx','GIA910615IJ5','Av. Vallarta 1320, Guadalajara, Jalisco',1),
('Specialized México','Daniela Cruz','5552468101','atencion@specialized.mx','SPE870909KL6','Insurgentes Sur 1450, Ciudad de México',1),
('SRAM México','Miguel Castillo','8187654321','ventas@sram.mx','SRA920101MN7','Parque Industrial Apodaca 77, Nuevo León',1),
('Kenda México','Patricia Morales','4423344556','ventas@kenda.mx','KEN880404OP8','Carretera 57 km 199, Querétaro',1),
('Continental Tires México','Jorge Medina','5551122334','bike@continental.mx','CON790707QR9','Av. Alemania 300, Guadalajara, Jalisco',1),
('Park Tool Distribución','Sofía Aguilar','2224567890','ventas@parktool.mx','PAR930808ST0','Calle Herramientas 55, Puebla, Puebla',1);

INSERT INTO productos (codigo_barras, modelo, nombre_producto, descripcion, precio_compra, precio_menudeo, precio_mayoreo, stock, stock_minimo, id_proveedor, estado) VALUES
('750100000001','SH-MT200','Freno hidráulico Shimano MT200','Freno hidráulico delantero o trasero para MTB',450.00,690.00,620.00,18,3,1,1),
('750100000002','SH-TZ500','Desviador trasero Shimano Tourney TZ500','Desviador trasero 6/7 velocidades',145.00,230.00,205.00,25,5,1,1),
('750100000003','SH-HG40','Cadena Shimano HG40 6/7/8v','Cadena para bicicleta de montaña y ruta',155.00,240.00,215.00,30,6,1,1),
('750100000004','SH-M315','Palanca de cambios Shimano Altus M315','Shifter derecho 8 velocidades',180.00,285.00,255.00,20,4,1,1),
('750100000005','SH-BBUN300','Centro Shimano BB-UN300','Eje de centro cuadrado sellado',210.00,330.00,295.00,16,3,1,1),
('750100000006','MER-ROD26','Rin aluminio Mercurio 26 pulgadas','Rin doble pared para MTB rodada 26',280.00,430.00,390.00,14,3,2,1),
('750100000007','MER-ROD29','Rin aluminio Mercurio 29 pulgadas','Rin doble pared para MTB rodada 29',330.00,520.00,470.00,12,3,2,1),
('750100000008','MER-ASIENTO','Asiento deportivo Mercurio','Asiento cómodo para bicicleta urbana o MTB',135.00,220.00,195.00,22,5,2,1),
('750100000009','MER-PARADOR','Parador lateral ajustable Mercurio','Parador metálico para rodada 24 a 29',95.00,160.00,140.00,28,5,2,1),
('750100000010','MER-CANDADO','Candado de cable Mercurio','Candado con llave para bicicleta',85.00,140.00,125.00,35,8,2,1),
('750100000011','BEN-MANMTB','Manubrio MTB Benotto aluminio','Manubrio recto de aluminio 31.8 mm',120.00,190.00,170.00,18,4,3,1),
('750100000012','BEN-POTENCIA','Potencia Benotto aluminio','Potencia ahead 31.8 mm',115.00,185.00,165.00,20,4,3,1),
('750100000013','BEN-PEDAL','Pedales plataforma Benotto','Pedales plásticos con reflejante',75.00,125.00,110.00,40,10,3,1),
('750100000014','BEN-CAMARA26','Cámara Benotto 26x1.95','Cámara con válvula americana',65.00,110.00,95.00,45,10,3,1),
('750100000015','BEN-CAMARA29','Cámara Benotto 29x2.10','Cámara con válvula americana para MTB',75.00,125.00,110.00,38,8,3,1),
('750100000016','TREK-GRIP','Puños Trek Bontrager XR','Puños ergonómicos para MTB',140.00,230.00,205.00,18,4,4,1),
('750100000017','TREK-BIDON','Ánfora Trek Bontrager 710 ml','Botella para ciclismo color negro',60.00,105.00,90.00,30,8,4,1),
('750100000018','TREK-PORTA','Porta ánfora Bontrager','Porta botella plástico reforzado',75.00,130.00,115.00,26,6,4,1),
('750100000019','TREK-LUZDEL','Luz delantera Bontrager Ion','Luz LED recargable para bicicleta',320.00,520.00,470.00,10,2,4,1),
('750100000020','TREK-LUZTRA','Luz trasera Bontrager Flare','Luz trasera LED recargable',280.00,450.00,405.00,12,2,4,1),
('750100000021','GIA-ASIENTO','Asiento Giant Contact Comfort','Asiento cómodo para ruta o urbano',230.00,360.00,325.00,10,2,5,1),
('750100000022','GIA-CINTA','Cinta de manubrio Giant','Cinta para bicicleta de ruta',165.00,260.00,235.00,15,3,5,1),
('750100000023','GIA-CASCO','Casco Giant Relay','Casco ventilado para ciclismo',480.00,760.00,690.00,8,2,5,1),
('750100000024','GIA-GUANTE','Guantes Giant dedo corto','Guantes acolchados para ciclismo',150.00,240.00,215.00,20,4,5,1),
('750100000025','GIA-BOMBA','Bomba manual Giant Control Mini','Bomba portátil de alta presión',210.00,340.00,305.00,14,3,5,1),
('750100000026','SPE-CASCO','Casco Specialized Align II','Casco de ciclismo con ajuste fino',620.00,980.00,890.00,7,2,6,1),
('750100000027','SPE-GUANTE','Guantes Specialized Body Geometry','Guantes acolchados dedo corto',210.00,340.00,305.00,16,3,6,1),
('750100000028','SPE-LLANTA29','Llanta Specialized Fast Trak 29x2.35','Llanta MTB para terreno mixto',520.00,820.00,740.00,10,2,6,1),
('750100000029','SPE-ASIENTO','Asiento Specialized Bridge Sport','Asiento deportivo para MTB/ruta',430.00,680.00,615.00,9,2,6,1),
('750100000030','SPE-CAMARA700','Cámara Specialized 700x25/32','Cámara válvula presta para ruta',90.00,150.00,135.00,30,6,6,1),
('750100000031','SRAM-PC830','Cadena SRAM PC-830 8v','Cadena para transmisión 8 velocidades',175.00,280.00,250.00,20,4,7,1),
('750100000032','SRAM-PC951','Cadena SRAM PC-951 9v','Cadena para transmisión 9 velocidades',230.00,360.00,325.00,18,4,7,1),
('750100000033','SRAM-X4','Desviador trasero SRAM X4','Desviador 8 velocidades para MTB',310.00,490.00,440.00,10,2,7,1),
('750100000034','SRAM-PG850','Cassette SRAM PG-850 11-32','Cassette 8 velocidades',360.00,570.00,515.00,9,2,7,1),
('750100000035','SRAM-LEVEL','Balatas SRAM Level','Balatas para freno de disco hidráulico',170.00,270.00,240.00,22,4,7,1),
('750100000036','KEN-KRITER26','Llanta Kenda Kriterium 26x1.95','Llanta MTB uso urbano',180.00,290.00,260.00,24,5,8,1),
('750100000037','KEN-KWEST700','Llanta Kenda Kwest 700x28','Llanta urbana para ruta/híbrida',220.00,350.00,315.00,18,4,8,1),
('750100000038','KEN-SMALLBLOCK','Llanta Kenda Small Block 8 29x2.10','Llanta MTB rodada 29',360.00,570.00,515.00,12,3,8,1),
('750100000039','KEN-CAMARA20','Cámara Kenda 20x1.75','Cámara para bicicleta infantil/BMX',55.00,95.00,85.00,35,8,8,1),
('750100000040','KEN-CAMARA24','Cámara Kenda 24x1.95','Cámara válvula americana',60.00,100.00,90.00,32,8,8,1),
('750100000041','CON-RIDE700','Llanta Continental Ride Tour 700x32','Llanta urbana antiponchaduras',280.00,450.00,405.00,12,3,9,1),
('750100000042','CON-RACE28','Cámara Continental Race 28','Cámara ruta válvula presta',85.00,145.00,130.00,30,6,9,1),
('750100000043','CON-TUBE29','Cámara Continental MTB 29','Cámara rodada 29 válvula americana',95.00,160.00,145.00,25,5,9,1),
('750100000044','CON-KING29','Llanta Continental Cross King 29x2.20','Llanta MTB para cross country',520.00,830.00,750.00,8,2,9,1),
('750100000045','CON-GATORSKIN','Llanta Continental Gatorskin 700x25','Llanta ruta resistente a ponchaduras',610.00,960.00,870.00,7,2,9,1),
('750100000046','PARK-TL1','Desmontadores Park Tool TL-1.2','Juego de desmontadores para llanta',65.00,115.00,100.00,30,8,10,1),
('750100000047','PARK-CC4','Medidor de cadena Park Tool CC-4','Herramienta para medir desgaste de cadena',190.00,310.00,280.00,10,2,10,1),
('750100000048','PARK-CT5','Corta cadenas Park Tool CT-5','Herramienta compacta para cadena',350.00,560.00,505.00,8,2,10,1),
('750100000049','PARK-HEX','Juego llaves Allen Park Tool','Llaves hexagonales para taller',260.00,420.00,380.00,12,3,10,1),
('750100000050','PARK-BBB4','Cepillo limpieza Park Tool BCB-4','Kit de cepillos para transmisión',180.00,290.00,260.00,15,3,10,1);

INSERT INTO clientes (nombre_cliente, telefono, email, direccion, estado) VALUES
('Cliente general','0000000000','cliente@general.com','Sin dirección',1),
('Pedro Sánchez','9631112233','pedro.sanchez@mail.com','Comitán de Domínguez, Chiapas',1),
('Laura Gómez','9632223344','laura.gomez@mail.com','Las Margaritas, Chiapas',1),
('Miguel Ángel Pérez','9633334455','miguel.perez@mail.com','La Trinitaria, Chiapas',1),
('María Fernanda Ruiz','9634445566','maria.ruiz@mail.com','Comitán de Domínguez, Chiapas',1);

INSERT INTO privilegios (modulo, accion) VALUES
('VENTAS','ABRIR'),
('VENTAS','REGISTRAR'),
('VENTAS','CANCELAR'),
('VENTAS','BUSCAR_MODELO'),
('VENTAS','TICKET_DIGITAL'),
('INVENTARIO','ABRIR'),
('INVENTARIO','CREAR_PRODUCTO'),
('INVENTARIO','ACTUALIZAR_STOCK'),
('INVENTARIO','CONTROL_LOTES'),
('INVENTARIO','AJUSTAR_INVENTARIO'),
('INVENTARIO','ACTUALIZAR_PRECIOS'),
('INVENTARIO','ELIMINAR_PRODUCTO'),
('PROVEEDORES','ABRIR'),
('PROVEEDORES','CREAR'),
('PROVEEDORES','ACTUALIZAR'),
('PROVEEDORES','ELIMINAR'),
('PROVEEDORES','HISTORIAL_COMPRAS'),
('PROVEEDORES','ORDEN_COMPRA'),
('REPORTES','ABRIR'),
('REPORTES','SEMANAL'),
('REPORTES','VENTAS'),
('REPORTES','INVENTARIO'),
('REPORTES','UTILIDAD'),
('REPORTES','CORTE_CAJA'),
('SEGURIDAD','ABRIR'),
('SEGURIDAD','USUARIOS'),
('SEGURIDAD','PRIVILEGIOS'),
('SEGURIDAD','ACCESOS'),
('SEGURIDAD','CAMBIAR_PASSWORD');

-- Permisos completos para ADMIN
INSERT INTO rol_privilegios (id_rol, id_privilegio) SELECT 1, id_privilegio FROM privilegios;

-- Permisos para VENDEDOR
INSERT INTO rol_privilegios (id_rol, id_privilegio)
SELECT 2, id_privilegio FROM privilegios
WHERE modulo IN ('VENTAS','INVENTARIO','REPORTES') AND accion NOT IN ('CANCELAR','ELIMINAR_PRODUCTO','UTILIDAD');

-- Permisos para CAJERO
INSERT INTO rol_privilegios (id_rol, id_privilegio)
SELECT 3, id_privilegio FROM privilegios
WHERE (modulo='VENTAS' AND accion IN ('ABRIR','REGISTRAR','BUSCAR_MODELO','TICKET_DIGITAL'))
   OR (modulo='REPORTES' AND accion IN ('ABRIR','CORTE_CAJA'));

INSERT INTO lotes (id_producto, id_proveedor, cantidad, observaciones) VALUES
(1,1,18,'Carga inicial de inventario'),
(2,1,25,'Carga inicial de inventario'),
(3,1,30,'Carga inicial de inventario'),
(4,1,20,'Carga inicial de inventario'),
(5,1,16,'Carga inicial de inventario'),
(6,2,14,'Carga inicial de inventario'),
(7,2,12,'Carga inicial de inventario'),
(8,2,22,'Carga inicial de inventario'),
(9,2,28,'Carga inicial de inventario'),
(10,2,35,'Carga inicial de inventario'),
(11,3,18,'Carga inicial de inventario'),
(12,3,20,'Carga inicial de inventario'),
(13,3,40,'Carga inicial de inventario'),
(14,3,45,'Carga inicial de inventario'),
(15,3,38,'Carga inicial de inventario'),
(16,4,18,'Carga inicial de inventario'),
(17,4,30,'Carga inicial de inventario'),
(18,4,26,'Carga inicial de inventario'),
(19,4,10,'Carga inicial de inventario'),
(20,4,12,'Carga inicial de inventario'),
(21,5,10,'Carga inicial de inventario'),
(22,5,15,'Carga inicial de inventario'),
(23,5,8,'Carga inicial de inventario'),
(24,5,20,'Carga inicial de inventario'),
(25,5,14,'Carga inicial de inventario'),
(26,6,7,'Carga inicial de inventario'),
(27,6,16,'Carga inicial de inventario'),
(28,6,10,'Carga inicial de inventario'),
(29,6,9,'Carga inicial de inventario'),
(30,6,30,'Carga inicial de inventario'),
(31,7,20,'Carga inicial de inventario'),
(32,7,18,'Carga inicial de inventario'),
(33,7,10,'Carga inicial de inventario'),
(34,7,9,'Carga inicial de inventario'),
(35,7,22,'Carga inicial de inventario'),
(36,8,24,'Carga inicial de inventario'),
(37,8,18,'Carga inicial de inventario'),
(38,8,12,'Carga inicial de inventario'),
(39,8,35,'Carga inicial de inventario'),
(40,8,32,'Carga inicial de inventario'),
(41,9,12,'Carga inicial de inventario'),
(42,9,30,'Carga inicial de inventario'),
(43,9,25,'Carga inicial de inventario'),
(44,9,8,'Carga inicial de inventario'),
(45,9,7,'Carga inicial de inventario'),
(46,10,30,'Carga inicial de inventario'),
(47,10,10,'Carga inicial de inventario'),
(48,10,8,'Carga inicial de inventario'),
(49,10,12,'Carga inicial de inventario'),
(50,10,15,'Carga inicial de inventario');

INSERT INTO ventas (folio, id_usuario, id_cliente, tipo_venta, tipo_precio, subtotal, descuento, total, estado) VALUES
('V-000001',2,1,'CONTADO','MENUDEO',690.00,0.00,690.00,'PAGADA'),
('V-000002',3,2,'CONTADO','MENUDEO',430.00,0.00,430.00,'PAGADA'),
('V-000003',2,3,'CREDITO','MAYOREO',870.00,0.00,870.00,'PENDIENTE');

INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1,1,1,690.00,690.00),
(2,6,1,430.00,430.00),
(3,45,1,870.00,870.00);

INSERT INTO tickets (id_venta, folio_ticket, total) VALUES
(1,'T-000001',690.00),
(2,'T-000002',430.00);

INSERT INTO creditos (id_venta, id_cliente, monto_total, monto_pagado, saldo_pendiente, estado, fecha_limite) VALUES
(3,3,870.00,200.00,670.00,'PENDIENTE', DATE_ADD(CURDATE(), INTERVAL 30 DAY));

INSERT INTO pagos_credito (id_credito, monto) VALUES (1,200.00);

INSERT INTO cortes_caja (id_usuario, total_ventas, total_contado, total_credito, utilidad) VALUES
(3,1990.00,1120.00,870.00,520.00);

INSERT INTO reportes (id_usuario, tipo_reporte, descripcion) VALUES
(1,'INVENTARIO','Reporte inicial de inventario'),
(1,'VENTAS','Reporte de ventas de prueba'),
(1,'CORTE_CAJA','Corte de caja de demostración');
