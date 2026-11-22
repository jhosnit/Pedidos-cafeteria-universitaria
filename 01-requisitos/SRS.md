# Especificación de Requisitos de Software (SRS)

**Release:** 1.0  
**Versión del documento:** 1.0

## 1. Introducción

### 1.1 Propósito
Este documento especifica los requisitos del sistema de gestión de pedidos de la cafetería universitaria para el Release 1.0. Está dirigido al equipo de desarrollo, a quien revisa el proyecto y a quien mantenga el sistema.

### 1.2 Alcance
El sistema permite a los **clientes** consultar el menú, armar y confirmar pedidos anticipados, obtener un código de retiro y consultar el estado de sus pedidos. Permite al **personal** mantener el menú y llevar cada pedido desde su recepción hasta la entrega, verificando el código. Un **administrador** registra las cuentas del personal.

**Objetivos de negocio:** reducir las filas en horas de alta demanda y mejorar el seguimiento y la entrega de pedidos.

**Fuera de alcance del Release 1.0**: pago electrónico o cobro en línea, notificaciones por correo o mensajería, integración con impresoras o lectores de códigos, promociones, reembolsos, pedidos programados y gestión de inventario de ingredientes.

### 1.3 Definiciones

| Término | Definición |
|---|---|
| Menú | Conjunto de productos que la cafetería ofrece. |
| Producto disponible | Producto habilitado para incorporarse a nuevos pedidos. |
| Pedido confirmado | Solicitud registrada con código de retiro y estado Pendiente. |
| Código de retiro | Identificador único que permite localizar y entregar un pedido. Formato LL-DD (dos letras, guion, dos dígitos), por ejemplo AB-01. |
| Estado de pedido | Etapa de atención: Pendiente, En preparación, Listo para retirar, Entregado. |
| Selección | Lista temporal de productos y cantidades que el cliente arma antes de confirmar. No es un pedido. |
| HU / CA | Historia de usuario / Criterio de aceptación. |
| CU | Caso de uso. |
| RN, RU, RN-NEG, RNF | Requisito de negocio, de usuario, regla de negocio, requisito no funcional. |
| SUP | Supuesto o decisión del equipo. |

### 1.4 Referencias
- Especificación inicial de necesidades del cliente, versión 1.0 (27 de octubre de 2026).
- `historias-de-usuario.md` (HU-01 a HU-15 con criterios de aceptación).
- `casos-de-uso.md` (CU-01 a CU-15: diagramas, catálogo y descripciones con flujos).
- `requisitos-no-funcionales.md` (RNF-01 a RNF-09, con su categoría).

## 2. Descripción general

### 2.1 Perspectiva del producto
Aplicación web con base de datos relacional, con control de acceso por roles. Los datos persisten entre ejecuciones. Se despliega en contenedores con Docker Compose (RNF-07).

### 2.2 Actores

| Actor | Descripción |
|---|---|
| Cliente | Estudiante o docente. Crea su propia cuenta, consulta el menú, hace pedidos y consulta su estado. |
| Personal de cafetería | Mantiene el menú y procesa los pedidos. Su cuenta la registra el administrador. |
| Administrador | Registra las cuentas del personal. Su cuenta existe desde la instalación. |

### 2.3 Restricciones
- Los datos de productos, clientes y pedidos deben persistir entre ejecuciones.
- Sin pago en línea: el pago se hace presencialmente, fuera de la aplicación.
- El código de retiro puede mostrarse en pantalla.
- El sistema debe permitir demostrar los procesos y reglas de negocio.
- Entrega del Release 1.0: 27 de noviembre de 2026.

### 2.4 Dependencias
- Una base de datos relacional con soporte de restricciones de unicidad y transacciones (RNF-05, RNF-06).
- Docker y Docker Compose para levantar la aplicación y la base de datos (RNF-07).
- Datos iniciales para la demostración (cuentas de administrador, personal y cliente, y productos de ejemplo, al menos uno no disponible), cargados en una instalación nueva con el script `db/init/02-datos-iniciales.sql`. Sus credenciales se documentan en el manual de instalación.

