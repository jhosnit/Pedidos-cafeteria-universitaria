# Diagramas de secuencia de diseño (DSD)

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Convenciones

| Elemento | Representación |
|---|---|
| Actor | Participante `actor` |
| Control | Participante con la etiqueta `«control»` |
| Vista (boundary) | Participante con la etiqueta `«boundary»` |
| Entidad (entity) | Participante con la etiqueta `«entity»` |
| `ruteador(): void` | Mensaje del control a sí mismo que decide a qué operación o vista se dirige la petición (como en el ejemplo base). |
| Flecha punteada | Valor de retorno de una consulta. |
| `alt` / `else` | Flujos alternativos y de excepción del caso de uso. La etiqueta indica el número del flujo (por ejemplo, `4.1`) y la condición va entre corchetes. |
| `opt` / `loop` | Flujo opcional o repetitivo. |

## 2. Diagramas

### CU-01 — Registrar cuenta de cliente

```mermaid
sequenceDiagram
    actor A as Cliente
    participant C as «control»<br/>RegistrarCuentaCliente
    participant B1 as «boundary»<br/>RegistroDeCliente
    participant E as «entity»<br/>Cliente
    participant B2 as «boundary»<br/>Login

    A->>C: elegirCrearCuenta(): void
    C->>C: ruteador(): void
    C->>B1: mostrarFormularioRegistro(): void
    A->>C: enviarDatos(nombre:String, apellido:String, cedulaRuc:String, celular:String, correo:String, contrasena:String, confirmacion:String): void
    C->>C: validarDatos(): List#60;String#62;

    alt 4.1, 4.2, 4.3 Datos incompletos, formato inválido o contraseñas distintas [errores no vacío]
        C->>B1: mostrarErrores(errores:List#60;String#62;): void
    else [errores vacío]
        C->>E: existeCedulaRucOCorreo(cedulaRuc:String, correo:String): boolean
        E-->>C: return: datosExisten
        alt 4.4 Datos ya registrados [datosExisten == true]
            C->>B1: mostrarErrorDatosYaRegistrados(): void
        else [datosExisten == false]
            C->>C: generarCodigoCliente(): String
            C->>C: cifrarContrasena(contrasena:String): String
            C->>E: registrarCliente(codigoCliente:String, nombre:String, apellido:String, cedulaRuc:String, celular:String, correo:String, contrasenaCifrada:String): void
            C->>B2: mostrarConfirmacion(mensaje:String): void
        end
    end
```

### CU-02 — Iniciar sesión

```mermaid
sequenceDiagram
    actor A as Usuario
    participant C as «control»<br/>IniciarSesion
    participant B1 as «boundary»<br/>Login
    participant E as «entity»<br/>Usuario
    participant B2 as «boundary»<br/>Menu
    participant B3 as «boundary»<br/>PanelDePedidos
    participant B4 as «boundary»<br/>GestionDeCuentasDePersonal
    participant B5 as «boundary»<br/>AccesoDenegado

    A->>C: ingresar(): void
    C->>C: ruteador(): void
    C->>B1: mostrarFormulario(): void
    A->>C: iniciarSesion(correo:String, contrasena:String): void

    alt 3.1 Campos vacíos [correo o contrasena vacíos]
        C->>B1: mostrarErrorCamposVacios(): void
    else [campos completos]
        C->>E: buscarPorCorreo(correo:String): Usuario
        E-->>C: return: usuario
        C->>C: verificarContrasena(contrasena:String, hash:String): boolean
        alt 4.1 Credenciales incorrectas [usuario == null o contraseña no coincide]
            C->>B1: mostrarErrorCredenciales(): void
        else [credenciales válidas]
            C->>C: abrirSesion(usuario:Usuario): void
            C->>C: ruteador(): void
            alt 6.A [rol == Cliente]
                C->>B2: mostrarMenu(): void
            else 6.B [rol == Personal]
                C->>B3: mostrarPanelDePedidos(): void
            else 6.C [rol == Administrador]
                C->>B4: mostrarGestionDeCuentas(): void
            end
        end
    end

    A->>C: abrirVista(vista:String): void
    C->>C: verificarAcceso(vista:String): String
    alt A.1 Vista protegida sin sesión [sin sesión activa]
        C->>B1: redirigirAlLogin(): void
    else A.2 Vista no permitida para el rol [rol no autorizado]
        C->>B5: mostrarAccesoDenegado(): void
    end
```

