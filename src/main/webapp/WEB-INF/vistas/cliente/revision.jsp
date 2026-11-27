<c:set var="titulo" value="Revisión final"/>
<c:set var="seccion" value="seleccion"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Revisión final</span>
    <h1>Confirma tu pedido</h1>
    <p class="tenue">Al confirmar se registra el pedido y recibes tu código de retiro.</p>
</div>
<div class="marco"><div class="tarjeta tabla-caja">
    <table class="tabla">
        <thead><tr><th>Producto</th><th class="num">Cantidad</th><th class="num">Precio</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="i" items="${sel.items}">
            <tr>
                <td><c:out value="${i.nombre}"/></td>
                <td class="num">${i.cantidad}</td>
                <td class="num">$<fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td class="num">$<fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        </tbody>
        <tfoot>
            <tr><td colspan="3">Total</td><td class="num">$<fmt:formatNumber value="${sel.total}" minFractionDigits="2" maxFractionDigits="2"/></td></tr>
        </tfoot>
    </table>
</div></div>
<p class="tenue" style="margin:0">El pago se realiza de forma presencial en el mostrador, fuera de la aplicación.</p>
<form class="acciones" method="post" action="${ctx}/cliente/confirmar">
    <button class="boton boton-flecha" type="submit">Confirmar pedido
        <span class="icono" aria-hidden="true"><svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M3 8.5l3 3 7-7"/></svg></span>
    </button>
    <a class="boton boton-claro" href="${ctx}/cliente/seleccion">Volver</a>
</form>
<%@ include file="../comun/pie.jspf" %>