## 3. Requisitos de negocio, de usuario y reglas de negocio

### 3.1 Requisitos de negocio

| ID | Requisito |
|---|---|
| RN-01 | Reducir el tiempo empleado en registrar pedidos en períodos de alta demanda. |
| RN-02 | Facilitar que los clientes conozcan productos y precios antes de pedir. |
| RN-03 | Mejorar el seguimiento de pedidos desde su recepción hasta la entrega. |
| RN-04 | Reducir confusiones al entregar pedidos. |

### 3.2 Requisitos de usuario

| ID | Necesidad |
|---|---|
| RU-01 | Consultar el menú con productos disponibles y precios. |
| RU-02 | Seleccionar uno o más productos y sus cantidades. |
| RU-03 | Conocer el importe total antes de confirmar. |
| RU-04 | Confirmar un pedido y obtener un código de retiro. |
| RU-05 | Consultar el estado del pedido. |
| RU-06 | (Personal) Registrar o actualizar productos, precios y disponibilidad. |
| RU-07 | (Personal) Consultar pedidos y registrar sus avances hasta la entrega. |

### 3.3 Reglas de negocio

| ID | Regla |
|---|---|
| RN-NEG-01 | Un pedido incluye al menos un producto y cada cantidad es un entero positivo. |
| RN-NEG-02 | Solo los productos disponibles pueden incluirse en nuevos pedidos. |
| RN-NEG-03 | El total es la suma de precio vigente × cantidad de cada producto. |
| RN-NEG-04 | El precio registrado en un pedido no cambia si luego se modifica el precio del menú. |
| RN-NEG-05 | Cada pedido confirmado recibe un código de retiro único. |
| RN-NEG-06 | Un pedido inicia en Pendiente y avanza a En preparación, Listo para retirar y Entregado, en ese orden. |
| RN-NEG-07 | Un pedido solo pasa a Entregado si estaba Listo para retirar y el cliente presenta su código. |

## 4. Requisitos funcionales

Cada requisito funcional se detalla en `historias-de-usuario.md` con sus criterios de aceptación y en `casos-de-uso.md` los flujos.

| ID | Requisito funcional | Historia | Caso de uso | Prioridad |
|---|---|---|---|---|
| RF-01 | Registro de cuenta de cliente con validación de datos. | HU-01 | CU-01 | Media |
| RF-02 | Inicio y cierre de sesión con correo y contraseña; acceso restringido por rol. | HU-02 | CU-02, CU-03 | Alta |
| RF-03 | Registro de cuentas de personal por el administrador. | HU-03 | CU-04 | Baja |
| RF-04 | Consulta del menú con nombre, precio y disponibilidad. | HU-04 | CU-05 | Alta |
| RF-05 | Selección de productos y cantidades (agregar, modificar, quitar). | HU-05 | CU-06 | Alta |
| RF-06 | Cálculo y visualización del importe total antes de confirmar. | HU-06 | CU-06 | Alta |
| RF-07 | Confirmación del pedido y generación del código de retiro único. | HU-07 | CU-07 | Alta |
| RF-08 | Consulta de los pedidos propios y su estado. | HU-08 | CU-08 | Alta |
| RF-09 | Registro de productos. | HU-09 | CU-09 | Alta |
| RF-10 | Actualización de nombre y precio de productos. | HU-10 | CU-10 | Media |
| RF-11 | Cambio de disponibilidad de productos. | HU-11 | CU-11 | Alta |
| RF-12 | Consulta de pedidos con filtro por estado. | HU-12 | CU-12 | Alta |
| RF-13 | Cambio de estado Pendiente → En preparación. | HU-13 | CU-13 | Alta |
| RF-14 | Cambio de estado En preparación → Listo para retirar. | HU-14 | CU-14 | Alta |
| RF-15 | Entrega del pedido localizándolo por su código de retiro. | HU-15 | CU-15 | Alta |

## 5. Requisitos no funcionales

El detalle de cada requisito (categoría, origen y verificación) está en `requisitos-no-funcionales.md`.