### CU-03 — Cerrar sesión

```mermaid
sequenceDiagram
    actor A as Usuario
    participant C as «control»<br/>CerrarSesion
    participant B1 as «boundary»<br/>VistaActual
    participant B2 as «boundary»<br/>Login

    A->>C: cerrarSesion(): void
    C->>C: ruteador(): void
    C->>C: terminarSesion(): boolean

    alt 3 [sesión terminada]
        C->>B2: redirigirAlLogin(): void
    else Fallo [error al terminar la sesión]
        C->>B1: mostrarErrorCierreSesion(): void
    end
```

### CU-04 — Registrar cuenta de personal

```mermaid
sequenceDiagram
    actor A as Administrador
    actor X as Cliente o Personal
    participant C as «control»<br/>RegistrarCuentaPersonal
    participant B1 as «boundary»<br/>GestionDeCuentasDePersonal
    participant B2 as «boundary»<br/>RegistrarPersonal
    participant E as «entity»<br/>PersonalCafeteria
    participant B3 as «boundary»<br/>AccesoDenegado

    A->>C: elegirRegistrarPersonal(): void
    C->>C: ruteador(): void
    C->>B2: mostrarFormulario(): void
    A->>C: enviarDatos(nombre:String, apellido:String, correo:String, contrasena:String, confirmacion:String): void
    C->>C: validarDatos(): List#60;String#62;

    alt 4.1, 4.2, 4.3 Datos incompletos, formato inválido o contraseñas distintas [errores no vacío]
        C->>B2: mostrarErrores(errores:List#60;String#62;): void
    else [errores vacío]
        C->>E: existeCorreo(correo:String): boolean
        E-->>C: return: correoExiste
        alt 4.4 Correo ya registrado [correoExiste == true]
            C->>B2: mostrarErrorCorreoYaRegistrado(): void
        else [correoExiste == false]
            C->>C: cifrarContrasena(contrasena:String): String
            C->>E: registrarPersonal(nombre:String, apellido:String, correo:String, contrasenaCifrada:String): void
            C->>B1: mostrarConfirmacion(mensaje:String): void
        end
    end

    X->>C: abrirRegistroDePersonal(): void
    C->>C: verificarAcceso(rol:String): boolean
    alt A.1 Usuario sin permiso [rol != Administrador]
        C->>B3: mostrarAccesoDenegado(): void
    end
```

### CU-05 — Consultar menú

```mermaid
sequenceDiagram
    actor A as Cliente
    participant C as «control»<br/>ConsultarMenu
    participant E as «entity»<br/>Producto
    participant B as «boundary»<br/>Menu

    A->>C: verMenu(): void
    C->>C: ruteador(): void
    C->>E: getAll(): List#60;Producto#62;
    E-->>C: productos: List#60;Producto#62;

    alt 2.1 Menú sin productos [productos.isEmpty()]
        C->>B: mostrarSinProductos(): void
    else [hay productos]
        C->>B: mostrarMenu(productos:List#60;Producto#62;): void
        opt 3.1 Producto no disponible [producto.disponible == false]
            C->>B: mostrarEtiquetaNoDisponible(producto:Producto): void
        end
    end
```

### CU-06 — Armar selección y revisar importe

