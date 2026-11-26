# Documentación técnica del código fuente

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Javadoc

Todas las clases públicas y los métodos que tienen alguna regla de negocio están comentados con Javadoc (`@param`, `@return`, `@throws` y `@since`). La documentación HTML generada para el Release 1.0 está en la carpeta [`javadoc/`](javadoc/index.html) de este directorio; para verla se abre `javadoc/index.html` en el navegador.

Para volver a generarla desde la rama `dev` o `main`:

```bash
mvn javadoc:javadoc
```

o, sin Maven instalado:

```bash
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-25 mvn javadoc:javadoc
```

El resultado queda en `target/reports/apidocs/index.html`.

## 2. Organización del código

El paquete raíz es `ec.edu.epn.cafeteria` y sigue las capas descritas en [arquitectura.md](../02-diseño/arquitectura.md).

| Paquete | Responsabilidad | Clases |
|---|---|---|
| `modelo` | Entidades del dominio y la excepción de reglas de negocio. No dependen de la web ni de la base. | `Usuario`, `Cliente`, `Rol`, `Producto`, `Pedido`, `DetallePedido`, `EstadoPedido`, `Seleccion`, `ItemSeleccion`, `ReglaNegocioException` |
| `dao` | Acceso a PostgreSQL con JDBC. Cada método abre y cierra su conexión. | `Conexion`, `UsuarioDao`, `ProductoDao`, `PedidoDao`, `DatosException` |
| `servicio` | Reglas de negocio y validaciones del servidor. Reciben los DAO por constructor para poder probarlos con DAO en memoria. | `AuthService`, `ProductoService`, `PedidoService`, `Validaciones`, `ValidadorIdentificacion`, `CodigoRetiro` |
| `controlador` | Servlets (uno por caso de uso o pantalla) y el filtro de acceso. Leen parámetros, llaman al servicio y eligen la vista. | `AuthFilter`, `LoginServlet`, `LogoutServlet`, `RegistroServlet`, `AdminPersonalServlet`, `MenuServlet`, `SeleccionServlet`, `ConfirmarServlet`, `MisPedidosServlet`, `ProductosServlet`, `PanelPedidosServlet`, `EntregaServlet`, `Web` |

Las vistas están en `src/main/webapp/WEB-INF/vistas/` agrupadas por rol (`cliente/`, `personal/`, `admin/`) y comparten `comun/cabecera.jspf` y `comun/pie.jspf`. Los estilos están en `src/main/webapp/assets/app.css`.

## 3. Rutas de la aplicación

| Ruta | Método | Servlet | Rol | Caso de uso |
|---|---|---|---|---|
| `/login` | GET, POST | `LoginServlet` | Público | CU-02 |
| `/registro` | GET, POST | `RegistroServlet` | Público | CU-01 |
| `/logout` | POST | `LogoutServlet` | Con sesión | CU-03 |
| `/admin/personal` | GET | `AdminPersonalServlet` | Administrador | CU-04 |
| `/admin/personal/nuevo` | GET, POST | `AdminPersonalServlet` | Administrador | CU-04 |
| `/cliente/menu` | GET | `MenuServlet` | Cliente | CU-05 |
| `/cliente/seleccion` | GET, POST (`accion` = agregar, cambiar, quitar) | `SeleccionServlet` | Cliente | CU-06 |
| `/cliente/confirmar` | GET (revisión), POST (confirmar) | `ConfirmarServlet` | Cliente | CU-07 |
| `/cliente/pedidos` | GET (`id` opcional) | `MisPedidosServlet` | Cliente | CU-08 |
| `/personal/productos` | GET, POST (cambia disponibilidad) | `ProductosServlet` | Personal | CU-11 |
| `/personal/productos/form` | GET, POST (`id` opcional) | `ProductosServlet` | Personal | CU-09, CU-10 |
| `/personal/pedidos` | GET (`estado`, `id`), POST (avanzar estado) | `PanelPedidosServlet` | Personal | CU-12, CU-13, CU-14 |
| `/personal/entrega` | GET, POST (`accion` = buscar, confirmar) | `EntregaServlet` | Personal | CU-15 |

Después de cada POST que modifica datos se redirige con un GET (patrón Post/Redirect/Get) para que recargar la página no repita la operación. Los mensajes de éxito o error se pasan con atributos de sesión `flashOk` y `flashError` que la cabecera muestra una sola vez.

