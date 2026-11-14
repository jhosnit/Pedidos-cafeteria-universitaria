# Historias de usuario - Pedidos de la cafetería universitaria

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Actores

| Actor | Descripción |
|-------|------------|
| **Cliente** | Estudiante o docente que consulta el menú, registra pedidos y consulta su estado. Debe crear su propia cuenta para poder pedir. |
| **Personal de cafetería** | Trabajador que mantiene el menú y procesa los pedidos. No se registra por sí mismo, su cuenta la registra el administrador. |
| **Administrador** | Responsable que registra las cuentas del personal. Su cuenta ya existe en el sistema y no se registra desde la aplicación. |

## 2. Épicas e historias

| Épica | Historias |
|-------|-----------|
| **E1. Cuenta y acceso** | HU-01, HU-02, HU-03 |
| **E2. Pedido del cliente** | HU-04, HU-05, HU-06, HU-07, HU-08 |
| **E3. Gestión del menú** | HU-09, HU-10, HU-11 |
| **E4. Atención de pedidos** | HU-12, HU-13, HU-14, HU-15 |

## E1. Cuenta y acceso

### HU-01 — Registrar cuenta de cliente

**Como** cliente, **quiero** crear una cuenta con mis datos, **para** poder hacer y consultar mis pedidos.

**Criterios de aceptación**

* **CA-01.1 — Registro exitoso.** Dado que no tengo cuenta, cuando envío nombre, apellido, una cédula o RUC ecuatoriano válido, celular de 10 dígitos, correo institucional (dominio @epn.edu.ec, por ejemplo nombre.apellido@epn.edu.ec) y una contraseña de al menos 8 caracteres con su confirmación, entonces el sistema crea mi cuenta con un código de cliente único y me lleva al inicio de sesión.
* **CA-01.2 — Contraseña protegida.** Dada una cuenta creada, entonces la contraseña queda almacenada cifrada (hash) y nunca en texto plano.
* **CA-01.3 — Campos obligatorios.** Dado que falta algún campo, cuando envío el formulario, entonces el sistema indica los campos faltantes y no crea la cuenta.
* **CA-01.4 — Formato inválido.** Dado que la cédula o el RUC no supera el algoritmo de validación ecuatoriano, el celular no tiene 10 dígitos numéricos, el correo no tiene formato válido o no pertenece al dominio @epn.edu.ec, o la contraseña tiene menos de 8 caracteres, cuando envío el formulario, entonces el sistema informa el requisito incumplido y no crea la cuenta.
* **CA-01.5 — Contraseñas distintas.** Dado que la contraseña y su confirmación no coinciden, cuando envío el formulario, entonces el sistema lo informa y no crea la cuenta.
* **CA-01.6 — Datos ya registrados.** Dado que ya existe una cuenta con la misma cédula o RUC, o con el mismo correo, cuando envío el formulario, entonces el sistema informa que ya existe una cuenta con ese dato y no crea otra.

### HU-02 — Iniciar sesión

**Como** cliente, personal de cafetería o administrador, **quiero** iniciar sesión con mi correo y contraseña, **para** acceder a las funciones que corresponden a mi rol.

**Criterios de aceptación**

* **CA-02.1 — Ingreso de cliente.** Dado que tengo una cuenta de cliente, cuando ingreso mis credenciales correctas, entonces se abre mi sesión y veo la vista del menú.
* **CA-02.2 — Ingreso de personal.** Dado que tengo una cuenta de personal, cuando ingreso mis credenciales correctas, entonces se abre mi sesión y veo el panel de pedidos.
* **CA-02.3 — Ingreso de administrador.** Dado que soy el administrador, cuando ingreso mis credenciales correctas, entonces se abre mi sesión y veo la vista de gestión de cuentas de personal.
* **CA-02.4 — Credenciales incorrectas.** Dado que el correo no existe o la contraseña no coincide, cuando intento ingresar, entonces el sistema muestra un mensaje genérico («Correo o contraseña incorrectos») sin indicar cuál falló y no abre sesión.
* **CA-02.5 — Campos vacíos.** Dado que dejo el correo o la contraseña vacíos, cuando intento ingresar, entonces el sistema solicita completar ambos campos.
* **CA-02.6 — Acceso restringido.** Dado que no tengo sesión, cuando intento abrir una vista protegida, entonces el sistema me redirige al inicio de sesión. Dado que soy cliente, cuando intento abrir una vista del personal o del administrador, entonces el sistema me niega el acceso. Dado que soy personal, cuando intento abrir la vista del administrador, entonces el sistema me niega el acceso.
* **CA-02.7 — Cierre de sesión.** Dado que tengo sesión activa, cuando elijo cerrar sesión, entonces la sesión termina y debo ingresar de nuevo para acceder a vistas protegidas.

