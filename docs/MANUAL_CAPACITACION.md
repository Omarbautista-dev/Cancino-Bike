# Manual de Capacitacion - SIG-CB Cancino Bike

## 1. Presentacion

Este manual de capacitacion esta dirigido a los usuarios que operaran el sistema SIG-CB de la Refaccionaria Cancino Bike. El sistema fue desarrollado en JavaFX y utiliza una base de datos MySQL llamada `sig_cb`.

El objetivo principal del sistema es apoyar las operaciones diarias de la refaccionaria:

- Controlar el acceso de usuarios.
- Registrar ventas de contado y a credito.
- Consultar y administrar productos del inventario.
- Administrar proveedores y ordenes de compra.
- Consultar reportes de ventas, inventario, utilidad y corte de caja.
- Administrar usuarios, privilegios y accesos.

## 2. Alcance del manual

Este documento explica el uso operativo del sistema de acuerdo con el codigo actual del proyecto. No es un manual tecnico de programacion, aunque incluye notas basicas de instalacion y configuracion para poder ejecutar el sistema durante la capacitacion.

El manual cubre estos modulos:

- Login.
- Dashboard.
- Ventas.
- Inventario.
- Proveedores.
- Reportes.
- Seguridad.

## 3. Requisitos previos

Antes de iniciar la capacitacion, el equipo debe contar con:

- Java 21 instalado.
- MySQL Server activo.
- Maven instalado, si se ejecuta desde codigo fuente.
- Base de datos `sig_cb` creada con el script `src/main/java/org/example/database/sig_cb.sql`.
- Usuario de base de datos configurado en `ConexionBD.java`:
  - Host: `localhost`.
  - Puerto: `3306`.
  - Base de datos: `sig_cb`.
  - Usuario: `sigcb_app`.
  - Password: `SigCB_2026*`.

El proyecto tambien incluye un instalador en `installer/CancinoBike-1.0.exe`, que puede usarse para una demostracion en Windows si el entorno de base de datos ya esta preparado.

## 4. Usuarios iniciales para practica

El script de base de datos crea tres usuarios iniciales:

| Usuario | Password | Rol |
| --- | --- | --- |
| `admin` | `12345` | ADMIN |
| `vendedor` | `12345` | VENDEDOR |
| `cajero` | `12345` | CAJERO |

Durante la capacitacion se recomienda iniciar con `admin`, porque tiene todos los privilegios registrados en la base de datos.

Nota importante: en el codigo actual las contrasenas se validan directamente contra la tabla `usuarios`. Para un entorno productivo se recomienda implementar cifrado/hash de contrasenas.

## 5. Roles y responsabilidades

### Administrador

Responsable de:

- Crear y actualizar usuarios.
- Activar o desactivar cuentas.
- Asignar o quitar privilegios por rol.
- Revisar historial de accesos.
- Consultar reportes generales.
- Supervisar inventario, proveedores y ventas.

### Vendedor

Responsable de:

- Buscar productos.
- Registrar ventas.
- Consultar inventario.
- Revisar reportes permitidos.

### Cajero

Responsable de:

- Registrar ventas de contado.
- Generar ventas que crean ticket digital.
- Consultar corte de caja.

## 6. Flujo general del sistema

1. El usuario inicia sesion desde la pantalla de Login.
2. El sistema valida usuario, password y estado activo.
3. Si el acceso es correcto, se registra en el historial de accesos con estado `CORRECTO`.
4. Si el acceso falla y el usuario existe, se registra como `FALLIDO`.
5. El sistema abre el Dashboard.
6. Desde el Dashboard se accede a Ventas, Inventario, Proveedores, Reportes o Seguridad.

## 7. Modulo Login

### Objetivo

Permitir que solo usuarios registrados y activos entren al sistema.

### Procedimiento

1. Abrir el sistema.
2. Escribir el usuario en el campo `Usuario`.
3. Escribir la contrasena en el campo `Contrasena`.
4. Presionar `Iniciar Sesion`.

