# Manual de usuario

**Release:** 1.0  
**Versión del documento:** 1.0  

La aplicación de la cafetería permite pedir la comida antes del receso y retirarla en el mostrador con un código, sin hacer fila. Este manual está organizado por tipo de usuario: cliente, personal de cafetería y administrador.

Para entrar se abre la dirección que entregue la cafetería (en la instalación local es <http://localhost:8080>). La aplicación funciona en el computador y en el celular.

---

## 1. Para todos

### 1.1 Iniciar sesión

Escribe tu correo institucional y tu contraseña y pulsa **Ingresar**. Según tu cuenta se abre el menú (clientes), el panel de pedidos (personal) o la gestión de personal (administrador).

![Pantalla de inicio de sesión](img/01-login.png)

Si el correo o la contraseña no coinciden aparece «Correo o contraseña incorrectos». Por seguridad no se indica cuál de los dos está mal.

![Credenciales incorrectas](img/02-login-error.png)

### 1.2 Cerrar sesión

Pulsa **Salir** en la barra superior. Para volver a entrar tendrás que escribir tus datos otra vez.

### 1.3 Páginas sin permiso

Si abres una dirección que no corresponde a tu cuenta (por ejemplo, un cliente que intenta abrir el panel del personal) aparece un aviso y el botón **Ir a mi inicio**.

![Acceso denegado](img/18-acceso-denegado.png)

---

## 2. Cliente

### 2.1 Crear una cuenta

En el login pulsa **Crea una aquí** y completa el formulario:

- Nombre y apellido.
- Cédula o RUC ecuatoriano (se comprueba que sea válido).
- Celular de 10 dígitos.
- Correo institucional terminado en `@epn.edu.ec`.
- Contraseña de al menos 8 caracteres, escrita dos veces.

Si algo no está bien, el formulario te dice exactamente qué corregir y conserva lo que ya escribiste.

![Errores en el registro](img/03-registro-errores.png)

Cuando todo está correcto vuelves al login con tu código de cliente.

![Cuenta creada](img/04-registro-exitoso.png)

### 2.2 Ver el menú

Al entrar ves todos los productos con su precio en dólares. Los que no se pueden pedir en este momento aparecen con la etiqueta **No disponible** y sin botón.

![Menú del cliente](img/05-menu.png)

### 2.3 Armar tu selección

1. Escribe la cantidad junto al producto (de 1 a 10) y pulsa **Agregar**.
2. Si agregas un producto que ya estaba, se suma a la cantidad anterior, siempre que no pase de 10.
3. Abajo aparece una barra con cuántos productos llevas y el total.

![Menú con productos en la selección](img/06-menu-con-seleccion.png)

Si la cantidad no es un número entero entre 1 y 10 aparece un aviso y tu selección no cambia.

![Cantidad no válida](img/07-cantidad-invalida.png)

La selección todavía no es un pedido: la cafetería no la ve hasta que la confirmes.

### 2.4 Revisar el total

En **Mi selección** ves el precio, la cantidad y el subtotal de cada producto y el total a pagar. Puedes cambiar una cantidad con **Actualizar** o sacar un producto con **Quitar**; el total se recalcula al momento.

![Mi selección](img/08-seleccion.png)

### 2.5 Confirmar el pedido

Pulsa **Revisar pedido**, comprueba el detalle y pulsa **Confirmar pedido**.

![Revisión final](img/09-revision.png)

Recibirás un **código de retiro** de dos letras y dos números, por ejemplo `CA-38`. Guárdalo o toma una captura: lo necesitas para retirar. El pago se hace en persona en el mostrador.

![Pedido confirmado con código de retiro](img/10-confirmado.png)

Si mientras armabas tu selección algún producto se agotó, la aplicación te avisa cuál es y no crea el pedido; quítalo y vuelve a confirmar. Si cambió algún precio, te muestra el nuevo total para que lo revises y confirmes otra vez.

### 2.6 Consultar tus pedidos

En **Mis pedidos** ves todos tus pedidos, del más reciente al más antiguo, con su código, fecha, estado e importe. Los estados son:

| Estado | Qué significa |
|---|---|
| Pendiente | La cafetería recibió tu pedido. |
| En preparación | Lo están preparando. |
| Listo para retirar | Ya puedes ir al mostrador con tu código. |
| Entregado | Ya lo retiraste. |

![Mis pedidos](img/11-mis-pedidos.png)

Cuando tu pedido está listo aparece resaltado en verde. Recarga la página para ver los cambios.

![Pedido listo para retirar](img/16-mis-pedidos-listo.png)

Al tocar un pedido ves sus productos, cantidades y los precios con que se registró.

![Detalle del pedido](img/17-detalle-cliente-listo.png)

---

## 3. Personal de cafetería

La barra superior tiene tres secciones: **Pedidos**, **Entregar** y **Productos**.

### 3.1 Panel de pedidos

Al entrar ves los pedidos **Pendientes**, del más antiguo al más reciente, para atenderlos en orden de llegada. Con los filtros de arriba puedes ver otro estado o **Todos**.

![Panel de pedidos pendientes](img/12-panel-pendientes.png)

Al tocar el código de un pedido ves su detalle.

![Detalle del pedido para el personal](img/13-detalle-personal.png)

### 3.2 Avanzar un pedido

- En un pedido **Pendiente** pulsa **Iniciar preparación**.
- En un pedido **En preparación** pulsa **Marcar como listo**.

Los pedidos siempre avanzan en ese orden y no se pueden regresar. Si otro compañero ya cambió el estado del mismo pedido, la aplicación te avisa y no hace ningún cambio.

![Pedido en preparación](img/14-iniciada-preparacion.png)

![Pedidos listos para retirar](img/15-panel-listos.png)

### 3.3 Entregar un pedido

1. Pulsa **Entregar** (o **Entregar con código** en el panel).
2. Escribe el código que te muestra el cliente. Puede ir en minúsculas o sin guion: `ca38` funciona igual que `CA-38`.
3. Revisa el resumen: nombre del cliente, productos y total a cobrar.
4. Si todo coincide, pulsa **Confirmar entrega**. Si no, **Cancelar** y el pedido no cambia.

![Resumen antes de entregar](img/20-entrega-resumen.png)

![Pedido entregado](img/21-entregado.png)

Si el código no existe, si el pedido todavía no está listo o si ya fue entregado, la aplicación lo indica y no permite la entrega.

![Código no encontrado](img/19-entrega-no-encontrado.png)

### 3.4 Productos

En **Productos** ves todos los productos con su código, precio y estado.

![Lista de productos](img/22-productos.png)

- **Nuevo producto:** escribe el nombre (hasta 70 caracteres, sin repetir otro) y el precio (mayor que 0, máximo dos decimales). Queda disponible, salvo que marques «Registrar como no disponible».
- **Editar:** cambia nombre o precio. Los pedidos ya confirmados conservan el precio con el que se hicieron.
- **Marcar no disponible / disponible:** cambia la disponibilidad directamente desde la lista. Los productos no se eliminan, así el historial de pedidos queda completo.

![Errores al registrar un producto](img/23-producto-errores.png)

![Producto registrado](img/24-producto-registrado.png)

![Editar producto](img/25-producto-editar.png)

---

## 4. Administrador

Al entrar ves la lista de cuentas del personal de cafetería.

![Cuentas de personal](img/26-admin-personal.png)

Para agregar a un trabajador pulsa **Registrar personal**, completa nombre, apellido, correo institucional y una contraseña de al menos 8 caracteres (dos veces) y pulsa **Guardar**. La persona ya puede iniciar sesión y verá el panel de pedidos.

![Registrar personal](img/27-admin-form.png)

![Personal registrado](img/28-admin-registrado.png)

---

## 5. En el celular

Todas las pantallas se acomodan al ancho del teléfono; la barra de navegación se apila arriba y los productos se muestran uno debajo de otro.

![Menú en el celular](img/29-celular-menu.png)

## 6. Preguntas frecuentes

**¿Puedo pagar en la aplicación?** No. El pago se hace en persona en el mostrador al retirar.

**¿Puedo cancelar un pedido?** No en esta versión. Acércate al mostrador.

**Perdí mi código, ¿qué hago?** Entra a **Mis pedidos**: ahí está el código de cada pedido.

**¿Por qué no puedo pedir más de 10 unidades?** Es el máximo por producto definido por la cafetería para evitar errores al digitar.

**Cerré sesión y se borró lo que había elegido.** La selección se guarda solo mientras tienes la sesión abierta; vuelve a agregar los productos.

**No me llega ningún aviso cuando mi pedido está listo.** La aplicación no envía notificaciones; consulta **Mis pedidos** y recarga la página.