### HU-03 — Registrar cuenta de personal

**Como** administrador, **quiero** registrar la cuenta de un trabajador de la cafetería, **para** que pueda iniciar sesión y operar el menú y los pedidos.

**Criterios de aceptación**

* **CA-03.1 — Registro exitoso.** Dado que tengo sesión de administrador, cuando envío nombre, apellido, correo institucional (dominio @epn.edu.ec) y una contraseña de al menos 8 caracteres con su confirmación, entonces el sistema crea una cuenta con rol Personal y el trabajador puede iniciar sesión con ella.
* **CA-03.2 — Contraseña protegida.** Dada una cuenta creada, entonces la contraseña queda almacenada cifrada (hash) y nunca en texto plano.
* **CA-03.3 — Campos obligatorios.** Dado que falta algún campo, cuando envío el formulario, entonces el sistema indica los campos faltantes y no crea la cuenta.
* **CA-03.4 — Formato inválido.** Dado que el correo no tiene formato válido o no pertenece al dominio @epn.edu.ec, o la contraseña tiene menos de 8 caracteres, cuando envío el formulario, entonces el sistema informa el requisito incumplido y no crea la cuenta.
* **CA-03.5 — Contraseñas distintas.** Dado que la contraseña y su confirmación no coinciden, cuando envío el formulario, entonces el sistema lo informa y no crea la cuenta.
* **CA-03.6 — Correo ya registrado.** Dado que el correo ya pertenece a otra cuenta, cuando envío el formulario, entonces el sistema informa el conflicto y no crea otra cuenta.
* **CA-03.7 — Acceso exclusivo del administrador.** Dado que soy cliente o personal, cuando intento abrir el registro de cuentas de personal, entonces el sistema me niega el acceso.

## E2. Pedido del cliente

### HU-04 — Consultar el menú

**Como** cliente, **quiero** consultar el menú con los productos y sus precios, **para** conocer qué ofrece la cafetería antes de pedir.

**Criterios de aceptación**

* **CA-04.1 — Visualización.** Dado que tengo sesión activa, cuando abro el menú, entonces veo cada producto con su nombre, precio en USD y disponibilidad.
* **CA-04.2 — Producto no disponible.** Dado que un producto está marcado como no disponible, cuando abro el menú, entonces aparece con la etiqueta «No disponible» y sin opción de agregarlo.
* **CA-04.3 — Menú vacío.** Dado que no hay productos registrados, cuando abro el menú, entonces el sistema informa que aún no hay productos.
* **CA-04.4 — Información actualizada.** Dado que el personal cambió un precio o la disponibilidad de un producto, cuando vuelvo a abrir el menú, entonces veo los datos actualizados.

### HU-05 — Seleccionar productos y cantidades

**Como** cliente, **quiero** seleccionar uno o más productos y las cantidades que deseo, **para** armar mi pedido.

**Criterios de aceptación**

