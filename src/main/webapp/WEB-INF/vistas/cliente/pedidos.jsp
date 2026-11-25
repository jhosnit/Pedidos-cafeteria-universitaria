<c:set var="titulo" value="Mis pedidos"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Seguimiento</span>
        <h1>Mis pedidos</h1>
        <p>Del más reciente al más antiguo. Actualiza la página para ver el estado nuevo.</p>
    </div>
    <a class="boton boton-claro" href="${ctx}/cliente/menu">Ver menú</a>
</div>
<c:choose>
    <c:when test="${empty pedidos}">
        <div class="vacio">Aún no has realizado ningún pedido.</div>
    </c:when>
    <c:otherwise>
        <div class="lista">
            <c:forEach var="p" items="${pedidos}">
                <a class="fila-pedido ${p.estado == 'LISTO_PARA_RETIRAR' ? 'resaltado' : ''}" href="${ctx}/cliente/pedidos?id=${p.id}">
                    <span class="codigo">${p.codigoRetiro}</span>
                    <span class="tenue">
                        ${p.fechaTexto}
                        <c:if test="${p.estado == 'LISTO_PARA_RETIRAR'}"><br><strong style="color:var(--listo-texto)">Ya puedes retirarlo con tu código.</strong></c:if>
                    </span>
                    <span class="estado ${p.estado.claseCss}">${p.estado.etiqueta}</span>
                    <span class="importe ocultable">$<fmt:formatNumber value="${p.importeTotal}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    <span class="ocultable tenue" aria-hidden="true">Ver</span>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../comun/pie.jspf" %>
