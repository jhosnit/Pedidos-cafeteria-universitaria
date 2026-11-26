# Casos de prueba y resultados de ejecución

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Plan de pruebas

**Alcance.** Las 15 historias de usuario del Release 1.0 (HU-01 a HU-15), las reglas de negocio RN-NEG-01 a RN-NEG-07 y los requisitos no funcionales que se pueden comprobar en el ambiente local (RNF-01 a RNF-05 y RNF-07).

**Estrategia.**

1. **Pruebas unitarias automatizadas** con JUnit 5 sobre el modelo y los servicios. Los DAO se reemplazan por versiones en memoria, así las reglas se prueban sin base de datos. Se ejecutan con `mvn test` antes de abrir cada Pull Request.
2. **Pruebas funcionales manuales** sobre la aplicación levantada con `docker compose up --build`, siguiendo los criterios de aceptación de cada historia con las cuentas de demostración.
3. **Regresión:** cuando un caso falla, se analiza la causa, se corrige en un commit `fix:` y se vuelve a ejecutar la suite completa.

**Recursos.** Docker Desktop, imagen `maven:3.9-eclipse-temurin-25` para correr las pruebas sin instalar Maven, navegadores Chrome y Edge, y las cuentas del manual de instalación.

**Calendario.**

| Fecha | Actividad |
|---|---|
| 22/11/2026 | Pruebas unitarias de autenticación y validación de cédula/RUC |
| 23–24/11/2026 | Pruebas de gestión del menú |
| 24–25/11/2026 | Pruebas de selección y confirmación de pedidos; registro y corrección del defecto DEF-01 |
| 25/11/2026 | Pruebas de atención y entrega de pedidos |
| 26/11/2026 | Pruebas funcionales completas en Docker y ejecución final de la suite |

## 2. Pruebas unitarias automatizadas

Archivo de evidencia de la ejecución final: [evidencias/ejecucion-final.txt](evidencias/ejecucion-final.txt).

| ID | Clase de prueba | Qué verifica | Criterios | Casos | Resultado |
|---|---|---|---|---|---|
| CT-U01 | `ValidadorIdentificacionTest` | Cédula válida e inválida, provincia inexistente, RUC de persona natural, sociedad privada y entidad pública | CA-01.4 | 8 | Exitoso |
| CT-U02 | `AuthServiceTest` | Registro con hash BCrypt, campos vacíos, correo no institucional, celular, contraseña corta o distinta, datos repetidos, login correcto e incorrecto, registro de personal | CA-01.1 a CA-01.6, CA-02.4, CA-03.1 | 9 | Exitoso |
| CT-U03 | `ProductoServiceTest` | Registro disponible por defecto, faltan datos, precio inválido, precio con coma, nombre repetido sin mayúsculas, 70 caracteres, edición y cambio de disponibilidad | CA-09.1 a CA-09.5, CA-10.1, CA-10.3, CA-10.4, CA-11.1, CA-11.2 | 10 | Exitoso |
| CT-U04 | `SeleccionTest` | Total = Σ precio × cantidad, selección vacía en 0.00, suma de cantidades, máximo 10, cantidad fuera de rango, producto no disponible, recálculo | CA-05.1 a CA-05.6, CA-06.1 a CA-06.3 | 7 | Fallido al inicio, exitoso tras DEF-01 |
| CT-U05 | `CodigoRetiroTest` | Formato LL-DD en 200 generaciones, normalización de minúsculas y sin guion, rechazo de textos inválidos | CA-07.2, CA-15.4 | 202 | Exitoso |
| CT-U06 | `PedidoServiceTest` | Cantidad no numérica, producto no disponible, confirmación con código y selección vaciada, selección vacía, producto que dejó de estar disponible, precio modificado, misma cifra con otra escala, precio congelado, código repetido, solo mis pedidos | CA-05.3, CA-07.1 a CA-07.8, CA-08.2 | 10 | Exitoso |
| CT-U07 | `EstadoPedidoTest` | Orden Pendiente → En preparación → Listo → Entregado y estado final | RN-NEG-06 | 2 | Exitoso |
| CT-U08 | `AtencionPedidoTest` | Filtro por estado, avance de estados, cambio concurrente, sin saltos, entrega con código en minúsculas, código vacío o inexistente, pedido no listo, ya entregado, entrega concurrente | CA-12.2, CA-13.1 a CA-13.3, CA-14.1 a CA-14.4, CA-15.1 a CA-15.8 | 10 | Exitoso |
| | | | **Total** | **258** | **258 exitosos** |