### Validaciones

- Si usuario o contrasena estan vacios, el sistema solicita completar ambos campos.
- Si los datos son incorrectos, el sistema muestra acceso denegado.
- Si los datos son correctos, el sistema abre el Dashboard.

### Practica sugerida

- Iniciar sesion con `admin / 12345`.
- Intentar una contrasena incorrecta y revisar posteriormente el historial en Seguridad > Accesos.

## 8. Modulo Dashboard

### Objetivo

Servir como pantalla principal de navegacion.

### Opciones disponibles

- `Ventas`: abre el modulo de ventas.
- `Inventario`: abre el modulo de productos e inventario.
- `Proveedores`: abre el modulo de proveedores, historial y ordenes de compra.
- `Reportes`: abre reportes y corte de caja.
- `Seguridad`: abre administracion de usuarios, privilegios, accesos y cambio de password.
- `Salir`: cierra la sesion y regresa al Login.

### Practica sugerida

Entrar a cada modulo y regresar al Dashboard para identificar la navegacion principal.

## 9. Modulo Ventas

### Objetivo

Registrar ventas de productos disponibles en inventario, descontar stock automaticamente, generar un ticket digital y, si aplica, crear un credito.

### Elementos principales

- Buscador de producto por codigo, modelo o nombre.
- Tabla de productos disponibles.
- Campo `Cantidad`.
- Boton `Agregar al carrito`.
- Carrito de venta.
- Tipo de precio: `MENUDEO` o `MAYOREO`.
- Tipo de venta: `CONTADO` o `CREDITO`.
- Cliente y fecha limite, visibles solo cuando la venta es a credito.
- Campo de descuento.
- Totales: subtotal, descuento y total.
- Boton `REGISTRAR VENTA`.

### Registrar una venta de contado

1. Entrar al modulo `Ventas`.
2. Buscar el producto por codigo, modelo o nombre.
3. Seleccionar el producto en la tabla.
4. Escribir la cantidad.
5. Elegir tipo de precio: `MENUDEO` o `MAYOREO`.
6. Presionar `Agregar al carrito`.
7. Repetir el proceso si se agregaran mas productos.
8. Verificar subtotal, descuento y total.
9. Seleccionar tipo de venta `CONTADO`.
10. Presionar `REGISTRAR VENTA`.
11. Confirmar la venta.

Al guardar, el sistema:

- Registra la venta en la tabla `ventas`.
- Registra cada producto en `detalle_ventas`.
- Descuenta el stock en `productos`.
- Genera un ticket en la tabla `tickets`.
- Marca la venta como `PAGADA`.

### Registrar una venta a credito

1. Agregar productos al carrito.
2. Seleccionar tipo de venta `CREDITO`.
3. Seleccionar el cliente.
4. Seleccionar la fecha limite de pago.
5. Verificar el total.
6. Presionar `REGISTRAR VENTA`.
7. Confirmar la venta.

Al guardar, el sistema:

- Registra la venta como `PENDIENTE`.
- Descuenta el stock.
- Genera ticket digital.
- Crea un registro en la tabla `creditos` con saldo pendiente igual al total.

### Quitar o modificar productos del carrito

- Para quitar un producto, seleccionarlo en el carrito y presionar `Quitar producto`.
- Tambien se puede quitar con doble clic sobre el producto del carrito.
- Para cambiar cantidad, editar la cantidad directamente en la columna `Cant.` del carrito.
- Para limpiar toda la venta, presionar `Vaciar carrito` o `Cancelar`.

### Validaciones importantes

- La cantidad debe ser numerica y mayor a cero.
- No se permite vender mas unidades que el stock disponible.
- El descuento no puede ser negativo ni mayor al subtotal.
- En venta a credito se requiere cliente y fecha limite.

### Practica sugerida

- Registrar una venta de contado con un producto.
- Registrar una venta a credito con cliente y fecha limite.
- Intentar agregar una cantidad mayor al stock para observar la validacion.

