<c:set var="titulo" value="Entregar pedido"/>
<c:set var="seccion" value="entrega"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Mostrador</span>
    <h1>Entregar pedido</h1>
    <p class="tenue">Escribe el código que presenta el cliente. Puedes escribirlo en minúsculas o sin guion.</p>
</div>
<div class="marco">
    <div class="tarjeta">
        <c:if test="${not empty error}">
            <p class="aviso aviso-error" role="alert"><c:out value="${error}"/></p>
        </c:if>
        <form class="formulario" method="post" action="${ctx}/personal/entrega" novalidate>
            <input type="hidden" name="accion" value="buscar">
            <div class="campo">
                <label for="codigo">Código de retiro</label>
                <input class="entrada entrada-codigo" id="codigo" name="codigo" maxlength="8" placeholder="AB-01"
                       autocomplete="off" value="<c:out value='${codigo}'/>">
            </div>
            <div class="acciones">
                <button class="boton" type="submit">Buscar pedido</button>
                <a class="boton boton-claro" href="${ctx}/personal/pedidos">Cancelar</a>
            </div>
        </form>
    </div>
</div>
<%@ include file="../comun/pie.jspf" %>