## 3. Defecto encontrado y corrección

### DEF-01. El total de una selección vacía se mostraba como «0» y no como «0.00»

| Campo | Detalle |
|---|---|
| Caso | CT-U04 · `SeleccionTest.seleccionVaciaTieneTotalCeroConDosDecimales` |
| Criterio | CA-06.3: «Dado que mi selección no tiene productos, entonces el importe es 0.00 y no puedo confirmar». |
| Rama / commit | `feature/pedido-cliente`, commit `test: pruebas de la seleccion de productos` |
| Resultado obtenido | `expected: <0.00> but was: <0>` (1 fallo de 34 pruebas). Evidencia: [evidencias/ejecucion-inicial-seleccion.txt](evidencias/ejecucion-inicial-seleccion.txt) |
| Impacto | Bajo. En pantalla JSTL formatea el número con dos decimales, pero el valor que devuelve el modelo no respeta el formato de importes en USD y otras partes del código (o una futura API) podrían mostrarlo como «0». |

**Análisis.** `Seleccion.getTotal()` empezaba la suma con `BigDecimal.ZERO`, que tiene escala 0. Cuando hay productos, al sumar `precio × cantidad` (escala 2) el resultado toma escala 2 y por eso el resto de pruebas pasaban; pero si la selección está vacía no se suma nada y se devuelve el cero sin decimales. `BigDecimal.equals` compara también la escala, así que `0` y `0.00` no son iguales.

**Solución.** Iniciar el acumulador con `BigDecimal.ZERO.setScale(2)`. Commit `fix: total de la seleccion vacia con dos decimales (CA-06.3)`.

**Reejecución.** Se volvió a ejecutar toda la suite: 34 pruebas, 0 fallos ([evidencias/reejecucion-seleccion.txt](evidencias/reejecucion-seleccion.txt)). En la ejecución final del release el caso sigue pasando.

**Lo que aprendimos.** Al comparar precios en `PedidoService.confirmar` usamos `compareTo` y no `equals`, justamente para que 2.5 y 2.50 se consideren el mismo precio; quedó cubierto por `PedidoServiceTest.elMismoPrecioConOtraEscalaNoCuentaComoCambio`.

## 4. Pruebas funcionales

Ambiente: `docker compose up --build` en Windows 11 con Docker Desktop, navegador Microsoft Edge y Google Chrome, base cargada con `02-datos-iniciales.sql`. Las capturas de cada pantalla están en el [manual de usuario](../08-manual-usuario/manual-usuario.md).

