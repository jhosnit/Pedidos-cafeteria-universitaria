# Diagramas de robustez

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Notación

| Elemento | Forma en Mermaid | Estereotipo |
|---|---|---|
| Actor | Estadio `([ ])` | Actor |
| Boundary (vista) | Asimétrica `>[ ]` | `<<boundary>>` |
| Control (lógica del caso de uso) | Círculo `(( ))` | `<<control>>` |
| Entity (clase del dominio) | Cilindro `[( )]` | `<<entity>>` |

La numeración de los mensajes sigue los pasos y flujos alternativos de cada caso de uso.

## 2. Diagramas

### CU-01 — Registrar cuenta de cliente

```mermaid
flowchart LR
    A(["Cliente"])
    C(("<<control>><br/>RegistrarCuentaCliente"))
    B1>"<<boundary>><br/>RegistroDeCliente"]
    B2>"<<boundary>><br/>Login"]
    E1[("<<entity>><br/>Cliente")]

    A -->|"1: elegirCrearCuenta()"| C
    C -->|"2: mostrarFormularioRegistro()"| B1
    A -->|"3: enviarDatos(nombre, apellido, cedulaRuc, celular, correo, contrasena, confirmacion)"| C
    C -->|"4: validarDatos()"| C
    C -->|"4.1: mostrarCamposFaltantes()<br/>4.2: mostrarFormatoInvalido()<br/>4.3: mostrarContrasenasDistintas()<br/>4.4.2: mostrarDatosYaRegistrados()"| B1
    C -->|"4.4.1: consultarCedulaRucYCorreo(cedulaRuc, correo)"| E1
    C -->|"5: generarCodigoCliente()<br/>5.1: registrarCuenta(contrasena cifrada)"| E1
    C -->|"6: mostrarConfirmacion() y redirigir"| B2
```

### CU-02 — Iniciar sesión

```mermaid
flowchart LR
    A(["Usuario<br/>(Cliente, Personal<br/>o Administrador)"])
    C(("<<control>><br/>IniciarSesion"))
    B1>"<<boundary>><br/>Login"]
    B2>"<<boundary>><br/>Menu"]
    B3>"<<boundary>><br/>PanelDePedidos"]
    B4>"<<boundary>><br/>GestionDeCuentasDePersonal"]
    B5>"<<boundary>><br/>AccesoDenegado"]
    E1[("<<entity>><br/>Usuario")]

    A -->|"1: abrirPaginaDeInicioDeSesion()"| C
    C -->|"2: mostrarFormulario()"| B1
    A -->|"3: enviarCredenciales(correo, contrasena)"| C
    C -->|"3.1: solicitarCompletarCampos()<br/>4.1: mostrarCorreoOContrasenaIncorrectos()"| B1
    C -->|"4: validarCredenciales(correo, contrasena)"| E1
    C -->|"5: abrirSesion(rol)"| C
    C -->|"6.A: redirigir (rol = cliente)"| B2
    C -->|"6.B: redirigir (rol = personal)"| B3
    C -->|"6.C: redirigir (rol = admin)"| B4
    A -->|"A.1.1: abrirVistaProtegidaSinSesion()<br/>A.2.1: abrirVistaDeOtroRol()"| C
    C -->|"A.1.2: redirigirAlLogin()"| B1
    C -->|"A.2.2: negarAcceso()"| B5
```

### CU-03 — Cerrar sesión

```mermaid
flowchart LR
    A(["Usuario<br/>(Cliente, Personal<br/>o Administrador)"])
    C(("<<control>><br/>CerrarSesion"))
    B1>"<<boundary>><br/>VistaActual"]
    B2>"<<boundary>><br/>Login"]

    A -->|"1: elegirCerrarSesion()"| C
    C -->|"2: terminarSesion()"| C
    C -->|"2.1: mostrarErrorAlCerrarSesion()"| B1
    C -->|"3: redirigirAlLogin()"| B2
```

### CU-04 — Registrar cuenta de personal

