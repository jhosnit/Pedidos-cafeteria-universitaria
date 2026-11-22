<%@ page isErrorPage="true" %>
<c:set var="titulo" value="Algo salió mal"/>
<%@ include file="comun/cabecera.jspf" %>
<div class="vacio">
    <c:choose>
        <c:when test="${pageContext.errorData.statusCode == 404}">
            <h1>Página no encontrada</h1>
            <p>La dirección que abriste no existe.</p>
        </c:when>
        <c:otherwise>
            <h1>Algo salió mal</h1>
            <p>No pudimos completar la operación. Intenta de nuevo en un momento.</p>
        </c:otherwise>
    </c:choose>
    <a class="boton" href="${ctx}/">Volver al inicio</a>
</div>
<%@ include file="comun/pie.jspf" %>
