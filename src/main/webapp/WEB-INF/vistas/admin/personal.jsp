<c:set var="titulo" value="Personal de cafetería"/>
<%@ include file="../comun/cabecera.jspf" %>
<div class="encabezado">
    <div>
        <span class="etiqueta-seccion">Administración</span>
        <h1>Cuentas de personal</h1>
        <p>Las personas de esta lista pueden atender pedidos y mantener el menú.</p>
    </div>
    <a class="boton boton-flecha" href="${ctx}/admin/personal/nuevo">Registrar personal
        <span class="icono" aria-hidden="true"><svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M7 2v10M2 7h10"/></svg></span>
    </a>
</div>
<c:choose>
    <c:when test="${empty lista}">
        <div class="vacio">Todavía no hay cuentas de personal registradas.</div>
    </c:when>
    <c:otherwise>
        <div class="marco"><div class="tarjeta tabla-caja">
            <table class="tabla">
                <thead><tr><th>Nombre</th><th>Correo</th></tr></thead>
                <tbody>
                <c:forEach var="p" items="${lista}">
                    <tr><td><c:out value="${p.nombreCompleto}"/></td><td><c:out value="${p.correo}"/></td></tr>
                </c:forEach>
                </tbody>
            </table>
        </div></div>
    </c:otherwise>
</c:choose>
<%@ include file="../comun/pie.jspf" %>