```mermaid
flowchart LR
    A(["Administrador"])
    X(["Cliente o Personal"])
    C(("<<control>><br/>RegistrarCuentaPersonal"))
    B1>"<<boundary>><br/>GestionDeCuentasDePersonal"]
    B2>"<<boundary>><br/>RegistrarPersonal"]
    B3>"<<boundary>><br/>AccesoDenegado"]
    E1[("<<entity>><br/>PersonalCafeteria")]

    A -->|"1: elegirRegistrarPersonal()"| C
    C -->|"2: mostrarFormulario()"| B2
    A -->|"3: enviarDatos(nombre, apellido, correo, contrasena, confirmacion)"| C
    C -->|"4: validarDatos()"| C
    C -->|"4.1: mostrarCamposFaltantes()<br/>4.2: mostrarFormatoInvalido()<br/>4.3: mostrarContrasenasDistintas()<br/>4.4.2: mostrarCorreoYaRegistrado()"| B2
    C -->|"4.4.1: consultarCorreo(correo)"| E1
    C -->|"5: registrarCuenta(rol Personal, contrasena cifrada)"| E1
    C -->|"6: mostrarConfirmacion()"| B1
    X -->|"A.1.1: abrirRegistroDePersonal()"| C
    C -->|"A.1.2: negarAcceso()"| B3
```

### CU-05 — Consultar menú

```mermaid
flowchart LR
    A(["Cliente"])
    C(("<<control>><br/>ConsultarMenu"))
    B1>"<<boundary>><br/>Menu"]
    E1[("<<entity>><br/>Producto")]

    A -->|"1: elegirVerMenu()"| C
    C -->|"2: consultarProductos()"| E1
    C -->|"2.1: mostrarSinProductos()<br/>3: mostrarMenu(nombre, precio, disponibilidad)<br/>3.1: mostrarEtiquetaNoDisponible()"| B1
    A -->|"4: revisarMenu()"| B1
```

### CU-06 — Armar selección y revisar importe

```mermaid
flowchart LR
    A(["Cliente"])
    C(("<<control>><br/>ArmarSeleccion"))
    B1>"<<boundary>><br/>Menu"]
    B2>"<<boundary>><br/>ResumenDeSeleccion"]
    E1[("<<entity>><br/>Producto")]
    E2[("<<entity>><br/>Seleccion")]
    E3[("<<entity>><br/>ItemSeleccion")]

    A -->|"1: agregar(producto)"| C
    C -->|"1.1: verificarDisponibilidad(producto)"| E1
    C -->|"1.1.1: noOfrecerAgregar()"| B1
    C -->|"2: solicitarCantidad()"| B1
    A -->|"3: indicarCantidad(cantidad)"| C
    C -->|"4: validarCantidad() entero entre 1 y 10"| C
    C -->|"4.1: mostrarErrorCantidad()"| B1
    C -->|"5: agregarItem(producto, cantidad)"| E3
    C -->|"5.0: obtenerSeleccion()"| E2
    C -->|"5.1: mostrarMaximoSuperado()<br/>6: mostrarResumen(cantidad, precioUnitario, subtotal, importeTotal)"| B2
    C -->|"6.0: consultarPrecioVigente(producto)"| E1
    A -->|"6.1: modificarCantidad(producto, cantidad)<br/>6.2: quitarProducto(producto)"| C
```

### CU-07 — Confirmar pedido

```mermaid
flowchart LR
    A(["Cliente"])
    C(("<<control>><br/>ConfirmarPedido"))
    B1>"<<boundary>><br/>ResumenDeSeleccion"]
    B2>"<<boundary>><br/>RevisionFinal"]
    B3>"<<boundary>><br/>PedidoConfirmado"]
    E1[("<<entity>><br/>Seleccion")]
    E2[("<<entity>><br/>Producto")]
    E3[("<<entity>><br/>Pedido")]
    E4[("<<entity>><br/>DetallePedido")]

    A -->|"1: elegirConfirmarPedido()"| C
    C -->|"2: mostrarRevisionFinal(productos, cantidades, importeTotal)"| B2
    A -->|"2.1: volver()<br/>3: confirmar()"| C
    C -->|"2.1.1: mostrarResumen()<br/>4.1: mostrarProductosNoDisponibles()<br/>4.3: mostrarSeleccionVacia()<br/>6.1: mostrarErrorAlGuardar()"| B1
    C -->|"4: validarSeleccion()"| E1
    C -->|"4.0: verificarDisponibilidadYPrecios()"| E2
    C -->|"4.2: mostrarPrecioModificado()"| B2
    C -->|"5: generarCodigoRetiro()<br/>5.1: regenerarSiCodigoRepetido()"| E3
    C -->|"6: registrarPedido(cliente, importeTotal, estado Pendiente)"| E3
    C -->|"6.0: registrarDetalles(producto, cantidad, precioRegistrado)"| E4
    C -->|"7: mostrarResumenYCodigo()"| B3
    C -->|"7.1: vaciarSeleccion()"| E1
```

