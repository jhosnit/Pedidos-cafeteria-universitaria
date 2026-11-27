<c:set var="titulo" value="Productos"/>
<c:set var="seccion" value="productos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Menú</span>
        <h1>Productos</h1>
        <p>Los productos no se eliminan: si algo se acabó, márcalo como no disponible.</p>
    </div>
    <a class="boton boton-flecha" href="${ctx}/personal/productos/form">Nuevo producto
        <span class="icono" aria-hidden="true"><svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M7 2v10M2 7h10"/></svg></span>
    </a>
</div>
<c:choose>
    <c:when test="${empty productos}">
        <div class="vacio">Aún no hay productos registrados.</div>
    </c:when>
    <c:otherwise>
        <div class="marco"><div class="tarjeta tabla-caja">
            <table class="tabla">
                <thead><tr><th>Código</th><th>Nombre</th><th class="num">Precio</th><th>Estado</th><th>Acciones</th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${productos}">
                    <tr>
                        <td class="mono">${p.codigo}</td>
                        <td><c:out value="${p.nombre}"/></td>
                        <td class="num">$<fmt:formatNumber value="${p.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td>
                            <span class="estado ${p.disponible ? 'listo_para_retirar' : 'no-disponible'}">${p.disponible ? 'Disponible' : 'No disponible'}</span>
                        </td>
                        <td>
                            <div class="acciones">
                                <form method="post" action="${ctx}/personal/productos">
                                    <input type="hidden" name="id" value="${p.id}">
                                    <button class="boton boton-claro boton-chico" type="submit">
                                        ${p.disponible ? 'Marcar no disponible' : 'Marcar disponible'}
                                    </button>
                                </form>
                                <a class="boton boton-claro boton-chico" href="${ctx}/personal/productos/form?id=${p.id}">Editar</a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div></div>
    </c:otherwise>
</c:choose>
<%@ include file="../comun/pie.jspf" %>
