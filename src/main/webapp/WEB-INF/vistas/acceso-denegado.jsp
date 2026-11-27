<c:set var="titulo" value="Acceso denegado"/>
<%@ include file="comun/cabecera.jspf" %>
<div class="vacio">
    <h1>No tienes acceso a esta página</h1>
    <p>Tu cuenta no tiene permiso para ver esta sección.</p>
    <a class="boton" href="${ctx}${sessionScope.usuario.rol.inicio}">Ir a mi inicio</a>
</div>
<%@ include file="comun/pie.jspf" %>
