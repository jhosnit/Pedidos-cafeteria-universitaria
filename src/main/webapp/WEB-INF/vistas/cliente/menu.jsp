<c:set var="titulo" value="Menú"/>
<c:set var="seccion" value="menu"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Menú de hoy</span>
        <h1>¿Qué se te antoja?</h1>
        <p>Precios en dólares. El pago se hace en el mostrador al retirar.</p>
    </div>
</div>
<c:choose>
    <c:when test="${empty productos}">
        <div class="vacio">Aún no hay productos en el menú.</div>
    </c:when>
    <c:otherwise>
        <div class="rejilla">
            <c:forEach var="p" items="${productos}">
                <c:choose>
                    <c:when test="${p.disponible}">
                        <div class="marco"><div class="tarjeta producto">
                            <div class="producto-cabeza">
                                <span class="producto-nombre"><c:out value="${p.nombre}"/></span>
                                <span class="precio">$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></span>
                            </div>
                            <form class="agregar" method="post" action="${ctx}/cliente/seleccion">
                                <input type="hidden" name="accion" value="agregar">
                                <input type="hidden" name="productoId" value="${p.id}">
                                <input class="entrada entrada-cantidad" id="cant-${p.id}" name="cantidad" type="number"
                                       min="1" max="10" step="1" value="1" aria-label="Cantidad de ${fn:escapeXml(p.nombre)}">
                                <button class="boton boton-ancho" type="submit">Agregar</button>
                            </form>
                        </div></div>
                    </c:when>
                    <c:otherwise>
                        <div class="tarjeta-apagada producto">
                            <div class="producto-cabeza">
                                <span class="producto-nombre"><c:out value="${p.nombre}"/></span>
                                <span class="precio">$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></span>
                            </div>
                            <span class="estado no-disponible">No disponible</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<c:if test="${not empty sessionScope.seleccion and not sessionScope.seleccion.vacia}">
    <div class="barra-total">
        <div class="acciones">
            <span class="tenue">${sessionScope.seleccion.cantidadProductos} productos</span>
            <span class="monto">$<fmt:formatNumber value="${sessionScope.seleccion.total}" minFractionDigits="2" maxFractionDigits="2"/></span>
        </div>
        <a class="boton boton-flecha" href="${ctx}/cliente/seleccion">Ver mi selección
            <span class="icono" aria-hidden="true"><svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="#FFFFFF" stroke-width="1.6"><path d="M4 12L12 4M6 4h6v6"/></svg></span>
        </a>
    </div>
</c:if>
<%@ include file="../comun/pie.jspf" %>
