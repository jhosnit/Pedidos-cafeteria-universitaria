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
<%@ include file="../comun/pie.jspf" %>
