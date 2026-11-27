<c:set var="titulo" value="Pedido confirmado"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Pedido confirmado</span>
    <h1>Listo, recibimos tu pedido</h1>
    <p class="tenue">Tu pedido está <strong>Pendiente</strong>. Revisa su estado en «Mis pedidos».</p>
</div>
<div class="dos-columnas">
    <div class="marco"><div class="tarjeta tabla-caja">
        <table class="tabla">
            <thead><tr><th>Producto</th><th class="num">Cantidad</th><th class="num">Subtotal</th></tr></thead>
            <tbody>
            <c:forEach var="d" items="${pedido.detalles}">
                <tr>
                    <td><c:out value="${d.nombreProducto}"/></td>
                    <td class="num">${d.cantidad}</td>
                    <td class="num">$<fmt:formatNumber value="${d.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot><tr><td colspan="2">Total</td><td class="num">$<fmt:formatNumber value="${pedido.importeTotal}" minFractionDigits="2" maxFractionDigits="2"/></td></tr></tfoot>
        </table>
        <p class="tenue">Registrado el ${pedido.fechaTexto}</p>
    </div></div>
    <div class="ticket">
        <span class="rotulo">CÓDIGO DE RETIRO</span>
        <span class="grande">${pedido.codigoRetiro}</span>
        <span>Preséntalo en el mostrador. El pago se realiza en persona, fuera de la aplicación.</span>
    </div>
</div>
<div class="acciones">
    <a class="boton" href="${ctx}/cliente/pedidos">Ver mis pedidos</a>
    <a class="boton boton-claro" href="${ctx}/cliente/menu">Volver al menú</a>
</div>
<%@ include file="../comun/pie.jspf" %>
