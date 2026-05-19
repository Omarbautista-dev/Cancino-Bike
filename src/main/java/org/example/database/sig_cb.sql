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
-- DATOS INICIALES PARA ENTREGA CON PRODUCTOS REALES CANCINO BIKE
-- Fuente: PDF de productos reales.
-- Nota: el PDF solo trae Precio Venta; por eso:
--       precio_menudeo = Precio Venta, precio_mayoreo = 90% del Precio Venta, precio_compra = 65% del Precio Venta.
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
('Cancino Bike Inventario General','Gerardo Cancino Altuzar','9630000000','ventas@cancinobike.local','CBI000000AA1','Comitán de Domínguez, Chiapas',1),
('Proveedor de Refacciones Ciclistas','Área de Compras','9630000001','refacciones@cancinobike.local','PRC000000AA2','Comitán de Domínguez, Chiapas',1),
('Proveedor de Motorefacciones','Área de Compras','9630000002','moto@cancinobike.local','PMO000000AA3','Comitán de Domínguez, Chiapas',1);

INSERT INTO productos (codigo_barras, modelo, nombre_producto, descripcion, precio_compra, precio_menudeo, precio_mayoreo, stock, stock_minimo, id_proveedor, estado) VALUES
('31162412','31162412','ABRAZADERA DE ASIENTO','[Accesorios] De aluminio 6mm para ajuste fijo de poste. Unidad: PZA.',78.00,120.00,108.00,15,3,1,1),
('31162414','31162414','ABRAZADERA C/BLOQUEO','[Accesorios] Aluminio 31.8mm con palanca de liberación rápida Unidad: PZA.',94.25,145.00,130.50,10,2,1,1),
('LUB-001','LUB-001','ACEITE NHL AZUL','[Químicos] Lubricante de cadena de larga duración (antifricció Unidad: PZA.',55.25,85.00,76.50,20,4,1,1),
('ADAP-MTB','ADAP-MTB','ADAPTADOR AUM/RED','[ieRzeafsa.cciones] Adaptador metálico para diferentes diámetros de p Unidad: PZA.',91.00,140.00,126.00,8,2,1,1),
('ARO-24C','ARO-24C','ARO ACERO 24X1.175','[Ruedas] Aro cromado reforzado 12G de 36 hoyos para R24. Unidad: PZA.',312.00,480.00,432.00,6,2,2,1),
('ASI-CRU-AZ','ASI-CRU-AZ','ASIENTO CRUSIER JUV','[Asientos] Asiento ancho ultra suave color azul para paseo. Unidad: PZA.',208.00,320.00,288.00,5,1,1,1),
('ASI-ALT-16','ASI-ALT-16','ASTO JUVENIL ALTREGO','[Asientos] Asiento ergonómico negro acolchado para R16 a R Unidad: PZA.',159.25,245.00,220.50,12,3,1,1),
('ASI-ALT-MTB','ASI-ALT-MTB','ASTO MTB ALTREGO','[amAsaise.ntos] Asiento deportivo modelo Century con diseño de fl Unidad: PZA.',234.00,360.00,324.00,8,2,1,1),
('FRE-KEV-REC','FRE-KEV-REC','BALATA KEVLAR','[Frenos] Pastillas rectangulares de alta fricción y larga vida. Unidad: PA.',143.00,220.00,198.00,15,3,2,1),
('FRE-KEV-RED','FRE-KEV-RED','BALATA KEVLAR','[Frenos] Pastillas redondas premium para frenado de precis Unidad: PA.',126.75,195.00,175.50,15,3,2,1),
('MBAE-6202','MBAE-6202','BALERO NAHEL 6202','[Baleros] Rodamiento sellado con tapas de protección naran Unidad: PZA.',48.75,75.00,67.50,25,5,1,1),
('BAL-6302','BAL-6302','BALERO NAHEL 6302','[Baleros] Rodamiento de alto rendimiento para motor. Unidad: PZA.',61.75,95.00,85.50,20,4,1,1),
('BALE-CENT','BALE-CENT','BALERO P/CENTRO','[Baleros] Balines estándar 1/4 con jaula para eje central. Unidad: PZA.',16.25,25.00,22.50,45,9,1,1),
('BALE-CIG','BALE-CIG','BALERO CIGÜEÑAL','[Baleros] Rodamiento especializado para motor de motocicl Unidad: PZA.',29.25,45.00,40.50,12,3,1,1),
('BALE-TRAS','BALE-TRAS','BALERO TRASERO','[Baleros] Balín estándar para maza trasera de bicicleta. Unidad: PZA.',16.25,25.00,22.50,60,12,1,1),
('MBIR-NHL','MBIR-NHL','BIRLOS NHL125','[Moto] Kit de tornillos reforzados para corona de moto. Unidad: JG.',61.75,95.00,85.50,10,2,3,1),
('BLOQ-6MM','BLOQ-6MM','BLOQUEO ASIENTO','[inAioc.cesorios] Tornillo de seguridad con bloqueo manual de alum Unidad: PZA.',42.25,65.00,58.50,18,4,1,1),
('MBOB-H150','MBOB-H150','BOBINA ENCENDIDO','[Eléctrico] Bobina de encendido original para Honda Cargo GL Unidad: PZA.',247.00,380.00,342.00,5,1,3,1),
('MBOB-110','MBOB-110','BOBINA NHL 110/150','[Eléctrico] Bobina universal para motonetas con capuchón. Unidad: PZA.',169.00,260.00,234.00,8,2,3,1),
('MBOM-NHL','MBOM-NHL','BOMBA NHL200','[Frenos] Bomba de líquido de frenos para sistema de disco. Unidad: PZA.',273.00,420.00,378.00,5,1,2,1),
('BOM-PIE-ALT','BOM-PIE-ALT','BOMBA PIE ALTREGO','[Accesorios] Inflador reforzado de pedal con manómetro. Unidad: PZA.',377.00,580.00,522.00,6,2,1,1),
('MBUJ-NHL','MBUJ-NHL','BUJES NHL 150/250','[Refacciones] Juego de bujes de acero para maza de motocicleta Unidad: JG.',58.50,90.00,81.00,12,3,2,1),
('CAB-BMX-19','CAB-BMX-19','CABLE FRENO BMX','[Cables] Chicote de acero extra largo para trucos y giros. Unidad: PZA.',55.25,85.00,76.50,30,6,1,1),
('1479000','1479000','CABLE FRENO TRASERO','[Cables] Chicote de acero con terminal para bicicleta de rut Unidad: PZA.',35.75,55.00,49.50,40,8,1,1),
('CAD-ALT-114','CAD-ALT-114','CADENA ALTREGO','[Transmisión] Cadena de paso estándar para bicicletas urbanas. Unidad: PZA.',126.75,195.00,175.50,15,3,2,1),
('MCAD-428H','MCAD-428H','CADENA MOTO 428H','[Moto] Cadena de tracción reforzada paso 428 para moto. Unidad: PZA.',312.00,480.00,432.00,6,2,3,1),
('CAL-DEL-160','CAL-DEL-160','CALIPER DELANTERO','[Frenos] Caliper mecánico con disco rotor de 160mm. Unidad: JG.',422.50,650.00,585.00,5,1,2,1),
('CAM-12','CAM-12','CAMARA 12 X 1 1/2','[Cámaras] Cámara de caucho para llanta de bicicleta infantil. Unidad: PZA.',78.00,120.00,108.00,18,4,1,1),
('CAM-16','CAM-16','CAMARA 16 X 2.125','[Cámaras] Cámara inflable para bicicleta rodada 16. Unidad: PZA.',87.75,135.00,121.50,25,5,1,1),
('CAM-20','CAM-20','CAMARA 20 X 2.125','[Cámaras] Cámara reforzada ideal para BMX y uso rudo R20. Unidad: PZA.',94.25,145.00,130.50,30,6,1,1),
('CAM-24','CAM-24','CAMARA 24 X 2.125','[Cámaras] Cámara estándar para bicicleta de montaña R24. Unidad: PZA.',104.00,160.00,144.00,25,5,1,1),
('CAM-26-48','CAM-26-48','CAMARA 26 X 1.50','[Cámaras] Cámara delgada de alta presión con válvula de 48m Unidad: PZA.',117.00,180.00,162.00,15,3,1,1),
('CAM-26-BOL','CAM-26-BOL','CAMARA 26 X 2.125','[Cámaras] Cámara de alta resistencia para montaña R26. Unidad: PZA.',110.50,170.00,153.00,50,10,1,1),
('MCAM-130','MCAM-130','CAMARA MOTO 130/60','[Moto] Cámara para motocicleta marca Goldkylin TR87. Unidad: PZA.',159.25,245.00,220.50,8,2,3,1),
('MCAM-410','MCAM-410','CAMARA MOTO 4.10-18','[Moto] Cámara reforzada para motocicleta doble propósit Unidad: PZA.',182.00,280.00,252.00,8,2,3,1),
('CAMB-MTB','CAMB-MTB','CAMBIO TRAS ALTREGO','[Transmisión] Desviador trasero cromado compatible con 6/7 vel Unidad: PZA.',221.00,340.00,306.00,6,2,2,1),
('CAND-8X-AZ','CAND-8X-AZ','CANDADO CABLE AZUL','[ticAoc.cesorios] Candado de cable de acero con recubrimiento plás Unidad: PZA.',136.50,210.00,189.00,10,2,1,1),
('CAND-8X-RJ','CAND-8X-RJ','CANDADO CABLE ROJO','[Accesorios] Candado de seguridad largo con sistema de llaves. Unidad: PZA.',136.50,210.00,189.00,10,2,1,1),
('MCAN-520H','MCAN-520H','CANDADO CADENA MOTO','[Moto] Eslabón de unión rápido para cadena de moto 520 Unidad: PZA.',42.25,65.00,58.50,25,5,3,1),
('CARB-150Z','CARB-150Z','CARBURADOR 150Z','[Moto] Kit de carburación completo para Italika 150z/170z Unidad: PZA.',747.50,1150.00,1035.00,3,1,3,1),
('CARB-FT125','CARB-FT125','CARBURADOR FT125','[Moto] Carburador de alto flujo para motores de trabajo 12 Unidad: PZA.',637.00,980.00,882.00,4,1,3,1),
('CUA-29-V','CUA-29-V','CUADRO MTB 29"','[Cuadros] Cuadro de aluminio ligero geometría moderna. Unidad: PZA.',2242.50,3450.00,3105.00,2,1,1,1),
('CUA-29-UBV','CUA-29-UBV','CUADRO 29 UB-MAX','[Cuadros] Cuadro profesional gama media-alta Verde Fusión. Unidad: PZA.',2470.00,3800.00,3420.00,1,1,1,1),
('CUA-29-UBVI','CUA-29-UBVI','CUADRO 29 UB-MAX','[Cuadros] Cuadro profesional gama media-alta Violeta Fusión Unidad: PZA.',2470.00,3800.00,3420.00,1,1,1,1),
('CUA-29-N','CUA-29-N','CUADRO MTB 29"','[Cuadros] Cuadro de aluminio reforzado Negro/Naranja. Unidad: PZA.',2242.50,3450.00,3105.00,2,1,1,1),
('DESV-CP','DESV-CP','DESVIADOR TRAS MTB','[Transmisión] Tensor de cadena marca Nahel con soporte de pat Unidad: PZA.',120.25,185.00,166.50,15,3,2,1),
('DESV-SP','DESV-SP','DESVIADOR TRAS MTB','[Transmisión] Tensor de cadena directo para montaje al cuadro. Unidad: PZA.',107.25,165.00,148.50,15,3,2,1),
('DIAB-ALU','DIAB-ALU','DIABLO ALUMINIO','[Accesorios] Par de apoyos traseros/delanteros hexagonales. Unidad: PA.',143.00,220.00,198.00,6,2,1,1),
('MDIS-CLU','MDIS-CLU','DISCOS CLUTCH','[Moto] Kit de pastas de embrague para Italika FT125/150. Unidad: JG.',169.00,260.00,234.00,8,2,3,1),
('2163000','2163000','EJE CENTRO NECO','[Refacciones] Eje de centro sellado con baleros tipo cartucho. Unidad: PZA.',159.25,245.00,220.50,20,4,2,1),
('EJE-145','EJE-145','EJE MAZA DEL','[Ruedas] Eje cromado completo para rueda delantera. Unidad: PZA.',71.50,110.00,99.00,15,3,2,1),
('EJE-210N','EJE-210N','EJE MAZA TRAS NEGRO','[Ruedas] Eje reforzado negro para rueda trasera con tuercas Unidad: PZA.',87.75,135.00,121.50,12,3,2,1),
('EJE-210C','EJE-210C','EJE MAZA TRAS CROM','[Ruedas] Eje cromado de lujo para rueda trasera de bicicleta Unidad: PZA.',94.25,145.00,130.50,12,3,2,1),
('MEMB-125','MEMB-125','EMBRAGUE COMPLETO','[Moto] Centro de embrague con resortes para moto 125. Unidad: PZA.',507.00,780.00,702.00,3,1,3,1),
('MESP-RET','MESP-RET','ESPEJOS MOTO','[Accesorios] Par de espejos de amplia visión para motocicletas. Unidad: PA.',159.25,245.00,220.50,8,2,1,1),
('EXT-VAL','EXT-VAL','EXTENSION VALVULA','[Accesorios] Accesorio para inflado fácil en válvulas cortas. Unidad: PZA.',29.25,45.00,40.50,50,10,1,1),
('FORR-50M','FORR-50M','FORRO CABLE MANDO','[Cables] Funda protectora para chicotes (Rollo de 50m). Unidad: ROL.',552.50,850.00,765.00,2,1,1,1),
('2417500','2417500','FRENO V BRAKE ALU','[Frenos] Kit completo de frenos V-Brake de aluminio. Unidad: JG.',234.00,360.00,324.00,10,2,2,1),
('MINT-PRES','MINT-PRES','SWITCH ENCENDIDO','[Eléctrico] Interruptor de encendido universal con dos llaves. Unidad: PZA.',81.25,125.00,112.50,10,2,3,1),
('MINT-TORN','MINT-TORN','SWITCH ENCENDIDO','[Eléctrico] Switch de llave para montaje con tornillería. Unidad: PZA.',87.75,135.00,121.50,10,2,3,1),
('MLIG-GAN','MLIG-GAN','LIGA PORTABULTOS','[Accesorios] Pulpo elástico reforzado para carga en parrilla. Unidad: PZA.',42.25,65.00,58.50,20,4,1,1),
('LIMP-EST','LIMP-EST','LIMPIARAYO ESTRELLA','[Accesorios] Decoración colorida para rayos (Bolsa 36 pzas). Unidad: PZA.',35.75,55.00,49.50,15,3,1,1),
('LIMP-SOL','LIMP-SOL','LIMPIARAYO SOL','[Accesorios] Decoración modelo Sol para rayos (Bolsa 36 pzas). Unidad: PZA.',35.75,55.00,49.50,15,3,1,1),
('LLAN-12-R','LLAN-12-R','LLANTA 12 X 1/2','[Llantas] Neumático para bicicleta de aprendizaje infantil. Unidad: PZA.',208.00,320.00,288.00,10,2,1,1),
('LLAN-16-B','LLAN-16-B','LLANTA 16 X 2.125','[Llantas] Llanta para bicicleta infantil de gran durabilidad. Unidad: PZA.',185.25,285.00,256.50,15,3,1,1),
('LLAN-20-R','LLAN-20-R','LLANTA 20 X 2.125','[Llantas] Llanta de montaña R20 con gajos para tracción. Unidad: PZA.',221.00,340.00,306.00,20,4,1,1),
('LLAN-20-B','LLAN-20-B','LLANTA 20 X 2.125','[Llantas] Llanta urbana lisa para rodada 20. Unidad: PZA.',191.75,295.00,265.50,20,4,1,1),
('LLAN-24-N','LLAN-24-N','LLANTA 24 X 1.75','[Llantas] Llanta delgada para mayor velocidad en ciudad R2 Unidad: PZA.',201.50,310.00,279.00,12,3,1,1),
('LLAN-24-R','LLAN-24-R','LLANTA 24 X 2.125','[Llantas] Llanta ancha todo terreno para bicicleta R24. Unidad: PZA.',247.00,380.00,342.00,15,3,1,1),
('LLAN-26-N','LLAN-26-N','LLANTA 26 X 1.50','[Llantas] Llanta tipo Slick para rodar rápido en pavimento. Unidad: PZA.',208.00,320.00,288.00,10,2,1,1),
('LLAN-26-J','LLAN-26-J','LLANTA 26 X 2.125','[Llantas] Llanta de alto desempeño marca Jamer Brava HD. Unidad: PZA.',312.00,480.00,432.00,15,3,1,1),
('LLAN-26-1.3','LLAN-26-1.3','LLANTA 26 X 2.125','[Llantas] Llanta pesada NHL para mayor resistencia a ponch Unidad: PZA.',256.75,395.00,355.50,20,4,1,1),
('LLAN-26-T','LLAN-26-T','LLANTA 26 X 2.125','[Llantas] Llanta marca NHL especial para caminos de tierra. Unidad: PZA.',256.75,395.00,355.50,20,4,1,1),
('LLAN-26-S','LLAN-26-S','LLANTA 26X2.125','[Llantas] Llanta económica ideal para renovar tu bicicleta. Unidad: PZA.',201.50,310.00,279.00,30,6,1,1),
('LLAV-ALL','LLAV-ALL','JUEGO LLAVES ALLEN','[Herramientas] Herramientas de acero endurecido de alta calidad. Unidad: JG.',247.00,380.00,342.00,6,2,1,1),
('MAN-NHL','MAN-NHL','MANUBRIO MOTO NHL','[Moto] Manubrio de acero cromado ergonómico. Unidad: PZA.',351.00,540.00,486.00,5,1,3,1),
('MAZ-DEL','MAZ-DEL','MAZA DELANTERA','[Ruedas] Maza de acero económica de 28 hoyos. Unidad: PZA.',107.25,165.00,148.50,15,3,2,1),
('MAZ-TRA','MAZ-TRA','MAZA TRASERA','[Ruedas] Maza trasera de acero con rosca para piñón. Unidad: PZA.',123.50,190.00,171.00,15,3,2,1),
('3099000','3099000','MAZA TRASERA 40H','[Ruedas] Maza especial cromada de 40 hoyos para carga. Unidad: PZA.',143.00,220.00,198.00,4,1,2,1),
('3099200','3099200','MAZAS CASSETTE AZ','[Ruedas] Par de mazas premium aluminio para 10 vel. Azul. Unidad: JG.',1202.50,1850.00,1665.00,2,1,2,1),
('3099500','3099500','MAZAS CASSETTE NG','[grRou.edas] Par de mazas premium aluminio balero sellado Ne Unidad: JG.',1202.50,1850.00,1665.00,3,1,1,1),
('3099600','3099600','MAZAS CASSETTE RJ','[Ruedas] Par de mazas premium aluminio freno disco Rojo. Unidad: JG.',1202.50,1850.00,1665.00,2,1,2,1),
('MULT-GR','MULT-GR','MULTIPLICACION TRI','[Transmisión] Juego de 3 estrellas con protector de cadena gris. Unidad: PZA.',273.00,420.00,378.00,8,2,2,1),
('MULT-NG','MULT-NG','MULTIPLICACION TRI','[Transmisión] Juego de 3 estrellas con protector de cadena negro Unidad: PZA.',273.00,420.00,378.00,8,2,2,1),
('PAL-FRE-P','PAL-FRE-P','PALANCA FRENO','[Frenos] Par de palancas de freno de material compuesto. Unidad: PA.',94.25,145.00,130.50,20,4,2,1),
('PAL-FRE-J','PAL-FRE-J','PALANCA MANDO','[Frenos] Mandos integrados de aluminio para 21 vel. Unidad: PA.',312.00,480.00,432.00,8,2,2,1),
('PALA-IZQ','PALA-IZQ','PALANCA MULT IZQ','[Transmisión] Brazo de repuesto para multiplicación de aluminio. Unidad: PZA.',94.25,145.00,130.50,12,3,2,1),
('PAR-TRA','PAR-TRA','PARADOR TRASERO','[Accesorios] Pata de cabra de acero reforzado para R26. Unidad: PZA.',120.25,185.00,166.50,15,3,1,1),
('PARR-28','PARR-28','PARRILLA 3 BARRAS','[Accesorios] Parrilla trasera clásica para rodada 28 urbana. Unidad: PZA.',318.50,490.00,441.00,6,2,1,1),
('PORT-AZ','PORT-AZ','PORTANFORA AZUL','[Accesorios] Sujetador de botella de agua para cuadro azul. Unidad: PZA.',61.75,95.00,85.50,15,3,1,1),
('PORT-RJ','PORT-RJ','PORTANFORA ROJO','[Accesorios] Sujetador de botella de agua para cuadro rojo. Unidad: PZA.',61.75,95.00,85.50,15,3,1,1),
('3807500','3807500','POSTE ASIENTO 25.4','[Asientos] Poste de acero corto para bicicletas infantiles. Unidad: PZA.',61.75,95.00,85.50,20,4,1,1),
('3809600','3809600','POSTE ASIENTO 25.4','[Asientos] Poste de acero cromado largo para confort. Unidad: PZA.',84.50,130.00,117.00,20,4,1,1),
('25174700','25174700','POSTE ASIENTO 27.2','[Asientos] Poste de aluminio ligero de 30cm para MTB. Unidad: PZA.',191.75,295.00,265.50,10,2,1,1),
('POS-MTB','POS-MTB','POSTE MANUBRIO MTB','[Refacciones] Potencia de acero para manubrio de 22.2mm. Unidad: PZA.',143.00,220.00,198.00,8,2,2,1),
('MPUN-UNI','MPUN-UNI','PUÑOS UNIVERSALES','[Accesorios] Par de puños de goma suave antideslizante. Unidad: PA.',42.25,65.00,58.50,30,6,1,1),
('3981000','3981000','RAYO R-12','[Ruedas] Rayo corto galvanizado para bicicleta R12. Unidad: PZA.',5.20,8.00,7.20,150,30,2,1),
('RAY-26','RAY-26','RAYO R26 12G','[Ruedas] Rayo reforzado grueso para rodada 26 de montaña Unidad: PZA.',6.50,10.00,9.00,300,60,2,1),
('MREL-DEST','MREL-DEST','RELEVADOR DESTELLADOR','[Eléctrico] Relevador electrónico para intermitentes de moto. Unidad: PZA.',61.75,95.00,85.50,12,3,3,1),
('ROL-FOR','ROL-FOR','ROLLO FORRO CABLE','[Cables] 20 metros de funda con interior de teflón. Unidad: ROL.',617.50,950.00,855.00,3,1,1,1),
('RUED-LAT','RUED-LAT','RUEDA LATERAL','[Ruedas] Par de ruedas de apoyo con soportes largos. Unidad: JG.',143.00,220.00,198.00,12,3,2,1),
('SALP-F150A','SALP-F150A','SALPICADERA AZUL','[Plásticos] Guardafangos original para Italika F-150 azul. Unidad: PZA.',351.00,540.00,486.00,3,1,3,1),
('SALP-FT150','SALP-FT150','SALPICADERA NEGRA','[Plásticos] Guardafangos delantero reforzado negro. Unidad: PZA.',364.00,560.00,504.00,3,1,3,1),
('SALP-F150R','SALP-F150R','SALPICADERA ROJA','[Plásticos] Guardafangos original para Italika F-150 rojo. Unidad: PZA.',351.00,540.00,486.00,3,1,3,1),
('SALP-FT125A','SALP-FT125A','SALPICADERA AZUL','[Plásticos] Repuesto plástico para salpicadera de FT125. Unidad: PZA.',256.75,395.00,355.50,6,2,3,1),
('SALP-FT125P','SALP-FT125P','SALPICADERA PLATA','[Plásticos] Repuesto plástico plateado para salpicadera FT125 Unidad: PZA.',256.75,395.00,355.50,6,2,3,1),
('SALP-NHL-A','SALP-NHL-A','SALPICADERA AZ/BL','[Plásticos] Guardafangos estilo deportivo Azul con Blanco. Unidad: PZA.',299.00,460.00,414.00,4,1,3,1),
('SALP-NHL-R','SALP-NHL-R','SALPICADERA RO/NG','[Plásticos] Guardafangos estilo deportivo Rojo con Negro. Unidad: PZA.',299.00,460.00,414.00,4,1,3,1),
('SPRO-6P','SPRO-6P','SPROCK 6 PASOS','[Transmisión] Piñón de 6 velocidades paso estándar café. Unidad: PZA.',208.00,320.00,288.00,15,3,2,1),
('TAZA-STD','TAZA-STD','TAZA CENTRO STD','[Refacciones] Kit de tazas y baleros para eje central. Unidad: JG.',107.25,165.00,148.50,12,3,2,1),
('31171504','31171504','TAZA DE DIRECCION','[Refacciones] Juego completo de tazas para dirección de bici. Unidad: JG.',126.75,195.00,175.50,15,3,2,1),
('TAZA-MTB','TAZA-MTB','TAZA TELES MTB','[Refacciones] Dirección semi-integrada para cuadros de montaña Unidad: JG.',159.25,245.00,220.50,10,2,2,1),
('1514-1032','1514-1032','TENSOR CADENA','[Moto] Pieza para ajustar la tensión de cadena en motos. Unidad: PZA.',42.25,65.00,58.50,40,8,3,1),
('TIJE-20','TIJE-20','TIJERA R20 MTB','[Suspensión] Horquilla con suspensión básica para niños. Unidad: PZA.',377.00,580.00,522.00,5,1,1,1),
('TIJE-24','TIJE-24','TIJERA R24 MTB','[Suspensión] Horquilla con suspensión para bicicleta R24. Unidad: PZA.',416.00,640.00,576.00,5,1,1,1),
('TIJE-26-N','TIJE-26-N','TIJERA R26 MTB','[Suspensión] Suspensión negra delantera para bicicleta R26. Unidad: PZA.',448.50,690.00,621.00,8,2,1,1),
('TIJE-26-R','TIJE-26-R','TIJERA R26 MTB','[Suspensión] Suspensión roja delantera para bicicleta R26. Unidad: PZA.',448.50,690.00,621.00,8,2,1,1),
('TIJE-29-N','TIJE-29-N','TIJERA SUS R29','[Suspensión] Horquilla Over aluminio con sistema de bloqueo. Unidad: PZA.',1852.50,2850.00,2565.00,3,1,1,1),
('TIJE-29-A','TIJE-29-A','TIJERA SUSP R29','[Suspensión] Suspensión azul bloqueo hidráulico para disco. Unidad: PZA.',1072.50,1650.00,1485.00,3,1,1,1),
('TIJE-29-R','TIJE-29-R','TIJERA SUSP R29','[Suspensión] Suspensión roja bloqueo hidráulico para disco. Unidad: PZA.',1072.50,1650.00,1485.00,3,1,1,1),
('TOPE-4M','TOPE-4M','TOPE FORRO MANDO','[Cables] Terminales de acero para chicotes (Bote 100 pzas) Unidad: BOT.',227.50,350.00,315.00,8,2,1,1),
('TORN-AJU','TORN-AJU','TORNILLO AJUSTADOR','[Frenos] Tornillo fino para ajuste de frenado en manija. Unidad: PZA.',16.25,25.00,22.50,60,12,2,1),
('VALV-FT','VALV-FT','VALVULAS ADM/ESC','[Moto] Kit de válvulas de motor de alta compresión. Unidad: JG.',253.50,390.00,351.00,8,2,3,1),
('ZAPA-BMX','ZAPA-BMX','ZAPATA BMX','[Frenos] Gomas de freno de alta adherencia tipo bellota. Unidad: PA.',81.25,125.00,112.50,25,5,2,1);

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