| ID | Categoría | Requisito | Cómo se verifica |
|---|---|---|---|
| RNF-01 | Fiabilidad | **Persistencia.** Clientes, productos y pedidos se conservan entre ejecuciones mediante la base de datos relacional y volúmenes persistentes. | Registrar datos, reiniciar los contenedores y comprobar que siguen disponibles. |
| RNF-02 | Seguridad | Contraseñas cifradas con hash seguro (BCrypt); nunca en texto plano en la base de datos ni en los registros. | Revisar la tabla de usuarios y los logs. |
| RNF-03 | Seguridad | El control de acceso por rol se aplica en el servidor (filtro HTTP `AuthFilter`), aunque se invoquen URLs directas. | Solicitar directamente una vista restringida sin sesión o con un rol sin permiso: debe responder 403 o redirigir al login. |
| RNF-04 | Integridad | Las validaciones (formato de correo, cédula/RUC, cantidades y reglas de negocio) se aplican en el servidor aunque la interfaz también valide. | Enviar peticiones HTTP con datos inválidos sin pasar por la interfaz. |
| RNF-05 | Integridad de datos | La base de datos garantiza unicidad de código de retiro, correo, cédula/RUC y nombre de producto con restricciones e índices únicos. | Intentar duplicados mediante solicitudes concurrentes. |
| RNF-06 | Integridad transaccional | Un pedido y sus detalles se registran en una única transacción: si algo falla, no queda un pedido parcial. | Simular un fallo en la confirmación y comprobar que no se crea el pedido ni detalles huérfanos. |
| RNF-07 | Portabilidad | **Despliegue en contenedores.** Aplicación y base de datos se ejecutan con Docker Compose (Tomcat 10.1 con Java 25 y PostgreSQL 17), sin dependencias locales adicionales. | Levantar el entorno en una máquina limpia con `docker compose up --build`. |
| RNF-08 | Compatibilidad | La aplicación funciona en versiones actualizadas de Chrome, Firefox y Edge, sin plugins ni extensiones. | Operar el flujo completo en los tres navegadores. |
| RNF-09 | Eficiencia | El servidor responde en menos de 2 segundos en la consulta del menú, el listado de pedidos y las transacciones ordinarias, en red local. | Medir el tiempo de carga de las vistas principales con las herramientas del navegador. |

## 6. Supuestos y decisiones del equipo