```mermaid
sequenceDiagram
    actor A as Cliente
    participant C as «control»<br/>ArmarSeleccion
    participant B1 as «boundary»<br/>Menu
    participant E1 as «entity»<br/>Producto
    participant E2 as «entity»<br/>Seleccion
    participant E3 as «entity»<br/>ItemSeleccion
    participant B2 as «boundary»<br/>ResumenDeSeleccion

    A->>C: agregar(codigoProducto:String): void
    C->>C: ruteador(): void
    C->>E1: estaDisponible(codigoProducto:String): boolean
    E1-->>C: return: disponible

    alt 1.1 Producto no disponible [disponible == false]
        C->>B1: ocultarOpcionAgregar(codigoProducto:String): void
    else [disponible == true]
        C->>B1: solicitarCantidad(): void
        A->>C: indicarCantidad(codigoProducto:String, cantidad:String): void
        C->>C: validarCantidad(cantidad:String): boolean

        alt 4.1 Cantidad inválida [no es entero entre 1 y 10]
            C->>B1: mostrarErrorCantidad(): void
        else [cantidad válida]
            C->>E2: agregarItem(codigoProducto:String, cantidad:int): boolean
            E2-->>C: return: agregado

            alt 5.1 La suma supera el máximo [agregado == false]
                C->>B2: mostrarErrorMaximoSuperado(): void
            else [agregado == true]
                C->>E2: getItems(): List#60;ItemSeleccion#62;
                E2-->>C: items: List#60;ItemSeleccion#62;
                loop Por cada ítem
                    C->>E3: getSubtotal(): BigDecimal
                    E3-->>C: return: subtotal
                end
                C->>E2: getImporteTotal(): BigDecimal
                E2-->>C: return: importeTotal
                C->>B2: mostrarResumen(items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void
            end
        end
    end

    alt 6.1 Modificar la cantidad
        A->>C: modificarCantidad(codigoProducto:String, cantidad:String): void
        C->>C: validarCantidad(cantidad:String): boolean
        C->>E2: modificarCantidad(codigoProducto:String, cantidad:int): void
        C->>B2: mostrarResumen(items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void
    else 6.2 Quitar un producto
        A->>C: quitarProducto(codigoProducto:String): void
        C->>E2: quitarItem(codigoProducto:String): void
        C->>B2: mostrarResumen(items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void
    end
```

### CU-07 — Confirmar pedido

```mermaid
sequenceDiagram
    actor A as Cliente
    participant C as «control»<br/>ConfirmarPedido
    participant E1 as «entity»<br/>Seleccion
    participant B1 as «boundary»<br/>ResumenDeSeleccion
    participant B2 as «boundary»<br/>RevisionFinal
    participant E2 as «entity»<br/>Producto
    participant E3 as «entity»<br/>Pedido
    participant E4 as «entity»<br/>DetallePedido
    participant B3 as «boundary»<br/>PedidoConfirmado

    A->>C: confirmarPedido(): void
    C->>C: ruteador(): void
    C->>E1: getItems(): List#60;ItemSeleccion#62;
    E1-->>C: items: List#60;ItemSeleccion#62;
    C->>B2: mostrarRevisionFinal(items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void

    alt 2.1 El cliente no confirma
        A->>C: volver(): void
        C->>B1: mostrarResumen(items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void
    else 3 El cliente confirma
        A->>C: confirmar(): void
        C->>E2: validarVigencia(items:List#60;ItemSeleccion#62;): ResultadoValidacion
        E2-->>C: return: resultado

        alt 4.3 Selección vacía [items.isEmpty()]
            C->>B1: mostrarErrorSeleccionVacia(): void
        else 4.1 Producto que dejó de estar disponible [resultado.hayNoDisponibles]
            C->>B1: mostrarProductosNoDisponibles(productos:List#60;Producto#62;): void
        else 4.2 Precio modificado [resultado.hayPreciosCambiados]
            C->>B2: mostrarPrecioModificado(nuevoImporte:BigDecimal): void
        else [reglas válidas]
            C->>C: generarCodigoRetiro(): String
            loop 5.1 Código repetido [existeCodigo == true]
                C->>E3: existeCodigo(codigoRetiro:String): boolean
                C->>C: generarCodigoRetiro(): String
            end
            C->>E3: registrarPedido(codigoRetiro:String, codigoCliente:String, importeTotal:BigDecimal): void
            C->>E4: registrarDetalles(codigoRetiro:String, items:List#60;ItemSeleccion#62;): void

            alt 6.1 Error al registrar el pedido [falla al guardar]
                C->>B1: mostrarErrorAlGuardar(): void
            else 7 [pedido registrado]
                C->>B3: mostrarResumenYCodigo(codigoRetiro:String, items:List#60;ItemSeleccion#62;, importeTotal:BigDecimal): void
                C->>E1: vaciar(): void
            end
        end
    end
```

