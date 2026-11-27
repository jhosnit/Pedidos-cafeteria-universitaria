<c:set var="titulo" value="Mi selección"/>
<c:set var="seccion" value="seleccion"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Antes de confirmar</span>
        <h1>Mi selección</h1>
        <p>Todavía no es un pedido. Revisa las cantidades y confirma cuando estés listo.</p>
    </div>
    <a class="boton boton-claro" href="${ctx}/cliente/menu">Seguir agregando</a>
</div>
<c:choose>
    <c:when test="${sel.vacia}">
        <div class="vacio">Tu selección está vacía. Total: $0.00</div>
    </c:when>
    <c:otherwise>
        <div class="marco"><div class="tarjeta tabla-caja">
            <table class="tabla">
                <thead><tr><th>Producto</th><th class="num">Precio</th><th>Cantidad</th><th class="num">Subtotal</th><th><span hidden>Quitar</span></th></tr></thead>
                <tbody>
                <c:forEach var="i" items="${sel.items}">
                    <tr>
                        <td><c:out value="${i.nombre}"/></td>
                        <td class="num">$<fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>
                            <form class="acciones" method="post" action="${ctx}/cliente/seleccion">
                                <input type="hidden" name="accion" value="cambiar">
                                <input type="hidden" name="productoId" value="${i.productoId}">
                                <input class="entrada entrada-cantidad" name="cantidad" type="number" min="1" max="10" step="1"
                                       value="${i.cantidad}" aria-label="Cantidad de ${fn:escapeXml(i.nombre)}">
                                <button class="boton boton-claro boton-chico" type="submit">Actualizar</button>
                            </form>
                        </td>
                        <td class="num">$<fmt:formatNumber value="${i.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>
                            <form method="post" action="${ctx}/cliente/seleccion">
                                <input type="hidden" name="accion" value="quitar">
                                <input type="hidden" name="productoId" value="${i.productoId}">
                                <button class="boton boton-claro boton-chico" type="submit">Quitar</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div></div>
    </c:otherwise>
</c:choose>
<div class="barra-total">
    <div class="acciones">
        <span class="tenue">Total a pagar</span>
        <span class="monto">$<fmt:formatNumber value="${sel.total}" minFractionDigits="2" maxFractionDigits="2"/></span>
    </div>
    <c:choose>
        <c:when test="${sel.vacia}">
            <button class="boton" type="button" disabled>Revisar pedido</button>
        </c:when>
        <c:otherwise>
            <a class="boton boton-flecha" href="${ctx}/cliente/confirmar">Revisar pedido
                <span class="icono" aria-hidden="true"><svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="#FFFFFF" stroke-width="1.6"><path d="M4 12L12 4M6 4h6v6"/></svg></span>
            </a>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="../comun/pie.jspf" %>
