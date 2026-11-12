-- DATOS OPCIONALES DE DEMOSTRACION.
-- No pertenecen al script original del profesor ni representan ingresos reales.
USE jardineria;

INSERT INTO producto (codigo_producto,nombre,gama,dimensiones,proveedor,descripcion,cantidad_en_stock,precio_venta,precio_proveedor)
VALUES
('DEMO-001','Kit de herramientas basico','Herramientas','30x20 cm','Proveedor Demo','Registro exclusivo para demostracion',8,45.90,25.00),
('DEMO-002','Lavanda de demostracion','AromÃ¡ticas','Maceta 15 cm','Vivero Demo','Registro exclusivo para demostracion',40,12.50,6.00),
('DEMO-003','Rosal ornamental demo','Ornamentales','Maceta 25 cm','Vivero Demo','Registro exclusivo para demostracion',3,28.00,14.00);

INSERT INTO detalle_pedido (codigo_pedido,codigo_producto,cantidad,precio_unidad,numero_linea)
VALUES
(1,'DEMO-001',2,45.90,1),
(2,'DEMO-002',5,12.50,1);

INSERT INTO pago (codigo_cliente,forma_pago,id_transaccion,fecha_pago,total)
VALUES
(5,'Transferencia','DEMO-TX-001','2009-01-20',91.80),
(5,'Tarjeta','DEMO-TX-002','2009-01-21',62.50);