### CU-08 — Consultar mis pedidos

```mermaid
sequenceDiagram
    actor A as Cliente
    participant C as «control»<br/>ConsultarMisPedidos
    participant E1 as «entity»<br/>Pedido
    participant B1 as «boundary»<br/>MisPedidos
    participant E2 as «entity»<br/>DetallePedido
    participant B2 as «boundary»<br/>DetalleDePedidoCliente

    A->>C: misPedidos(): void
    C->>C: ruteador(): void
    C->>E1: getPorCliente(codigoCliente:String): List#60;Pedido#62;
    E1-->>C: pedidos: List#60;Pedido#62;

    alt 2.1 El cliente no tiene pedidos [pedidos.isEmpty()]
        C->>B1: mostrarSinPedidos(): void
    else [hay pedidos]
        C->>B1: mostrarPedidos(pedidos:List#60;Pedido#62;): void
        opt 3.1 Pedido listo para retirar [estado == ListoParaRetirar]
            C->>B1: resaltarPedidoListo(pedido:Pedido): void
        end
        A->>C: seleccionarPedido(codigoRetiro:String): void
        C->>C: ruteador(): void
        C->>E2: getPorPedido(codigoRetiro:String): List#60;DetallePedido#62;
        E2-->>C: detalles: List#60;DetallePedido#62;
        C->>B2: mostrarDetalle(pedido:Pedido, detalles:List#60;DetallePedido#62;): void
    end
```

