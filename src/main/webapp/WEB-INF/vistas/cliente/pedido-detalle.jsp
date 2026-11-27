<c:set var="titulo" value="Pedido ${pedido.codigoRetiro}"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Detalle del pedido</span>
        <h1 class="mono">${pedido.codigoRetiro}</h1>
        <p>Registrado el ${pedido.fechaTexto}</p>
    </div>
    <span class="estado ${pedido.estado.claseCss}">${pedido.estado.etiqueta}</span>
</div>
<c:if test="${pedido.estado == 'LISTO_PARA_RETIRAR'}">
    <p class="aviso aviso-ok">Tu pedido está listo. Acércate al mostrador y presenta el código ${pedido.codigoRetiro}.</p>
</c:if>
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
<div class="acciones"><a class="boton boton-claro" href="${ctx}/cliente/pedidos">Volver a mis pedidos</a></div>
<%@ include file="../comun/pie.jspf" %>
