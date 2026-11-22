# Decisiones de arquitectura (ADR)

**Release:** 1.0  
**Versión del documento:** 1.0  

Aquí registramos las decisiones de diseño que tomamos durante el Release 1.0, con el problema que las motivó, la alternativa elegida y lo que implican. Todas están vigentes.

---

## ADR-01. MVC con Servlets y JSP sobre Tomcat

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** Necesitamos una aplicación web sencilla de desplegar, que el equipo conozca por la materia y que permita demostrar las reglas de negocio. No hay requisitos de interfaz en tiempo real ni de aplicación móvil.

**Decisión.** Usar el patrón Modelo-Vista-Controlador con Jakarta Servlet 6 como controladores, JSP con JSTL como vistas y clases Java simples para el modelo y los servicios. Las JSP se guardan dentro de `WEB-INF/vistas` para que solo se lleguen a ellas a través de un servlet.

**Consecuencias.** Pocas dependencias y un `.war` liviano. Cada pantalla recarga la página completa; para un menú y un panel de pedidos es suficiente. El estado del pedido no se actualiza solo: el cliente y el personal deben recargar (coherente con SUP-20, sin notificaciones).

---

## ADR-02. JDBC directo en lugar de un ORM

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** El modelo tiene cinco tablas y consultas simples. La parte delicada es registrar el pedido con sus detalles de forma atómica (RNF-06) y cambiar estados sin pisar el trabajo de otro empleado.

**Decisión.** Acceder a PostgreSQL con JDBC y `PreparedStatement` en clases DAO (`UsuarioDao`, `ProductoDao`, `PedidoDao`). Las transacciones se manejan a mano con `setAutoCommit(false)`, `commit()` y `rollback()`.

**Consecuencias.** Se ve exactamente qué SQL se ejecuta y las consultas parametrizadas evitan inyección SQL. A cambio hay más código repetido para leer cada `ResultSet`. Se abre una conexión por operación (sin pool); para la carga de una cafetería es aceptable y se puede cambiar a un `DataSource` de Tomcat sin tocar los servicios.

---

## ADR-03. La selección del cliente vive en la sesión HTTP

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** El cliente arma su lista antes de confirmar y esa lista todavía no es un pedido (SUP-14, CA-05.7).

**Decisión.** Guardar un objeto `Seleccion` en la sesión. Solo al confirmar se crea el `Pedido` en la base.

**Consecuencias.** No quedan pedidos a medias en la base. Si el cliente cierra sesión o la sesión expira (30 minutos), pierde la selección. Al confirmar se vuelve a consultar cada producto para detectar si dejó de estar disponible o cambió de precio (CA-07.4, CA-07.5).

---

## ADR-04. Cambios de estado con UPDATE condicionado

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** Dos empleados pueden pulsar «Iniciar preparación» sobre el mismo pedido casi al mismo tiempo, y el segundo no debe repetir ni saltar estados (SUP-16, CA-13.3, CA-14.4, CA-15.8).

**Decisión.** Cambiar el estado con `UPDATE pedido SET estado = ? WHERE id = ? AND estado = ?`, donde el último parámetro es el estado que el empleado tenía en pantalla. Si la sentencia afecta cero filas, otro usuario ya lo cambió y se informa sin modificar nada.

**Consecuencias.** No hace falta bloquear filas ni guardar versiones. La regla de orden de estados (RN-NEG-06) queda en `EstadoPedido.siguiente()` y la base solo acepta los cuatro valores válidos por un `CHECK`.

---

## ADR-05. Código de retiro LL-DD aleatorio con unicidad en la base

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** Cada pedido necesita un código único y fácil de dictar (RN-NEG-05, SUP-09).

**Decisión.** Generar dos letras y dos dígitos al azar (`AB-01`) y dejar que la restricción `UNIQUE` de `pedido.codigo_retiro` detecte repetidos. Si el insert falla por esa restricción, se hace rollback y se intenta con otro código, hasta 50 veces (SUP-19). Al buscar, el código se normaliza: se aceptan minúsculas y sin guion (CA-15.4).

**Consecuencias.** La unicidad la garantiza la base incluso con pedidos simultáneos. Hay 67.600 combinaciones (SUP-21); si algún día se agotan habrá que ampliar el formato.

---

## ADR-06. Contraseñas con BCrypt y control de acceso en un filtro

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** RNF-02 pide que las contraseñas nunca estén en texto plano y RNF-03 que los permisos se validen en el servidor aunque se escriba la URL a mano.

**Decisión.** Cifrar con BCrypt (costo 10, librería `at.favre.lib:bcrypt`). Un único `AuthFilter` revisa cada petición: `/cliente/*`, `/personal/*` y `/admin/*` exigen el rol correspondiente; sin sesión se redirige al login y con otro rol se responde 403. En la sesión se guarda el usuario sin el hash.

**Consecuencias.** Las reglas de acceso están en un solo lugar y no dependen de ocultar botones. Al iniciar sesión se crea una sesión nueva para evitar fijación de sesión.

---

## ADR-07. Despliegue con Docker Compose

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** Cada integrante tiene un sistema distinto y no todos tienen Java 25 o Maven instalados (RNF-07).

**Decisión.** Un `Dockerfile` de dos etapas (compila con `maven:3.9-eclipse-temurin-25` y despliega en `tomcat:10.1-jdk25-temurin`) y un `docker-compose.yml` con la base `postgres:17`. Los scripts de `db/init` crean el esquema y los datos de demostración la primera vez.

**Consecuencias.** Basta Docker Desktop para levantar todo con `docker compose up --build`. Los datos persisten en el volumen `dbdata` (RNF-01); para empezar de cero se usa `docker compose down -v`.

---

## ADR-08. Sistema de diseño propio en una sola hoja de estilos

**Fecha:** 22/11/2026 · **Estado:** Aceptada

**Contexto.** Queríamos una interfaz clara para usarla en el celular durante el receso, sin agregar frameworks de JavaScript.

**Decisión.** Elegimos entre seis propuestas visuales la dirección «Cobalto»: fondo gris azulado, azul cobalto como color principal, tipografías Plus Jakarta Sans y JetBrains Mono (para precios y códigos), tarjetas con doble borde y botones tipo píldora. Todo está en `assets/app.css` con variables CSS (colores, radios, sombras) y un color por cada estado del pedido.

**Consecuencias.** Las vistas JSP solo usan clases de esa hoja. Cambiar un color o un radio se hace en un solo lugar. La página se adapta a pantallas de celular con una sola media query.