INSERT INTO rol_privilegios (id_rol, id_privilegio) SELECT 1, id_privilegio FROM privilegios;

INSERT INTO rol_privilegios (id_rol, id_privilegio)
SELECT 2, id_privilegio FROM privilegios
WHERE modulo IN ('VENTAS','INVENTARIO','REPORTES') AND accion NOT IN ('CANCELAR','ELIMINAR_PRODUCTO','UTILIDAD');

INSERT INTO rol_privilegios (id_rol, id_privilegio)
SELECT 3, id_privilegio FROM privilegios
WHERE (modulo='VENTAS' AND accion IN ('ABRIR','REGISTRAR','BUSCAR_MODELO','TICKET_DIGITAL'))
   OR (modulo='REPORTES' AND accion IN ('ABRIR','CORTE_CAJA'));

INSERT INTO lotes (id_producto, id_proveedor, cantidad, observaciones) VALUES
(1,1,15,'Carga inicial de inventario real Cancino Bike'),
(2,1,10,'Carga inicial de inventario real Cancino Bike'),
(3,1,20,'Carga inicial de inventario real Cancino Bike'),
(4,1,8,'Carga inicial de inventario real Cancino Bike'),
(5,2,6,'Carga inicial de inventario real Cancino Bike'),
(6,1,5,'Carga inicial de inventario real Cancino Bike'),
(7,1,12,'Carga inicial de inventario real Cancino Bike'),
(8,1,8,'Carga inicial de inventario real Cancino Bike'),
(9,2,15,'Carga inicial de inventario real Cancino Bike'),
(10,2,15,'Carga inicial de inventario real Cancino Bike'),
(11,1,25,'Carga inicial de inventario real Cancino Bike'),
(12,1,20,'Carga inicial de inventario real Cancino Bike'),
(13,1,45,'Carga inicial de inventario real Cancino Bike'),
(14,1,12,'Carga inicial de inventario real Cancino Bike'),
(15,1,60,'Carga inicial de inventario real Cancino Bike'),
(16,3,10,'Carga inicial de inventario real Cancino Bike'),
(17,1,18,'Carga inicial de inventario real Cancino Bike'),
(18,3,5,'Carga inicial de inventario real Cancino Bike'),
(19,3,8,'Carga inicial de inventario real Cancino Bike'),
(20,2,5,'Carga inicial de inventario real Cancino Bike'),
(21,1,6,'Carga inicial de inventario real Cancino Bike'),
(22,2,12,'Carga inicial de inventario real Cancino Bike'),
(23,1,30,'Carga inicial de inventario real Cancino Bike'),
(24,1,40,'Carga inicial de inventario real Cancino Bike'),
(25,2,15,'Carga inicial de inventario real Cancino Bike'),
(26,3,6,'Carga inicial de inventario real Cancino Bike'),
(27,2,5,'Carga inicial de inventario real Cancino Bike'),
(28,1,18,'Carga inicial de inventario real Cancino Bike'),
(29,1,25,'Carga inicial de inventario real Cancino Bike'),
(30,1,30,'Carga inicial de inventario real Cancino Bike'),
(31,1,25,'Carga inicial de inventario real Cancino Bike'),
(32,1,15,'Carga inicial de inventario real Cancino Bike'),
(33,1,50,'Carga inicial de inventario real Cancino Bike'),
(34,3,8,'Carga inicial de inventario real Cancino Bike'),
(35,3,8,'Carga inicial de inventario real Cancino Bike'),
(36,2,6,'Carga inicial de inventario real Cancino Bike'),
(37,1,10,'Carga inicial de inventario real Cancino Bike'),
(38,1,10,'Carga inicial de inventario real Cancino Bike'),
(39,3,25,'Carga inicial de inventario real Cancino Bike'),
(40,3,3,'Carga inicial de inventario real Cancino Bike'),
(41,3,4,'Carga inicial de inventario real Cancino Bike'),
(42,1,2,'Carga inicial de inventario real Cancino Bike'),
(43,1,1,'Carga inicial de inventario real Cancino Bike'),
(44,1,1,'Carga inicial de inventario real Cancino Bike'),
(45,1,2,'Carga inicial de inventario real Cancino Bike'),
(46,2,15,'Carga inicial de inventario real Cancino Bike'),
(47,2,15,'Carga inicial de inventario real Cancino Bike'),
(48,1,6,'Carga inicial de inventario real Cancino Bike'),
(49,3,8,'Carga inicial de inventario real Cancino Bike'),
(50,2,20,'Carga inicial de inventario real Cancino Bike'),
(51,2,15,'Carga inicial de inventario real Cancino Bike'),
(52,2,12,'Carga inicial de inventario real Cancino Bike'),
(53,2,12,'Carga inicial de inventario real Cancino Bike'),
(54,3,3,'Carga inicial de inventario real Cancino Bike'),
(55,1,8,'Carga inicial de inventario real Cancino Bike'),
(56,1,50,'Carga inicial de inventario real Cancino Bike'),
(57,1,2,'Carga inicial de inventario real Cancino Bike'),
(58,2,10,'Carga inicial de inventario real Cancino Bike'),
(59,3,10,'Carga inicial de inventario real Cancino Bike'),
(60,3,10,'Carga inicial de inventario real Cancino Bike'),
(61,1,20,'Carga inicial de inventario real Cancino Bike'),
(62,1,15,'Carga inicial de inventario real Cancino Bike'),
(63,1,15,'Carga inicial de inventario real Cancino Bike'),
(64,1,10,'Carga inicial de inventario real Cancino Bike'),
(65,1,15,'Carga inicial de inventario real Cancino Bike'),
(66,1,20,'Carga inicial de inventario real Cancino Bike'),
(67,1,20,'Carga inicial de inventario real Cancino Bike'),
(68,1,12,'Carga inicial de inventario real Cancino Bike'),
(69,1,15,'Carga inicial de inventario real Cancino Bike'),
(70,1,10,'Carga inicial de inventario real Cancino Bike'),
(71,1,15,'Carga inicial de inventario real Cancino Bike'),
(72,1,20,'Carga inicial de inventario real Cancino Bike'),
(73,1,20,'Carga inicial de inventario real Cancino Bike'),
(74,1,30,'Carga inicial de inventario real Cancino Bike'),
(75,1,6,'Carga inicial de inventario real Cancino Bike'),
(76,3,5,'Carga inicial de inventario real Cancino Bike'),
(77,2,15,'Carga inicial de inventario real Cancino Bike'),
(78,2,15,'Carga inicial de inventario real Cancino Bike'),
(79,2,4,'Carga inicial de inventario real Cancino Bike'),
(80,2,2,'Carga inicial de inventario real Cancino Bike'),
(81,1,3,'Carga inicial de inventario real Cancino Bike'),
(82,2,2,'Carga inicial de inventario real Cancino Bike'),
(83,2,8,'Carga inicial de inventario real Cancino Bike'),
(84,2,8,'Carga inicial de inventario real Cancino Bike'),
(85,2,20,'Carga inicial de inventario real Cancino Bike'),
(86,2,8,'Carga inicial de inventario real Cancino Bike'),
(87,2,12,'Carga inicial de inventario real Cancino Bike'),
(88,1,15,'Carga inicial de inventario real Cancino Bike'),
(89,1,6,'Carga inicial de inventario real Cancino Bike'),
(90,1,15,'Carga inicial de inventario real Cancino Bike'),
(91,1,15,'Carga inicial de inventario real Cancino Bike'),
(92,1,20,'Carga inicial de inventario real Cancino Bike'),
(93,1,20,'Carga inicial de inventario real Cancino Bike'),
(94,1,10,'Carga inicial de inventario real Cancino Bike'),
(95,2,8,'Carga inicial de inventario real Cancino Bike'),
(96,1,30,'Carga inicial de inventario real Cancino Bike'),
(97,2,150,'Carga inicial de inventario real Cancino Bike'),
(98,2,300,'Carga inicial de inventario real Cancino Bike'),
(99,3,12,'Carga inicial de inventario real Cancino Bike'),
(100,1,3,'Carga inicial de inventario real Cancino Bike'),
(101,2,12,'Carga inicial de inventario real Cancino Bike'),
(102,3,3,'Carga inicial de inventario real Cancino Bike'),
(103,3,3,'Carga inicial de inventario real Cancino Bike'),
(104,3,3,'Carga inicial de inventario real Cancino Bike'),
(105,3,6,'Carga inicial de inventario real Cancino Bike'),
(106,3,6,'Carga inicial de inventario real Cancino Bike'),
(107,3,4,'Carga inicial de inventario real Cancino Bike'),
(108,3,4,'Carga inicial de inventario real Cancino Bike'),
(109,2,15,'Carga inicial de inventario real Cancino Bike'),
(110,2,12,'Carga inicial de inventario real Cancino Bike'),
(111,2,15,'Carga inicial de inventario real Cancino Bike'),
(112,2,10,'Carga inicial de inventario real Cancino Bike'),
(113,3,40,'Carga inicial de inventario real Cancino Bike'),
(114,1,5,'Carga inicial de inventario real Cancino Bike'),
(115,1,5,'Carga inicial de inventario real Cancino Bike'),
(116,1,8,'Carga inicial de inventario real Cancino Bike'),
(117,1,8,'Carga inicial de inventario real Cancino Bike'),
(118,1,3,'Carga inicial de inventario real Cancino Bike'),
(119,1,3,'Carga inicial de inventario real Cancino Bike'),
(120,1,3,'Carga inicial de inventario real Cancino Bike'),
(121,1,8,'Carga inicial de inventario real Cancino Bike'),
(122,2,60,'Carga inicial de inventario real Cancino Bike'),
(123,3,8,'Carga inicial de inventario real Cancino Bike'),
(124,2,25,'Carga inicial de inventario real Cancino Bike');