* **CA-05.1 — Agregar producto.** Dado que veo un producto disponible, cuando indico una cantidad entera entre 1 y 10 y lo agrego, entonces el producto aparece en mi selección con esa cantidad.
* **CA-05.2 — Varios productos.** Dado que ya tengo un producto en mi selección, cuando agrego otro producto disponible, entonces ambos quedan en la selección.
* **CA-05.3 — Cantidad inválida.** Dado que la cantidad está vacía, es cero, negativa, decimal, no numérica o mayor que 10, cuando intento agregar el producto, entonces el sistema informa que debe ser un entero entre 1 y 10 y la selección no cambia.
* **CA-05.4 — Producto no disponible.** Dado que un producto no está disponible, entonces el sistema no permite agregarlo a la selección.
* **CA-05.5 — Producto repetido.** Dado que un producto ya está en mi selección, cuando lo agrego de nuevo con otra cantidad, entonces el sistema suma la cantidad a la existente. Si la suma supera 10, el sistema informa el máximo permitido y la cantidad no cambia.
* **CA-05.6 — Modificar o quitar.** Dado que tengo productos en mi selección, cuando cambio una cantidad válida (entero entre 1 y 10) o quito un producto, entonces la selección se actualiza.
* **CA-05.7 — Selección temporal.** Dado que armé una selección sin confirmar, entonces no existe ningún pedido registrado hasta que yo confirme.

### HU-06 — Ver el importe total antes de confirmar

**Como** cliente, **quiero** ver el importe total de mi selección antes de confirmar, **para** saber cuánto debo pagar.

**Criterios de aceptación**

* **CA-06.1 — Cálculo del total.** Dada una selección con productos, entonces veo por cada producto su cantidad, precio unitario y subtotal, y un importe total igual a la suma de precio × cantidad. Ejemplo: 2 × 1.50 + 1 × 2.25 = **5.25**.
* **CA-06.2 — Recálculo.** Dado que cambio una cantidad o quito un producto, entonces el importe total se recalcula de inmediato.
* **CA-06.3 — Selección vacía.** Dado que mi selección no tiene productos, entonces el importe es 0.00 y no puedo confirmar.

### HU-07 — Confirmar pedido y obtener código de retiro

**Como** cliente, **quiero** confirmar mi pedido y recibir un código de retiro, **para** poder identificarlo cuando vaya a retirarlo.

**Criterios de aceptación**

* **CA-07.1 — Confirmación exitosa.** Dado que mi selección tiene al menos un producto y todos están disponibles, cuando confirmo el pedido, entonces el sistema crea un pedido asociado a mi cuenta en estado **Pendiente**, con fecha de registro, importe total y código de retiro, y me muestra el resumen y el código en pantalla.
* **CA-07.2 — Código único.** Dado un pedido confirmado, entonces su código tiene 6 caracteres alfanuméricos en mayúsculas, sin 0/O ni 1/I, y no coincide con el de ningún otro pedido.
* **CA-07.3 — Precio congelado.** Dado un pedido confirmado, cuando el personal modifica después el precio de un producto del pedido, entonces el pedido conserva el precio original y su importe total no cambia.
* **CA-07.4 — Producto que dejó de estar disponible.** Dado que un producto de mi selección dejó de estar disponible, cuando confirmo, entonces el sistema informa cuál es y no crea el pedido. Mi selección se conserva.
* **CA-07.5 — Precio modificado.** Dado que el precio de un producto cambió desde que armé la selección, cuando confirmo, entonces el sistema informa el nuevo importe, me pide revisarlo y no crea el pedido hasta que vuelva a confirmar. Mi selección se conserva.
* **CA-07.6 — Sin productos.** Dado que mi selección está vacía, entonces el sistema no permite confirmar.
* **CA-07.7 — Pago presencial.** Dado un pedido confirmado, entonces el resumen indica que el pago se realiza presencialmente, fuera de la aplicación.
* **CA-07.8 — Selección vaciada.** Dado un pedido confirmado, entonces mi selección temporal queda vacía.

### HU-08 — Consultar el estado de mis pedidos

**Como** cliente, **quiero** consultar el estado de mis pedidos, **para** saber cuándo puedo retirarlos.

**Criterios de aceptación**