| ID | Supuesto / decisión | Motivo | Dónde se refleja |
|---|---|---|---|
| SUP-01 | La cantidad por producto en una selección es un entero entre **1 y 10**. | RN-NEG-01 solo exige un entero positivo; el máximo evita pedidos abusivos o errores de digitación. | CA-05.1, 05.3, 05.5; CU-06, CU-07 |
| SUP-02 | Existen **tres roles** (Cliente, Personal, Administrador) y se accede con **correo y contraseña**. | El cliente nombra solo dos usuarios y no describe autenticación; hace falta identificar al cliente para "consultar sus pedidos". | HU-02, CU-02, RNF-02, RNF-03 |
| SUP-03 | La cuenta del **administrador** ya existe desde la instalación (datos iniciales) y no se registra desde la aplicación. | Evita un registro público de administradores. | Manual de instalación, `db/init/02-datos-iniciales.sql` (2.4) |
| SUP-04 | El personal **no se registra solo**: lo registra el administrador. | Evita que cualquiera obtenga permisos de personal. | HU-03, CU-04 |
| SUP-05 | El correo debe ser **institucional (@epn.edu.ec)**. | El cliente pide "correo institucional". | CA-01.1, CA-01.4, CA-03.1 |
| SUP-06 | Se acepta **cédula o RUC ecuatoriano**, validado con el algoritmo oficial. | El cliente pide cédula; el RUC se admite por ser derivado de ella. | CA-01.1, CA-01.4 |
| SUP-07 | El celular tiene **10 dígitos numéricos**. | Formato de telefonía móvil ecuatoriana. | CA-01.4 |
| SUP-08 | La contraseña tiene **mínimo 8 caracteres**. | Práctica básica de seguridad. | CA-01.1, CA-03.1 |
| SUP-09 | El **código de retiro** tiene el formato **LL-DD**: dos letras mayúsculas (A-Z), un guion y dos dígitos (00-99), por ejemplo AB-01. Se acepta sin distinguir mayúsculas ni el guion. | El cliente solo exige unicidad; un código corto es fácil de dictar y de escribir. Al tener posiciones fijas de letra y de dígito, no hay confusión entre O y 0 ni entre I y 1. | CA-07.2, CA-15.4 |
| SUP-10 | Los productos **no se eliminan**; solo se marcan como no disponibles. | Conserva el historial y evita pedidos con productos inexistentes. | CA-10.5, CA-11.1, CU-10, CU-11 |
| SUP-11 | El nombre de producto tiene máximo **70 caracteres**, es único sin distinguir mayúsculas; el precio es positivo con máximo 2 decimales, en **USD**. | Datos mínimos de integridad. | CA-09.3 a 09.5 |
| SUP-12 | **No existe cancelación** de pedidos. | No está en la especificación. | Fuera de alcance |
| SUP-13 | El **pago es presencial**, fuera de la aplicación. | Lo indica el cliente. | CA-07.7, CU-07 |
| SUP-14 | La **selección** del cliente es temporal y **se mantiene en la sesión**: no es un pedido hasta que se confirma. | Evita pedidos incompletos en la base de datos. | CA-05.7, CA-07.8, CU-06 |
| SUP-15 | Si al confirmar un producto dejó de estar disponible o su precio cambió, **no se crea el pedido** y se informa al cliente; la selección se conserva. | Protege RN-NEG-02 y RN-NEG-03. | CA-07.4, CA-07.5, CU-07 (4.1, 4.2) |
| SUP-16 | Ante un cambio de estado **concurrente**, el sistema rechaza la segunda operación e informa. | Evita estados inconsistentes con varios empleados. | CA-13.3, CA-14.4, CU-13, CU-14 |
| SUP-17 | El cliente **solo ve sus propios pedidos**; el personal ve todos. | Privacidad de los datos. | CA-08.2, CA-12.1 |
| SUP-18 | En la entrega, el personal **ingresa el código que presenta el cliente y el sistema localiza el pedido**; luego revisa el resumen y confirma la entrega. | El glosario del cliente dice que el código "permite localizar y entregar un pedido". | CA-15.1, CA-15.2, CA-15.7, CU-15 |
| SUP-19 | Si el código de retiro generado **ya existe**, el sistema genera otro hasta obtener uno único. | Garantiza RN-NEG-05 sin que el cliente lo note. | CU-07 (5.1), RNF-05 |
| SUP-20 | El sistema **no envía notificaciones**: el cliente consulta el estado cuando lo necesita. | Fuera de alcance según el cliente. | CU-08 |
| SUP-21 | El código LL-DD admite **67.600 combinaciones** (26 × 26 × 100) y es único entre todos los pedidos (RN-NEG-05, RNF-05). Si se agotaran, habría que ampliar el formato. | La capacidad es suficiente para el volumen de una cafetería universitaria en el Release 1.0; se documenta como limitación conocida. | CA-07.2, CU-07 (5.1) |

> Decisiones de diseño pendientes (arquitectura, tecnología, control de concurrencia) se registran como ADR en `02-diseno`.

## 7. Matriz de trazabilidad

### 7.1 Requisitos de negocio → requisitos de usuario → historias → casos de uso

| RN | RU | Historias | Casos de uso |
|---|---|---|---|
| RN-01 | RU-01 a RU-04 | HU-04, HU-05, HU-06, HU-07 | CU-05, CU-06, CU-07 |
| RN-02 | RU-01 | HU-04 | CU-05 |
| RN-03 | RU-05, RU-07 | HU-08, HU-12, HU-13, HU-14, HU-15 | CU-08, CU-12, CU-13, CU-14, CU-15 |
| RN-04 | RU-04, RU-07 | HU-07, HU-15 | CU-07, CU-15 |

### 7.2 Requisitos de usuario → historias → casos de uso

