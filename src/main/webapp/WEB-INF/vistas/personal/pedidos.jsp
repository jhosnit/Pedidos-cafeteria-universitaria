<c:set var="titulo" value="Panel de pedidos"/>
<c:set var="seccion" value="pedidos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Atención</span>
        <h1>Panel de pedidos</h1>
        <p>Del más antiguo al más reciente, para atender en orden de llegada.</p>
    </div>
    <a class="boton boton-oscuro" href="${ctx}/personal/entrega">Entregar con código</a>
</div>
<nav class="filtros" aria-label="Filtrar por estado">
    <c:forEach var="e" items="${estados}">
        <a class="${filtro == e.name() ? 'activo' : ''}" href="${ctx}/personal/pedidos?estado=${e.name()}">${e.etiqueta}</a>
    </c:forEach>
    <a class="${filtro == 'TODOS' ? 'activo' : ''}" href="${ctx}/personal/pedidos?estado=TODOS">Todos</a>
</nav>
<c:choose>
    <c:when test="${empty pedidos}">
        <div class="vacio">No hay pedidos para este filtro.</div>
    </c:when>
    <c:otherwise>
        <div class="lista">
            <c:forEach var="p" items="${pedidos}">
                <div class="fila-pedido">
                    <a class="codigo" href="${ctx}/personal/pedidos?id=${p.id}">${p.codigoRetiro}</a>
                    <span><c:out value="${p.nombreCliente}"/><br><span class="tenue" style="font-size:13px">${p.fechaTexto}</span></span>
                    <span class="estado ${p.estado.claseCss}">${p.estado.etiqueta}</span>
                    <span class="importe ocultable">$<fmt:formatNumber value="${p.importeTotal}" minFractionDigits="2" maxFractionDigits="2"/></span>
                    <span class="ocultable">
                        <c:choose>
                            <c:when test="${p.estado == 'PENDIENTE' or p.estado == 'EN_PREPARACION'}">
                                <form method="post" action="${ctx}/personal/pedidos">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <input type="hidden" name="estado" value="${p.estado}">
                                    <button class="boton boton-chico" type="submit">${p.estado == 'PENDIENTE' ? 'Iniciar preparación' : 'Marcar como listo'}</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <a class="boton boton-claro boton-chico" href="${ctx}/personal/pedidos?id=${p.id}">Ver</a>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../comun/pie.jspf" %>