## 4. Contratos de los métodos principales

### `PedidoService.confirmar(Seleccion seleccion, int clienteId)`

- **Qué hace:** vuelve a consultar cada producto de la selección; si alguno ya no está disponible o cambió de precio, no crea el pedido. Si todo está bien, crea el pedido en estado Pendiente con un código de retiro único y vacía la selección.
- **Devuelve:** el `Pedido` creado con su código.
- **Lanza:** `ReglaNegocioException` si la selección está vacía (CA-07.6), si un producto dejó de estar disponible (CA-07.4) o si cambió algún precio (CA-07.5). En este último caso la selección queda con los precios nuevos para que el cliente revise y confirme otra vez.

### `PedidoDao.insertar(Pedido pedido)`

- **Qué hace:** inserta el pedido y todos sus detalles en una sola transacción (RNF-06).
- **Devuelve:** `true` si se guardó; `false` si el código de retiro ya existía (violación de `uq_pedido_codigo_retiro`), en cuyo caso el servicio genera otro.
- **Lanza:** `DatosException` ante cualquier otro error de base; antes hace `rollback`.

### `PedidoService.avanzar(int pedidoId, EstadoPedido estadoActual)`

- **Qué hace:** pasa el pedido de Pendiente a En preparación o de En preparación a Listo para retirar.
- **Devuelve:** el nuevo estado.
- **Lanza:** `ReglaNegocioException` si el estado no permite avanzar desde el panel o si otro usuario ya lo cambió (CA-13.3, CA-14.4).

### `PedidoService.buscarParaEntrega(String codigoTexto)` y `entregar(int pedidoId)`

- **Qué hacen:** el primero normaliza el código (`ab01` → `AB-01`) y devuelve el pedido solo si está Listo para retirar; el segundo lo marca como Entregado.
- **Lanzan:** `ReglaNegocioException` si el código está vacío, no existe, el pedido no está listo, ya fue entregado o cambió mientras tanto (CA-15.2 a CA-15.8).

### `Seleccion.agregar(Producto producto, int cantidad)`

- **Qué hace:** agrega el producto o suma la cantidad si ya estaba.
- **Lanza:** `ReglaNegocioException` si el producto no está disponible, si la cantidad no está entre 1 y 10, o si la suma pasa de 10 (CA-05.3 a CA-05.5).

### `ValidadorIdentificacion.esValida(String valor)`

- **Devuelve:** `true` para una cédula de 10 dígitos o un RUC de 13 dígitos válidos según el algoritmo del Registro Civil (módulo 10) y del SRI (módulo 11 para sociedades privadas y públicas).

## 5. Base de datos

El esquema está en `db/init/01-esquema.sql` y los datos de demostración en `db/init/02-datos-iniciales.sql`.

| Tabla | Clave | Restricciones importantes |
|---|---|---|
| `usuario` | `id` | Índice único sobre `LOWER(correo)`; `rol` solo acepta CLIENTE, PERSONAL o ADMINISTRADOR. |
| `cliente` | `usuario_id` | `codigo_cliente` y `cedula_ruc` únicos. |
| `producto` | `id` | Índice único sobre `LOWER(nombre)`; `precio > 0`. |
| `pedido` | `id` | `codigo_retiro` único con formato `^[A-Z]{2}-[0-9]{2}$`; `estado` limitado a los cuatro valores. |
| `detalle_pedido` | `id` | `cantidad` entre 1 y 10; `precio_registrado` guarda el precio del momento (RN-NEG-04). |

## 6. Variables de entorno

| Variable | Uso | Valor por defecto |
|---|---|---|
| `DB_URL` | Cadena JDBC hacia PostgreSQL | `jdbc:postgresql://localhost:5432/cafeteria` |
| `DB_USER` | Usuario de la base | `cafeteria` |
| `DB_PASSWORD` | Clave del usuario de la base | definida en `docker-compose.yml` para el entorno de desarrollo |

## 7. Pruebas automatizadas

Las pruebas unitarias están en `src/test/java` y se ejecutan con `mvn test`. Los servicios se prueban con DAO en memoria (subclases que reemplazan los métodos que tocan la base), así las pruebas no necesitan PostgreSQL. El detalle de los casos y sus resultados está en [06-pruebas](../06-pruebas/pruebas.md).