* **CA-08.1 — Lista de pedidos.** Dado que tengo pedidos, cuando abro «Mis pedidos», entonces veo código, fecha, estado e importe de cada uno, del más reciente al más antiguo.
* **CA-08.2 — Solo mis pedidos.** Dado que existen pedidos de otros clientes, entonces en mi lista solo aparecen los míos.
* **CA-08.3 — Detalle.** Dado que selecciono un pedido, entonces veo sus productos, cantidades, precios registrados, importe total y estado.
* **CA-08.4 — Estado actualizado.** Dado que el personal cambió el estado de mi pedido, cuando vuelvo a consultarlo, entonces veo el estado nuevo.
* **CA-08.5 — Listo para retirar.** Dado un pedido en estado Listo para retirar, entonces aparece resaltado e indica que puedo retirarlo presentando mi código.
* **CA-08.6 — Sin pedidos.** Dado que no tengo pedidos, entonces el sistema informa que aún no he realizado ninguno.

## E3. Gestión del menú

### HU-09 — Registrar producto

**Como** personal de cafetería, **quiero** registrar productos en el menú con su nombre, precio y disponibilidad, **para** que los clientes puedan pedirlos.

**Criterios de aceptación**

* **CA-09.1 — Registro exitoso.** Dado que ingreso un nombre (hasta 70 caracteres) y un precio válidos, cuando guardo el producto, entonces el sistema le asigna un código único, queda marcado como disponible por defecto (puedo marcarlo como no disponible al registrarlo) y aparece en el menú.
* **CA-09.2 — Datos incompletos.** Dado que falta el nombre o el precio, cuando guardo, entonces el sistema indica los campos faltantes y no crea el producto.
* **CA-09.3 — Precio inválido.** Dado que el precio no es numérico, es menor o igual a 0 o tiene más de 2 decimales, cuando guardo, entonces el sistema informa el requisito y no crea el producto.
* **CA-09.4 — Nombre duplicado.** Dado que ya existe un producto con ese nombre (sin distinguir mayúsculas), cuando guardo, entonces el sistema informa el conflicto y no crea el producto.
* **CA-09.5 — Nombre demasiado largo.** Dado que el nombre supera los 70 caracteres, cuando guardo, entonces el sistema informa el máximo permitido y no crea el producto.

### HU-10 — Actualizar nombre y precio de un producto

**Como** personal de cafetería, **quiero** modificar el nombre y el precio de un producto, **para** mantener el menú actualizado.

**Criterios de aceptación**

* **CA-10.1 — Actualización exitosa.** Dado un producto existente, cuando guardo un nombre y un precio válidos, entonces el producto queda actualizado y los nuevos pedidos usan el precio nuevo.
* **CA-10.2 — Pedidos anteriores intactos.** Dado un pedido ya confirmado con ese producto, cuando cambio su precio, entonces el pedido conserva el precio con el que se registró (RN-NEG-04).
* **CA-10.3 — Datos inválidos.** Dado que el nombre está vacío o supera los 70 caracteres, o el precio es inválido (CA-09.3), cuando guardo, entonces el sistema informa el requisito y el producto no cambia.
* **CA-10.4 — Nombre duplicado.** Dado que el nombre pertenece a otro producto, cuando guardo, entonces el sistema informa el conflicto y el producto no cambia.
* **CA-10.5 — Sin eliminación.** Dado cualquier producto, entonces el sistema no ofrece la opción de eliminarlo.

### HU-11 — Cambiar la disponibilidad de un producto

**Como** personal de cafetería, **quiero** marcar un producto como disponible o no disponible, **para** evitar que se pidan productos que no puedo preparar.

**Criterios de aceptación**

* **CA-11.1 — Marcar no disponible.** Dado un producto disponible, cuando lo marco como no disponible, entonces sigue visible en el menú con la etiqueta «No disponible» y no puede incluirse en nuevos pedidos.
* **CA-11.2 — Marcar disponible.** Dado un producto no disponible, cuando lo marco como disponible, entonces los clientes pueden incluirlo en nuevos pedidos.
* **CA-11.3 — Pedidos existentes.** Dado un pedido ya confirmado con un producto que luego se marca como no disponible, entonces el pedido no se ve afectado.
* **CA-11.4 — Cambio rápido.** Dada la lista de productos, entonces puedo cambiar la disponibilidad desde la lista sin abrir el formulario de edición.

## E4. Atención de pedidos

### HU-12 — Consultar pedidos recibidos