| RU | Historias | Casos de uso |
|---|---|---|
| RU-01 | HU-04 | CU-05 |
| RU-02 | HU-05 | CU-06 |
| RU-03 | HU-06 | CU-06 |
| RU-04 | HU-07 | CU-07 |
| RU-05 | HU-08 | CU-08 |
| RU-06 | HU-09, HU-10, HU-11 | CU-09, CU-10, CU-11 |
| RU-07 | HU-12, HU-13, HU-14, HU-15 | CU-12, CU-13, CU-14, CU-15 |
| (supuestos SUP-02 a SUP-04) | HU-01, HU-02, HU-03 | CU-01, CU-02, CU-03, CU-04 |

### 7.3 Reglas de negocio → criterios de aceptación → casos de uso

| Regla | Criterios de aceptación | Casos de uso |
|---|---|---|
| RN-NEG-01 | CA-05.1, CA-05.3, CA-06.3, CA-07.6 | CU-06, CU-07 |
| RN-NEG-02 | CA-04.2, CA-05.4, CA-07.4, CA-11.1, CA-11.2 | CU-05, CU-06, CU-07, CU-11 |
| RN-NEG-03 | CA-06.1, CA-06.2, CA-07.5 | CU-06, CU-07 |
| RN-NEG-04 | CA-07.3, CA-10.2 | CU-07, CU-10 |
| RN-NEG-05 | CA-07.2 | CU-07 |
| RN-NEG-06 | CA-13.1, CA-13.2, CA-14.1, CA-14.2, CA-14.3 | CU-13, CU-14 |
| RN-NEG-07 | CA-15.1, CA-15.2, CA-15.3, CA-15.6 | CU-15 |

### 7.4 Requisitos no funcionales → historias y casos de uso

| RNF | Historias | Casos de uso |
|---|---|---|
| RNF-01 | Todas | Todos |
| RNF-02 | HU-01, HU-02, HU-03 | CU-01, CU-02, CU-04 |
| RNF-03 | HU-02, HU-03, HU-09 a HU-15 | CU-02 (A.1, A.2), CU-04 (A.1), CU-05 a CU-15 |
| RNF-04 | HU-01, HU-03, HU-05, HU-07, HU-09, HU-10, HU-15 | CU-01, CU-04, CU-06, CU-07, CU-09, CU-10, CU-15 |
| RNF-05 | HU-01, HU-03, HU-07, HU-09, HU-10 | CU-01, CU-04, CU-07, CU-09, CU-10 |
| RNF-06 | HU-07 | CU-07 (6.1) |
| RNF-07 | Todas (despliegue) | Todos |
| RNF-08 | Todas | Todos |
| RNF-09 | HU-04, HU-07, HU-08, HU-12 | CU-05, CU-07, CU-08, CU-12 |
| Datos iniciales (2.4) | HU-02, HU-04 | CU-02, CU-05 |

### 7.5 Historias → casos de uso → commits, diseño, pruebas y evidencia en GitHub
 
Esta sección se completó revisando el historial del repositorio `jhosnit/Pedidos-cafeteria-universitaria` el 21/11/2026. La columna **Commits** sale del historial de la rama `documentacion`; **Componentes de diseño** sale de `modelo-de-dominio.md`. Las celdas **Pendiente** aún no tienen evidencia y se llenan a medida que avanza el proyecto.
 
**Estado del repositorio (21/11/2026)**
 
| Rama | Commits | Contenido |
|---|---|---|
| `main` | 1 | Solo `README.md`. Aún no hay código. |
| `dev` | 1 | Igual que `main`. |
| `documentacion` | 10 | Requisitos (historias de usuario, casos de uso y RNF). Los demás documentos (diseño, ambiente, documentación técnica, análisis estático, pruebas y manuales) existen pero están vacíos. |

**Registro de commits**
 
