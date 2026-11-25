<c:set var="titulo" value="Resumen de entrega"/>
<c:set var="seccion" value="entrega"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Verifica antes de entregar</span>
    <h1>Resumen de entrega</h1>
</div>
<div class="dos-columnas">
    <div class="marco"><div class="tarjeta tabla-caja">
        <h3><c:out value="${pedido.nombreCliente}"/></h3>
        <p class="tenue">Pedido registrado el ${pedido.fechaTexto}</p>
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
            <tfoot><tr><td colspan="2">Total a cobrar</td><td class="num">$<fmt:formatNumber value="${pedido.importeTotal}" minFractionDigits="2" maxFractionDigits="2"/></td></tr></tfoot>
        </table>
    </div></div>
    <div class="ticket">
        <span class="rotulo">LISTO PARA RETIRAR</span>
        <span class="grande">${pedido.codigoRetiro}</span>
        <span>Confirma la entrega solo si el código coincide con el que muestra el cliente.</span>
    </div>
</div>
<form class="acciones" method="post" action="${ctx}/personal/entrega">
    <input type="hidden" name="accion" value="confirmar">
    <input type="hidden" name="id" value="${pedido.id}">
    <input type="hidden" name="codigo" value="${pedido.codigoRetiro}">
    <button class="boton" type="submit">Confirmar entrega</button>
    <a class="boton boton-claro" href="${ctx}/personal/entrega">Cancelar</a>
</form>
<%@ include file="../comun/pie.jspf" %>