**Como** personal de cafetería, **quiero** consultar los pedidos y su estado, **para** saber cuáles debo atender.

**Criterios de aceptación**

* **CA-12.1 — Vista inicial.** Dado que abro el panel de pedidos, entonces veo los pedidos Pendientes, del más antiguo al más reciente, con código, nombre y apellido del cliente, fecha, estado e importe total.
* **CA-12.2 — Filtro por estado.** Dado que elijo un estado (Pendiente, En preparación, Listo para retirar, Entregado) o «Todos», entonces la lista muestra solo los pedidos que corresponden.
* **CA-12.3 — Detalle.** Dado que selecciono un pedido, entonces veo sus productos, cantidades, precios registrados e importe total.
* **CA-12.4 — Sin resultados.** Dado que no hay pedidos para el filtro elegido, entonces el sistema lo informa.

### HU-13 — Iniciar la preparación de un pedido

**Como** personal de cafetería, **quiero** marcar un pedido Pendiente como En preparación, **para** distinguir los pedidos que ya se están atendiendo.

**Criterios de aceptación**

* **CA-13.1 — Cambio de estado.** Dado un pedido Pendiente, cuando elijo «Iniciar preparación», entonces el pedido pasa a **En preparación**.
* **CA-13.2 — Acción no disponible.** Dado un pedido que no está Pendiente, entonces el sistema no ofrece la acción «Iniciar preparación».
* **CA-13.3 — Cambio concurrente.** Dado que otro usuario ya cambió el estado del pedido, cuando intento iniciarlo, entonces el sistema informa que ya no está Pendiente y no realiza cambios.
* **CA-13.4 — Visible para el cliente.** Dado el cambio realizado, cuando el cliente consulta su pedido, entonces ve el estado En preparación.

### HU-14 — Marcar un pedido como listo para retirar

**Como** personal de cafetería, **quiero** marcar un pedido como Listo para retirar cuando termino de prepararlo, **para** que el cliente sepa que puede pasar por él.

**Criterios de aceptación**

* **CA-14.1 — Cambio de estado.** Dado un pedido En preparación, cuando elijo «Marcar como listo», entonces el pedido pasa a **Listo para retirar**.
* **CA-14.2 — Sin saltos.** Dado un pedido Pendiente, entonces el sistema no permite marcarlo como Listo para retirar.
* **CA-14.3 — Sin retrocesos.** Dado un pedido en cualquier estado, entonces el sistema no ofrece volver a un estado anterior.
* **CA-14.4 — Cambio concurrente.** Dado que otro usuario ya cambió el estado del pedido, cuando intento marcarlo, entonces el sistema informa que ya no está En preparación y no realiza cambios.
* **CA-14.5 — Visible para el cliente.** Dado el cambio realizado, cuando el cliente consulta su pedido, entonces ve el estado Listo para retirar.

### HU-15 — Entregar un pedido con el código de retiro

**Como** personal de cafetería, **quiero** registrar un pedido como Entregado solo cuando el cliente presenta su código, **para** evitar confusiones en la entrega.

**Criterios de aceptación**

* **CA-15.1 — Entrega exitosa.** Dado un pedido Listo para retirar que selecciono en la lista, cuando ingreso el código correcto que presenta el cliente, entonces el pedido pasa a **Entregado**.
* **CA-15.2 — Código incorrecto.** Dado un pedido Listo para retirar que selecciono en la lista, cuando ingreso un código que no corresponde a ese pedido, entonces el sistema informa que no coincide y el pedido conserva su estado.
* **CA-15.3 — Pedido no listo.** Dado un pedido Pendiente o En preparación, cuando intento entregarlo (aunque el código sea correcto), entonces el sistema informa su estado actual y no lo entrega.
* **CA-15.4 — Código sin distinguir mayúsculas.** Dado un código correcto escrito en minúsculas, entonces el sistema lo acepta.
* **CA-15.5 — Código vacío.** Dado que no ingreso código, entonces el sistema lo solicita y no entrega el pedido.
* **CA-15.6 — Estado final.** Dado un pedido Entregado, entonces el sistema informa que ya fue entregado y no admite más cambios de estado.