| Commit | Fecha | Mensaje | Qué cambia |
|---|---|---|---|
| `e61ded5` | 09/11/2026 | agrega README inicial del proyecto | `README.md` (en `main`, `dev` y `documentacion`) |
| `8314121` | 10/11/2026 | crea estructura de carpetas de la documentacion | Las 8 carpetas de entregables, con archivos vacíos |
| `7c96b6c` | 13/11/2026 | agregar historias de usuario | HU-01 a HU-15 |
| `904cee1` | 13/11/2026 | agregar requisitos no funcionales | Requisitos no funcionales |
| `39bc72e` | 13/11/2026 | eliminar casos de uso | Retira el archivo vacío de casos de uso |
| `239c0af` | 21/11/2026 | corregir historia de usuario 15 | HU-07 y HU-15 (código de retiro y entrega por código) |
| `987e639` | 21/11/2026 | agregar casos de uso a partir de las historias de usuario | CU-01 a CU-15 |
| `3fe74a8` | 21/11/2026 | correguir formato de historias de usuario | Formato general de las historias, sin cambios de contenido por historia |
| `c32292d` | 21/11/2026 | alinear casos de uso con criterios de aceptacion de HU | CU-02, CU-07, CU-12 y CU-15 |
| `e2740d2` | 21/11/2026 | estandarizar requisitos no funcionales | Requisitos no funcionales (RNF-01 a RNF-09) |
 
**Trazabilidad por historia**
 
| Historia | Caso de uso | Commits | Componentes de diseño (modelo de dominio) | Casos de prueba | Issue | Pull Request |
|---|---|---|---|---|---|---|
| HU-01 | CU-01 | HU: 7c96b6c; CU: 987e639 | Usuario, Cliente | Pendiente | Pendiente | Pendiente |
| HU-02 | CU-02, CU-03 | HU: 7c96b6c; CU: 987e639, c32292d | Usuario (rol), Cliente, PersonalCafeteria, Administrador | Pendiente | Pendiente | Pendiente |
| HU-03 | CU-04 | HU: 7c96b6c; CU: 987e639 | Usuario, PersonalCafeteria | Pendiente | Pendiente | Pendiente |
| HU-04 | CU-05 | HU: 7c96b6c; CU: 987e639 | Producto | Pendiente | Pendiente | Pendiente |
| HU-05 | CU-06 | HU: 7c96b6c; CU: 987e639 | Seleccion, ItemSeleccion, Producto | Pendiente | Pendiente | Pendiente |
| HU-06 | CU-06 | HU: 7c96b6c; CU: 987e639 | Seleccion, ItemSeleccion | Pendiente | Pendiente | Pendiente |
| HU-07 | CU-07 | HU: 7c96b6c, 239c0af; CU: 987e639, c32292d | Pedido, DetallePedido, EstadoPedido, Seleccion, Producto | Pendiente | Pendiente | Pendiente |
| HU-08 | CU-08 | HU: 7c96b6c; CU: 987e639 | Pedido, DetallePedido, EstadoPedido, Cliente | Pendiente | Pendiente | Pendiente |
| HU-09 | CU-09 | HU: 7c96b6c; CU: 987e639 | Producto | Pendiente | Pendiente | Pendiente |
| HU-10 | CU-10 | HU: 7c96b6c; CU: 987e639 | Producto, DetallePedido (precio registrado) | Pendiente | Pendiente | Pendiente |
| HU-11 | CU-11 | HU: 7c96b6c; CU: 987e639 | Producto | Pendiente | Pendiente | Pendiente |
| HU-12 | CU-12 | HU: 7c96b6c; CU: 987e639, c32292d | Pedido, EstadoPedido, Cliente | Pendiente | Pendiente | Pendiente |
| HU-13 | CU-13 | HU: 7c96b6c; CU: 987e639 | Pedido, EstadoPedido | Pendiente | Pendiente | Pendiente |
| HU-14 | CU-14 | HU: 7c96b6c; CU: 987e639 | Pedido, EstadoPedido | Pendiente | Pendiente | Pendiente |
| HU-15 | CU-15 | HU: 7c96b6c, 239c0af; CU: 987e639, c32292d | Pedido, EstadoPedido | Pendiente | Pendiente | Pendiente |

## 8. Historial del documento

| Versión | Fecha | Cambios |
|---|---|---|
| 1.0 | 21/11/2026 | Versión inicial del SRS. |
 