## 10. Modulo Inventario

### Objetivo

Administrar productos, precios, stock y alertas de stock bajo.

### Elementos principales

- Resumen de cantidad de productos.
- Costo total del inventario.
- Buscador por codigo, modelo o nombre.
- Tabla de productos.
- Formulario de datos del producto.
- Botones: `Nuevo`, `Guardar`, `Actualizar`, `Eliminar`.
- Opciones laterales: `Productos`, `Actualizar stock`, `Lotes`, `Ajustes`, `Precios`.

### Consultar productos

1. Entrar a `Inventario`.
2. Revisar la tabla principal.
3. Usar el buscador para filtrar por codigo, modelo o nombre.
4. Observar los productos resaltados: el sistema marca en rojo los productos con stock menor o igual al stock minimo.

### Registrar un producto

1. Presionar `Nuevo`.
2. Capturar:
   - Codigo de barras.
   - Modelo.
   - Nombre del producto.
   - Descripcion.
   - Precio de compra.
   - Precio menudeo.
   - Precio mayoreo.
   - Stock.
   - Stock minimo.
   - Proveedor.
3. Presionar `Guardar`.

### Actualizar datos generales

1. Seleccionar un producto de la tabla.
2. Modificar los campos necesarios.
3. Presionar `Actualizar`.

### Actualizar solo stock

1. Seleccionar un producto.
2. Presionar `Actualizar stock`.
3. Modificar `Stock` o `Stock minimo`.
4. Presionar `Actualizar`.

### Actualizar solo precios

1. Seleccionar un producto.
2. Presionar `Precios`.
3. Modificar precio de compra, menudeo o mayoreo.
4. Presionar `Actualizar`.

### Ajuste de inventario

1. Seleccionar un producto.
2. Presionar `Ajustes`.
3. Escribir el stock real contado fisicamente.
4. Presionar `Actualizar`.

### Eliminar producto

1. Seleccionar un producto.
2. Presionar `Eliminar`.
3. Confirmar la accion.

El sistema realiza una baja logica: cambia el estado del producto a inactivo, no borra fisicamente el registro.

### Validaciones importantes

- El codigo de barras no debe repetirse al registrar un producto nuevo.
- Los precios y stock deben ser numericos.
- Los precios no pueden ser negativos.
- El stock no puede ser negativo.
- Precio menudeo y precio mayoreo no deberian ser menores al precio de compra.
- Debe seleccionarse un proveedor.

### Funcion parcial

La opcion `Lotes` muestra un mensaje indicando que el control de lotes se implementara con la tabla `lotes`. El esquema de base de datos ya contiene la tabla, pero el modulo operativo de lotes aun no esta implementado en el controlador.

### Practica sugerida

- Registrar un producto de prueba.
- Cambiar su stock minimo para provocar alerta de stock bajo.
- Actualizar sus precios.
- Darlo de baja con `Eliminar`.

## 11. Modulo Proveedores

### Objetivo

Administrar proveedores, consultar historial de compras y generar ordenes de compra.

### Elementos principales

- Buscador por empresa, contacto, telefono o RFC.
- Tabla de proveedores.
- Formulario de datos del proveedor.
- Historial de compras por proveedor.
- Panel de orden de compra.

### Registrar proveedor

1. Entrar a `Proveedores`.
2. Presionar `Datos proveedores`.
3. Capturar:
   - Nombre empresa.
   - Contacto.
   - Telefono.
   - Email.
   - RFC.
   - Direccion.
4. Presionar `Guardar`.

### Actualizar proveedor

1. Seleccionar un proveedor en la tabla.
2. Modificar los datos.
3. Presionar `Actualizar`.

### Eliminar proveedor

1. Seleccionar un proveedor.
2. Presionar `Eliminar`.
3. Confirmar.

El sistema realiza una baja logica del proveedor: cambia su estado a inactivo.

### Consultar historial de compras