INSERT INTO ventas (folio, id_usuario, id_cliente, tipo_venta, tipo_precio, subtotal, descuento, total, estado) VALUES
('V-000001',2,1,'CONTADO','MENUDEO',120.00,0.00,120.00,'PAGADA'),
('V-000002',3,2,'CONTADO','MENUDEO',145.00,0.00,145.00,'PAGADA'),
('V-000003',2,3,'CREDITO','MAYOREO',288.00,0.00,288.00,'PENDIENTE');

INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1,1,1,120.00,120.00),
(2,30,1,145.00,145.00),
(3,64,1,288.00,288.00);

INSERT INTO tickets (id_venta, folio_ticket, total) VALUES
(1,'T-000001',120.00),
(2,'T-000002',145.00);

INSERT INTO creditos (id_venta, id_cliente, monto_total, monto_pagado, saldo_pendiente, estado, fecha_limite) VALUES
(3,3,288.00,100.00,188.00,'PENDIENTE', DATE_ADD(CURDATE(), INTERVAL 30 DAY));

INSERT INTO pagos_credito (id_credito, monto) VALUES (1,100.00);

INSERT INTO cortes_caja (id_usuario, total_ventas, total_contado, total_credito, utilidad) VALUES
(3,553.00,265.00,288.00,193.55);

INSERT INTO reportes (id_usuario, tipo_reporte, descripcion) VALUES
(1,'INVENTARIO','Reporte inicial de inventario con productos reales Cancino Bike'),
(1,'VENTAS','Reporte de ventas de prueba'),
(1,'CORTE_CAJA','Corte de caja de demostración');