### CU-08 — Consultar mis pedidos

```mermaid
flowchart LR
    A(["Cliente"])
    C(("<<control>><br/>ConsultarMisPedidos"))
    B1>"<<boundary>><br/>MisPedidos"]
    B2>"<<boundary>><br/>DetalleDePedidoCliente"]
    E1[("<<entity>><br/>Pedido")]
    E2[("<<entity>><br/>DetallePedido")]

    A -->|"1: elegirMisPedidos()"| C
    C -->|"2: consultarPedidosDelCliente(codigoCliente)"| E1
    C -->|"2.1: mostrarSinPedidos()<br/>3: mostrarLista(codigoRetiro, fecha, estado, importe)<br/>3.1: resaltarListoParaRetirar()"| B1
    A -->|"4: seleccionarPedido(codigoRetiro)"| C
    C -->|"5.0: consultarDetalle(codigoRetiro)"| E2
    C -->|"5: mostrarDetalle(productos, cantidades, precios, importeTotal, estado)"| B2
```

### CU-09 — Registrar producto

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>RegistrarProducto"))
    B1>"<<boundary>><br/>GestionDeProductos"]
    B2>"<<boundary>><br/>NuevoProducto"]
    E1[("<<entity>><br/>Producto")]

    A -->|"1: elegirAgregarProducto()"| C
    C -->|"2: mostrarFormulario(nombre, precio, disponibilidad)"| B2
    A -->|"3: enviarDatos(nombre, precio, disponibilidad)"| C
    C -->|"4: validarDatos()"| C
    C -->|"4.1: mostrarCamposFaltantes()<br/>4.2: mostrarPrecioInvalido()<br/>4.3.2: mostrarNombreDuplicado()<br/>4.4: mostrarNombreMuyLargo()"| B2
    C -->|"4.3.1: consultarNombre(nombre)"| E1
    C -->|"5: generarCodigoProducto()<br/>5.1: registrarProducto(nombre, precio, disponible)"| E1
    C -->|"6: mostrarConfirmacionYLista()"| B1
```

### CU-10 — Actualizar nombre y precio de producto

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>ActualizarProducto"))
    B1>"<<boundary>><br/>GestionDeProductos"]
    B2>"<<boundary>><br/>EditarProducto"]
    E1[("<<entity>><br/>Producto")]

    A -->|"1: elegirEditar(codigoProducto)"| C
    C -->|"1.1: consultarProducto(codigoProducto)"| E1
    C -->|"2: mostrarFormulario(datos actuales)"| B2
    A -->|"3: guardar(nombre, precio)"| C
    A -->|"3.1: cancelar()"| C
    C -->|"3.1.1: regresarSinCambios()"| B1
    C -->|"4: validarDatos()"| C
    C -->|"4.1: mostrarDatosInvalidos()<br/>4.2.2: mostrarNombreDuplicado()"| B2
    C -->|"4.2.1: consultarNombre(nombre)"| E1
    C -->|"5: actualizarProducto(nombre, precio)"| E1
    C -->|"6: mostrarConfirmacionYLista()"| B1
```

### CU-11 — Cambiar disponibilidad de producto

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>CambiarDisponibilidad"))
    B1>"<<boundary>><br/>GestionDeProductos"]
    E1[("<<entity>><br/>Producto")]

    A -->|"1: cambiarDisponibilidad(codigoProducto, disponible)"| C
    C -->|"2: actualizarDisponibilidad(codigoProducto, disponible)"| E1
    C -->|"2.1: mostrarErrorAlGuardar()<br/>3: mostrarListaActualizada()"| B1