1. Seleccionar un proveedor.
2. Presionar `Historial compras`.
3. Revisar compras, productos, cantidades, precios, subtotales y total de compra.

### Crear orden de compra

1. Seleccionar un proveedor.
2. Presionar `Orden compra`.
3. Seleccionar un producto.
4. Capturar cantidad.
5. Capturar precio estimado.
6. Presionar `Agregar`.
7. Repetir si se agregaran mas productos.
8. Revisar el total estimado.
9. Presionar `Guardar orden`.

Al guardar, el sistema crea registros en:

- `ordenes_compra`.
- `detalle_orden_compra`.

### Validaciones importantes

- El nombre de empresa es obligatorio.
- El telefono debe tener 10 digitos si se captura.
- El email debe tener formato valido si se captura.
- El RFC debe tener 12 o 13 caracteres si se captura.
- No se permite registrar dos proveedores con el mismo nombre de empresa.
- En ordenes de compra, cantidad y precio deben ser numericos y mayores a cero.

### Practica sugerida

- Registrar un proveedor de prueba.
- Actualizar sus datos.
- Crear una orden de compra con dos productos.
- Revisar el historial de compras de un proveedor existente.

## 12. Modulo Reportes

### Objetivo

Consultar informacion resumida de ventas, inventario, utilidad y corte de caja.

### Elementos principales

- Selector de fecha.
- Tarjetas de resumen:
  - Total ventas.
  - Ventas contado.
  - Ventas credito.
  - Utilidad estimada.
  - Numero de ventas.
  - Valor inventario.
- Area de resumen.
- Botones: `Ventas`, `Inventario`, `Utilidad`, `Corte caja`, `Generar reporte`, `Guardar corte`.

### Generar reporte diario

1. Entrar a `Reportes`.
2. Seleccionar una fecha.
3. Presionar `Generar reporte`.
4. Revisar las tarjetas y el resumen.

El sistema calcula:

- Total de ventas no canceladas.
- Total de ventas de contado.
- Total de ventas a credito.
- Numero de ventas.
- Utilidad estimada.
- Valor actual del inventario.
- Productos con stock bajo.

### Reporte de ventas

1. Seleccionar fecha.
2. Presionar `Ventas`.
3. Revisar el resumen del dia.

### Reporte de inventario

1. Presionar `Inventario`.
2. Revisar valor del inventario y productos con stock bajo.

### Reporte de utilidad

1. Seleccionar fecha.
2. Presionar `Utilidad`.
3. Revisar la utilidad estimada.

La utilidad se calcula con la formula:

```text
(precio_unitario - precio_compra) * cantidad vendida
```

### Guardar corte de caja

1. Seleccionar fecha.
2. Presionar `Generar reporte`.
3. Presionar `Guardar corte`.
4. Confirmar la accion.

El sistema guarda el corte en la tabla `cortes_caja`.

### Practica sugerida

- Registrar una venta.
- Entrar a Reportes y generar el reporte de la fecha actual.
- Guardar el corte de caja.

## 13. Modulo Seguridad

### Objetivo

Administrar usuarios, contrasenas, privilegios y registro de accesos.

### Cambiar password

1. Entrar a `Seguridad`.
2. Presionar `Cambiar Password`.
3. Capturar:
   - Usuario.
   - Password actual.
   - Nuevo password.
   - Confirmacion del nuevo password.
4. Presionar `Cambiar password`.

Validaciones:

- Todos los campos son obligatorios.
- El nuevo password debe tener al menos 4 caracteres.
- El nuevo password y la confirmacion deben coincidir.
- El nuevo password no puede ser igual al actual.
- El usuario y password actual deben ser correctos.

### Administrar usuarios

1. Presionar `Usuarios`.
2. Usar el buscador para localizar por usuario, nombre o rol.
3. Para crear usuario:
   - Presionar `Nuevo`.
   - Capturar usuario, password inicial, nombre completo y rol.
   - Presionar `Guardar usuario`.