### CU-09 — Registrar producto

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>RegistrarProducto
    participant B1 as «boundary»<br/>GestionDeProductos
    participant B2 as «boundary»<br/>NuevoProducto
    participant E as «entity»<br/>Producto

    A->>C: elegirAgregarProducto(): void
    C->>C: ruteador(): void
    C->>B2: mostrarFormulario(): void
    A->>C: guardarProducto(nombre:String, precio:String, disponible:boolean): void
    C->>C: validarDatos(): List#60;String#62;

    alt 4.1, 4.2, 4.4 Datos incompletos, precio inválido o nombre muy largo [errores no vacío]
        C->>B2: mostrarErrores(errores:List#60;String#62;): void
    else [errores vacío]
        C->>E: existeNombre(nombre:String): boolean
        E-->>C: return: nombreExiste
        alt 4.3 Nombre duplicado [nombreExiste == true]
            C->>B2: mostrarErrorNombreDuplicado(): void
        else [nombreExiste == false]
            C->>C: generarCodigoProducto(): String
            C->>E: registrarProducto(codigoProducto:String, nombre:String, precio:BigDecimal, disponible:boolean): void
            C->>C: ruteador(): void
            C->>E: getAll(): List#60;Producto#62;
            E-->>C: productos: List#60;Producto#62;
            C->>B1: mostrarProductos(productos:List#60;Producto#62;): void
        end
    end
```

### CU-10 — Actualizar nombre y precio de producto

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>ActualizarProducto
    participant B1 as «boundary»<br/>GestionDeProductos
    participant E as «entity»<br/>Producto
    participant B2 as «boundary»<br/>EditarProducto

    A->>C: editar(codigoProducto:String): void
    C->>C: ruteador(): void
    C->>E: getPorCodigo(codigoProducto:String): Producto
    E-->>C: return: producto
    C->>B2: mostrarFormulario(producto:Producto): void

    alt 3.1 Cancelar la edición
        A->>C: cancelar(): void
        C->>B1: mostrarProductos(productos:List#60;Producto#62;): void
    else 3 Guardar
        A->>C: guardar(codigoProducto:String, nombre:String, precio:String): void
        C->>C: validarDatos(): List#60;String#62;

        alt 4.1 Datos inválidos [errores no vacío]
            C->>B2: mostrarErrores(errores:List#60;String#62;): void
        else [errores vacío]
            C->>E: existeNombreEnOtroProducto(codigoProducto:String, nombre:String): boolean
            E-->>C: return: nombreExiste
            alt 4.2 Nombre duplicado [nombreExiste == true]
                C->>B2: mostrarErrorNombreDuplicado(): void
            else [nombreExiste == false]
                C->>E: actualizarProducto(codigoProducto:String, nombre:String, precio:BigDecimal): void
                C->>C: ruteador(): void
                C->>E: getAll(): List#60;Producto#62;
                E-->>C: productos: List#60;Producto#62;
                C->>B1: mostrarProductos(productos:List#60;Producto#62;): void
            end
        end
    end
```

### CU-11 — Cambiar disponibilidad de producto

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>CambiarDisponibilidad
    participant E as «entity»<br/>Producto
    participant B as «boundary»<br/>GestionDeProductos

    A->>C: cambiarDisponibilidad(codigoProducto:String, disponible:boolean): void
    C->>C: ruteador(): void
    C->>E: actualizarDisponibilidad(codigoProducto:String, disponible:boolean): boolean
    E-->>C: return: actualizado

    alt 2.1 Error al guardar [actualizado == false]
        C->>B: mostrarErrorAlGuardar(): void
    else 3 [actualizado == true]
        C->>E: getAll(): List#60;Producto#62;
        E-->>C: productos: List#60;Producto#62;
        C->>B: mostrarProductos(productos:List#60;Producto#62;): void
    end
```

### CU-12 — Consultar pedidos recibidos

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>ConsultarPedidosRecibidos
    participant E1 as «entity»<br/>Pedido
    participant E3 as «entity»<br/>Cliente
    participant B1 as «boundary»<br/>PanelDePedidos
    participant E2 as «entity»<br/>DetallePedido
    participant B2 as «boundary»<br/>DetalleDePedidoPersonal

    A->>C: abrirPanelDePedidos(): void
    C->>C: ruteador(): void
    C->>E1: getPorEstado(estado:EstadoPedido): List#60;Pedido#62;
    E1-->>C: pedidos: List#60;Pedido#62;

    alt 3.2 Sin resultados [pedidos.isEmpty()]
        C->>B1: mostrarSinPedidos(): void
    else [hay pedidos]
        loop Por cada pedido
            C->>E3: getNombreApellido(codigoCliente:String): String
            E3-->>C: return: nombreCompleto
        end
        C->>B1: mostrarPedidos(pedidos:List#60;Pedido#62;): void
    end

    opt 3.1 Filtrar por estado
        A->>C: filtrarPorEstado(estado:EstadoPedido): void
        C->>E1: getPorEstado(estado:EstadoPedido): List#60;Pedido#62;
        E1-->>C: pedidos: List#60;Pedido#62;
        C->>B1: mostrarPedidos(pedidos:List#60;Pedido#62;): void
    end

    A->>C: seleccionarPedido(codigoRetiro:String): void
    C->>C: ruteador(): void
    C->>E2: getPorPedido(codigoRetiro:String): List#60;DetallePedido#62;
    E2-->>C: detalles: List#60;DetallePedido#62;
    C->>B2: mostrarDetalle(pedido:Pedido, detalles:List#60;DetallePedido#62;): void
```

### CU-13 — Iniciar preparación

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>IniciarPreparacion
    participant E as «entity»<br/>Pedido
    participant B1 as «boundary»<br/>DetalleDePedidoPersonal
    participant B2 as «boundary»<br/>PanelDePedidos

    A->>C: seleccionarPedido(codigoRetiro:String): void
    C->>C: ruteador(): void
    C->>E: getPorCodigo(codigoRetiro:String): Pedido
    E-->>C: return: pedido

    alt 2.1 El pedido no está Pendiente [pedido.estado != Pendiente]
        C->>B1: mostrarDetalle(pedido:Pedido): void
    else [pedido.estado == Pendiente]
        C->>B1: mostrarDetalleConAccion(pedido:Pedido, accion:String): void
        A->>C: iniciarPreparacion(codigoRetiro:String): void
        C->>E: getEstado(codigoRetiro:String): EstadoPedido
        E-->>C: return: estado

        alt 4.1 Estado modificado por otro usuario [estado != Pendiente]
            C->>B2: informarEstadoModificado(): void
        else [estado == Pendiente]
            C->>E: cambiarEstado(codigoRetiro:String, estado:EstadoPedido): void
            C->>B2: mostrarConfirmacionYLista(mensaje:String): void
        end
    end
```

### CU-14 — Marcar como listo para retirar

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>MarcarListo
    participant E as «entity»<br/>Pedido
    participant B1 as «boundary»<br/>DetalleDePedidoPersonal
    participant B2 as «boundary»<br/>PanelDePedidos

    A->>C: seleccionarPedido(codigoRetiro:String): void
    C->>C: ruteador(): void
    C->>E: getPorCodigo(codigoRetiro:String): Pedido
    E-->>C: return: pedido

    alt 2.1 El pedido no está En preparación [pedido.estado != EnPreparacion]
        C->>B1: mostrarDetalle(pedido:Pedido): void
    else [pedido.estado == EnPreparacion]
        C->>B1: mostrarDetalleConAccion(pedido:Pedido, accion:String): void
        A->>C: marcarComoListo(codigoRetiro:String): void
        C->>E: getEstado(codigoRetiro:String): EstadoPedido
        E-->>C: return: estado

        alt 4.1 Estado modificado por otro usuario [estado != EnPreparacion]
            C->>B2: informarEstadoModificado(): void
        else [estado == EnPreparacion]
            C->>E: cambiarEstado(codigoRetiro:String, estado:EstadoPedido): void
            C->>B2: mostrarConfirmacionYLista(mensaje:String): void
        end
    end
```

### CU-15 — Entregar pedido

```mermaid
sequenceDiagram
    actor A as Personal de cafetería
    participant C as «control»<br/>EntregarPedido
    participant B1 as «boundary»<br/>PanelDePedidos
    participant B2 as «boundary»<br/>IngresoDeCodigo
    participant E1 as «entity»<br/>Pedido
    participant E2 as «entity»<br/>DetallePedido
    participant E3 as «entity»<br/>Cliente
    participant B3 as «boundary»<br/>ResumenDeEntrega

    A->>C: entregarPedido(): void
    C->>C: ruteador(): void
    C->>B2: solicitarCodigoRetiro(): void
    A->>C: ingresarCodigo(codigo:String): void

    alt 3.1 Código vacío [codigo.isEmpty()]
        C->>B2: solicitarCodigo(): void
    else [código ingresado]
        C->>C: normalizarCodigo(codigo:String): String
        C->>E1: getPorCodigo(codigoRetiro:String): Pedido
        E1-->>C: return: pedido

        alt 4.1 Código inexistente [pedido == null]
            C->>B2: mostrarCodigoInexistente(): void
        else 4.3 El pedido ya fue entregado [pedido.estado == Entregado]
            C->>B1: informarYaEntregado(): void
        else 4.2 El pedido no está Listo para retirar [pedido.estado != ListoParaRetirar]
            C->>B1: informarEstadoActual(estado:EstadoPedido): void
        else [pedido.estado == ListoParaRetirar]
            C->>E2: getPorPedido(codigoRetiro:String): List#60;DetallePedido#62;
            E2-->>C: detalles: List#60;DetallePedido#62;
            C->>E3: getPorCodigoCliente(codigoCliente:String): Cliente
            E3-->>C: return: cliente
            C->>B3: mostrarResumenEntrega(pedido:Pedido, detalles:List#60;DetallePedido#62;, cliente:Cliente): void

            alt 6.1 El personal cancela
                A->>C: cancelar(): void
                C->>B1: mostrarPanel(): void
            else 6 El personal confirma la entrega
                A->>C: confirmarEntrega(codigoRetiro:String): void
                C->>E1: getEstado(codigoRetiro:String): EstadoPedido
                E1-->>C: return: estado

                alt 7.1 Estado modificado por otro usuario [estado != ListoParaRetirar]
                    C->>B1: informarEstadoModificado(): void
                else 7 [estado == ListoParaRetirar]
                    C->>E1: cambiarEstado(codigoRetiro:String, estado:EstadoPedido): void
                    C->>B1: confirmarEntrega(mensaje:String): void
                end
            end
        end
    end
```