| ID | Historia | Precondición | Pasos | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|---|
| CT-01 | HU-01 | Sin sesión | Registrarse con cédula 1710034066, celular 0991, correo luis@gmail.com y clave «abc» | Lista con los cuatro errores; no se crea la cuenta | Exitoso |
| CT-02 | HU-01 | Sin sesión | Registrarse con datos válidos (cédula 0102030400, correo @epn.edu.ec, clave de 8 caracteres) | Cuenta creada con código CLI-0000N y mensaje en el login | Exitoso |
| CT-03 | HU-02 | Cuenta de cliente | Ingresar con clave incorrecta | «Correo o contraseña incorrectos» sin decir cuál falló | Exitoso |
| CT-04 | HU-02 | Cuentas de demostración | Ingresar como cliente, personal y administrador | Se abre Menú, Panel de pedidos y Gestión de personal respectivamente | Exitoso |
| CT-05 | HU-02 | Sesión de cliente | Escribir en la barra de direcciones `/personal/pedidos` | Pantalla «No tienes acceso» con código 403 | Exitoso |
| CT-06 | HU-02 | Sin sesión | Abrir `/cliente/menu` | Redirección al login | Exitoso |
| CT-07 | HU-02 | Sesión abierta | Pulsar «Salir» y luego el botón atrás del navegador | Vuelve al login; las vistas protegidas piden ingresar otra vez | Exitoso |
| CT-08 | HU-03 | Sesión de administrador | Registrar a Rosa Vela con correo institucional | Aparece en la lista y puede iniciar sesión como personal | Exitoso |
| CT-09 | HU-03 | Sesión de personal | Abrir `/admin/personal` | Acceso denegado (403) | Exitoso |
| CT-10 | HU-04 | Sesión de cliente | Abrir el menú | Nombre, precio en USD y «No disponible» en el jugo de mora sin botón para agregar | Exitoso |
| CT-11 | HU-05 | Menú abierto | Agregar Humita con cantidad 11 | Mensaje «La cantidad debe ser un número entero entre 1 y 10»; selección sin cambios | Exitoso |
| CT-12 | HU-05, HU-06 | Menú abierto | Agregar 2 cafés americanos (1.00) y 1 sánduche (2.50) | Selección con subtotales 2.00 y 2.50, total 4.50 | Exitoso |
| CT-13 | HU-06 | Selección con productos | Cambiar cantidad y quitar un producto | El total se recalcula al instante | Exitoso |
| CT-14 | HU-07 | Selección con productos | Revisar y confirmar | Pedido Pendiente, código con formato LL-DD, indicación de pago presencial y selección vacía | Exitoso |
| CT-15 | HU-07 | Producto en la selección | Desde otra sesión, el personal sube el precio; el cliente confirma | Mensaje con el nuevo total; el pedido se crea solo al volver a confirmar | Exitoso |
| CT-16 | HU-07, HU-10 | Pedido confirmado con sánduche a 2.50 | El personal cambia el precio a 2.80 | El pedido sigue con 2.50 en `detalle_pedido` | Exitoso |
| CT-17 | HU-08 | Cliente con pedidos | Abrir «Mis pedidos» y un detalle | Lista del más reciente al más antiguo; detalle con precios registrados; el pedido listo aparece resaltado | Exitoso |
| CT-18 | HU-09 | Sesión de personal | Registrar «capuchino» con precio 1.555 | Errores de precio y de nombre repetido; no se crea | Exitoso |
| CT-19 | HU-09 | Sesión de personal | Registrar «Chocolate caliente» a 1.25 | Aparece en la lista como disponible y en el menú del cliente | Exitoso |
| CT-20 | HU-11 | Lista de productos | Marcar un producto como no disponible desde la lista | Cambia sin abrir el formulario; en el menú del cliente aparece «No disponible» | Exitoso |
| CT-21 | HU-12 | Pedidos en varios estados | Abrir el panel y usar los filtros | Por defecto solo Pendientes, del más antiguo al más reciente; cada filtro muestra lo suyo | Exitoso |
| CT-22 | HU-13 | Pedido Pendiente | Pulsar «Iniciar preparación» dos veces con la misma pantalla | La primera lo pasa a En preparación; la segunda informa que ya no está Pendiente | Exitoso |
| CT-23 | HU-14 | Pedido En preparación | Pulsar «Marcar como listo» | Pasa a Listo para retirar y el cliente lo ve resaltado | Exitoso |
| CT-24 | HU-15 | Pedido Pendiente | Buscar su código en «Entregar» | Informa que está Pendiente y no permite entregar | Exitoso |
| CT-25 | HU-15 | Sesión de personal | Buscar el código XX-00 | «No se encontró un pedido con el código XX-00» | Exitoso |
| CT-26 | HU-15 | Pedido Listo | Escribir el código en minúsculas sin guion, revisar el resumen y confirmar | Muestra cliente, productos y total; al confirmar queda Entregado | Exitoso |
| CT-27 | HU-15 | Pedido Entregado | Buscar otra vez el mismo código | «El pedido ... ya fue entregado» | Exitoso |
| CT-28 | RNF-01 | Datos registrados | `docker compose down` y `docker compose up` | Cuentas, productos y pedidos siguen ahí | Exitoso |
| CT-29 | RNF-02 | Cuenta creada | Consultar `SELECT contrasena_hash FROM usuario` | Solo hashes que empiezan con `$2a$10$` | Exitoso |
| CT-30 | RNF-07 | Máquina con Docker | `docker compose up --build` en un clon nuevo | Aplicación disponible en http://localhost:8080 | Exitoso |

## 5. Resumen

| Tipo | Casos | Exitosos | Fallidos al inicio | Corregidos |
|---|---|---|---|---|
| Unitarias (métodos de prueba) | 59 (258 ejecuciones con las repeticiones) | 59 | 1 (DEF-01) | 1 |
| Funcionales | 30 | 30 | 0 | — |