```

### CU-12 — Consultar pedidos recibidos

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>ConsultarPedidosRecibidos"))
    B1>"<<boundary>><br/>PanelDePedidos"]
    B2>"<<boundary>><br/>DetalleDePedidoPersonal"]
    E1[("<<entity>><br/>Pedido")]
    E2[("<<entity>><br/>DetallePedido")]
    E3[("<<entity>><br/>Cliente")]

    A -->|"1: abrirPanelDePedidos()"| C
    C -->|"2: consultarPedidosPendientes()"| E1
    C -->|"2.1: consultarDatosDelCliente(nombre, apellido)"| E3
    C -->|"3: mostrarLista(codigoRetiro, cliente, fecha, estado, importe)<br/>3.2: mostrarSinPedidos()"| B1
    A -->|"3.1: filtrarPorEstado(estado)"| C
    C -->|"3.1.1: consultarPedidosPorEstado(estado)"| E1
    A -->|"4: seleccionarPedido(codigoRetiro)"| C
    C -->|"5.0: consultarDetalle(codigoRetiro)"| E2
    C -->|"5: mostrarDetalle(productos, cantidades, precios, importeTotal)"| B2
```

### CU-13 — Iniciar preparación

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>IniciarPreparacion"))
    B1>"<<boundary>><br/>DetalleDePedidoPersonal"]
    B2>"<<boundary>><br/>PanelDePedidos"]
    E1[("<<entity>><br/>Pedido")]

    A -->|"1: seleccionarPedidoPendiente(codigoRetiro)"| C
    C -->|"1.1: consultarEstado(codigoRetiro)"| E1
    C -->|"2: mostrarDetalleConAccionIniciarPreparacion()<br/>2.1: noOfrecerAccion()"| B1
    A -->|"3: elegirIniciarPreparacion()"| C
    C -->|"4: verificarEstadoPendiente(codigoRetiro)"| E1
    C -->|"4.1: informarEstadoModificado()"| B2
    C -->|"5: cambiarEstado(En preparación)"| E1
    C -->|"6: confirmarCambioYActualizarLista()"| B2
```

### CU-14 — Marcar como listo para retirar

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>MarcarListo"))
    B1>"<<boundary>><br/>DetalleDePedidoPersonal"]
    B2>"<<boundary>><br/>PanelDePedidos"]
    E1[("<<entity>><br/>Pedido")]

    A -->|"1: seleccionarPedidoEnPreparacion(codigoRetiro)"| C
    C -->|"1.1: consultarEstado(codigoRetiro)"| E1
    C -->|"2: mostrarDetalleConAccionMarcarComoListo()<br/>2.1: noOfrecerAccion()"| B1
    A -->|"3: elegirMarcarComoListo()"| C
    C -->|"4: verificarEstadoEnPreparacion(codigoRetiro)"| E1
    C -->|"4.1: informarEstadoModificado()"| B2
    C -->|"5: cambiarEstado(Listo para retirar)"| E1
    C -->|"6: confirmarCambioYActualizarLista()"| B2
```

### CU-15 — Entregar pedido

```mermaid
flowchart LR
    A(["Personal de cafetería"])
    C(("<<control>><br/>EntregarPedido"))
    B1>"<<boundary>><br/>PanelDePedidos"]
    B2>"<<boundary>><br/>IngresoDeCodigo"]
    B3>"<<boundary>><br/>ResumenDeEntrega"]
    E1[("<<entity>><br/>Pedido")]
    E2[("<<entity>><br/>DetallePedido")]
    E3[("<<entity>><br/>Cliente")]

    A -->|"1: elegirEntregarPedido()"| C
    C -->|"2: solicitarCodigoRetiro()"| B2
    A -->|"3: ingresarCodigo(codigoRetiro)"| C
    C -->|"3.1: solicitarCodigo()<br/>4.1: mostrarCodigoInexistente()"| B2
    C -->|"4: validarFormatoYBuscarPedido(codigoRetiro)"| E1
    C -->|"4.2: informarEstadoActual()<br/>4.3: informarYaEntregado()"| B1
    C -->|"5.0: consultarDetalleYCliente()"| E2
    C -->|"5.1: consultarNombreApellido()"| E3
    C -->|"5: mostrarResumen(codigo, cliente, productos, cantidades, importeTotal)"| B3
    A -->|"6: confirmarEntrega()<br/>6.1: cancelar()"| C
    C -->|"7: verificarEstadoListoParaRetirar()<br/>7.0: cambiarEstado(Entregado)"| E1
    C -->|"7.1: informarEstadoModificado()<br/>8: confirmarEntrega()<br/>6.1.1: regresarSinCambios()"| B1
```