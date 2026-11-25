<c:set var="titulo" value="Pedido ${pedido.codigoRetiro}"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Detalle del pedido</span>
        <h1 class="mono">${pedido.codigoRetiro}</h1>
        <p><c:out value="${pedido.nombreCliente}"/> · ${pedido.fechaTexto}</p>
    </div>
    <span class="estado ${pedido.estado.claseCss}">${pedido.estado.etiqueta}</span>
</div>
<div class="marco"><div class="tarjeta tabla-caja">
    <table class="tabla">
        <thead><tr><th>Producto</th><th class="num">Cantidad</th><th class="num">Precio registrado</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="d" items="${pedido.detalles}">
            <tr>
                <td><c:out value="${d.nombreProducto}"/></td>
                <td class="num">${d.cantidad}</td>
                <td class="num">$<fmt:formatNumber value="${d.precioRegistrado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="num">$<fmt:formatNumber value="${d.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot><tr><td colspan="3">Total</td><td class="num">$<fmt:formatNumber value="${pedido.importeTotal}" minFractionDigits="2" maxFractionDigits="2"/></td></tr></tfoot>
    </table>
</div></div>
<div class="acciones">
    <c:if test="${pedido.estado == 'PENDIENTE' or pedido.estado == 'EN_PREPARACION'}">
        <form method="post" action="${ctx}/personal/pedidos">
            <input type="hidden" name="id" value="${pedido.id}">
            <input type="hidden" name="estado" value="${pedido.estado}">
            <button class="boton" type="submit">${pedido.estado == 'PENDIENTE' ? 'Iniciar preparación' : 'Marcar como listo'}</button>
        </form>
    </c:if>
    <a class="boton boton-claro" href="${ctx}/personal/pedidos?estado=${pedido.estado}">Volver</a>
</div>
<%@ include file="../comun/pie.jspf" %>