4. Para actualizar:
   - Seleccionar usuario.
   - Modificar usuario, nombre, rol o estado.
   - Presionar `Actualizar usuario`.
5. Para activar o desactivar:
   - Seleccionar usuario.
   - Presionar `Activar/Desactivar`.

### Administrar privilegios

1. Presionar `Privilegios`.
2. Seleccionar un rol.
3. Presionar `Cargar privilegios`.
4. Seleccionar un privilegio.
5. Presionar `Asignar privilegio` o `Quitar privilegio`.

Los privilegios disponibles en base de datos incluyen acciones de:

- Ventas.
- Inventario.
- Proveedores.
- Reportes.
- Seguridad.

Nota operativa: el sistema registra y administra privilegios por rol, pero la navegacion visible actual no bloquea botones por privilegio desde el controlador de Dashboard. Para capacitacion, explicar los privilegios como administracion registrada en base de datos y considerar validacion adicional si el sistema pasara a produccion.

### Revisar accesos

1. Presionar `Accesos`.
2. Revisar usuario, nombre completo, fecha y estado.
3. Usar el buscador por usuario, nombre o estado.

### Practica sugerida

- Crear un usuario de prueba.
- Cambiarlo de rol.
- Desactivarlo y volverlo a activar.
- Revisar accesos correctos y fallidos.

## 14. Buenas practicas de operacion

- Verificar los datos antes de guardar ventas, productos o proveedores.
- Usar descuentos solo con autorizacion.
- Revisar stock bajo al iniciar o cerrar el dia.
- Guardar corte de caja al finalizar operaciones.
- No compartir usuarios ni contrasenas.
- Desactivar usuarios que ya no deben operar el sistema.
- Evitar modificar precios sin autorizacion.
- Realizar respaldo de la base de datos antes de cambios importantes.

## 15. Ejercicios de capacitacion

### Ejercicio 1: Acceso y navegacion

Objetivo: conocer el flujo general del sistema.

1. Iniciar sesion con `admin`.
2. Entrar a cada modulo desde el Dashboard.
3. Cerrar sesion.
4. Intentar entrar con una contrasena incorrecta.
5. Revisar el historial de accesos.

### Ejercicio 2: Venta de contado

Objetivo: registrar una venta completa.

1. Entrar a Ventas.
2. Buscar un producto con stock.
3. Agregar una unidad al carrito.
4. Seleccionar `CONTADO`.
5. Registrar venta.
6. Revisar que el stock disminuyo en Inventario.

### Ejercicio 3: Venta a credito

Objetivo: registrar una venta que genere credito.

1. Entrar a Ventas.
2. Agregar un producto al carrito.
3. Seleccionar `CREDITO`.
4. Elegir cliente y fecha limite.
5. Registrar venta.
6. Consultar Reportes para verificar ventas a credito.

### Ejercicio 4: Control de inventario

Objetivo: administrar productos.

1. Crear un producto de prueba.
2. Cambiar su precio de menudeo y mayoreo.
3. Ajustar el stock real.
4. Definir stock minimo igual o mayor al stock para ver alerta.
5. Dar de baja el producto.

### Ejercicio 5: Proveedores y ordenes

Objetivo: generar una orden de compra.

1. Crear un proveedor de prueba.
2. Seleccionarlo.
3. Abrir `Orden compra`.
4. Agregar dos productos con cantidad y precio estimado.
5. Guardar la orden.

### Ejercicio 6: Seguridad

Objetivo: administrar usuarios.

1. Crear un usuario nuevo.
2. Asignarle rol.
3. Cambiar estado a inactivo.
4. Activarlo nuevamente.
5. Cargar privilegios de un rol y revisar sus acciones.

## 16. Evaluacion de aprendizaje

Al terminar la capacitacion, el usuario deberia poder:

- Iniciar y cerrar sesion correctamente.
- Buscar productos.
- Registrar ventas de contado.
- Registrar ventas a credito.
- Interpretar subtotal, descuento y total.
- Identificar productos con stock bajo.
- Registrar, actualizar y dar de baja productos.
- Registrar y actualizar proveedores.
- Crear ordenes de compra.
- Generar reportes y guardar corte de caja.
- Crear usuarios y administrar estados.
- Consultar historial de accesos.

## 17. Checklist para el instructor

Antes de iniciar:

- Confirmar que MySQL esta activo.
- Confirmar que la base `sig_cb` existe.
- Confirmar que el usuario `sigcb_app` puede conectarse.
- Confirmar que el sistema abre la pantalla de Login.
- Confirmar que `admin / 12345` inicia sesion.

Durante la practica:

- Usar datos de prueba claramente identificados.
- Evitar borrar o modificar productos reales si se usara una base compartida.
- Explicar cada mensaje de validacion.
- Revisar el impacto de una venta en el stock.
- Revisar reportes despues de registrar ventas.

Al finalizar:

- Limpiar datos de prueba si es necesario.
- Cambiar contrasenas de prueba si la base se conservara.
- Respaldar la base si se hicieron datos utiles.

## 18. Formas recomendadas de presentar el manual

### Opcion 1: Markdown en el repositorio

Usar este archivo `docs/MANUAL_CAPACITACION.md` como fuente principal.

Ventajas:

- Facil de mantener junto al codigo.
- Compatible con GitHub, GitLab y editores como VS Code.
- Se puede convertir a PDF, HTML o DOCX.

### Opcion 2: WriteBook

WriteBook es una buena opcion si quieres presentar el manual como un libro web navegable. La estructura recomendada seria:

```text
manual-capacitacion/
  README.md
  01-presentacion.md
  02-requisitos.md
  03-login-dashboard.md
  04-ventas.md
  05-inventario.md
  06-proveedores.md
  07-reportes.md
  08-seguridad.md
  09-ejercicios.md
  10-evaluacion.md
```

Ventajas:

- Presentacion limpia tipo libro.
- Navegacion por capitulos.
- Ideal para entregar evidencia academica o capacitacion formal.

Recomendacion: conservar este documento como base y dividirlo en capitulos para WriteBook.

### Opcion 3: PDF

Convertir el Markdown a PDF con herramientas como Pandoc, Typora, Obsidian o VS Code con extension de Markdown PDF.

Ventajas:

- Facil de imprimir.
- Adecuado para entrega escolar.
- No requiere servidor web.

### Opcion 4: Presentacion en diapositivas

Crear una presentacion en PowerPoint, Google Slides o Canva con:

- Objetivo del sistema.
- Roles de usuario.
- Flujo general.
- Capturas de cada modulo.
- Ejercicios practicos.
- Evaluacion final.

Ventajas:

- Mejor para exponer ante grupo.
- Permite explicar el sistema paso a paso.
- Puede combinarse con demostracion en vivo.

### Opcion 5: Manual interactivo con capturas

Crear una version HTML o sitio estatico con capturas de pantalla, secciones desplegables y enlaces internos.

Herramientas posibles:

- MkDocs.
- Docusaurus.
- VitePress.
- GitHub Pages.

Ventajas:

- Facil de navegar.
- Mas visual.
- Puede actualizarse por modulo.

### Opcion 6: Video tutorial

Grabar el uso del sistema por modulos:

- Video 1: Login y Dashboard.
- Video 2: Ventas.
- Video 3: Inventario.
- Video 4: Proveedores.
- Video 5: Reportes.
- Video 6: Seguridad.

Ventajas:

- Muy util para usuarios nuevos.
- Permite repetir la capacitacion sin instructor.
- Puede complementar el manual escrito.

## 19. Recomendacion final de entrega

Para una entrega academica completa, se recomienda presentar tres materiales:

1. Manual escrito en PDF.
2. Version web tipo WriteBook o MkDocs.
3. Diapositivas cortas para exposicion y practica guiada.

La fuente principal debe seguir siendo Markdown para evitar mantener varias versiones distintas del mismo contenido.
