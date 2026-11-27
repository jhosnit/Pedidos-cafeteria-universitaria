<c:set var="titulo" value="${producto.id > 0 ? 'Editar producto' : 'Nuevo producto'}"/>
<c:set var="seccion" value="productos"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Menú</span>
    <h1>${titulo}</h1>
</div>
<div class="marco">
    <div class="tarjeta">
        <c:if test="${not empty errores}">
            <div class="aviso aviso-error" role="alert">No se pudo guardar:
                <ul><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
            </div>
        </c:if>
        <form class="formulario" method="post" action="${ctx}/personal/productos/form" novalidate>
            <input type="hidden" name="id" value="${producto.id}">
            <div class="fila">
                <div class="campo">
                    <label for="nombre">Nombre</label>
                    <input class="entrada" id="nombre" name="nombre" maxlength="70" value="<c:out value='${producto.nombre}'/>" required>
                    <small>Hasta 70 caracteres. No puede repetirse.</small>
                </div>
                <div class="campo">
                    <label for="precio">Precio (USD)</label>
                    <input class="entrada mono" id="precio" name="precio" inputmode="decimal" placeholder="1.50" value="<c:out value='${precioTexto}'/>" required>
                    <small>Mayor que 0, máximo 2 decimales.</small>
                </div>
            </div>
            <c:if test="${producto.id == 0}">
                <label class="casilla"><input type="checkbox" name="noDisponible" ${producto.disponible ? '' : 'checked'}> Registrar como no disponible</label>
            </c:if>
            <div class="acciones">
                <button class="boton" type="submit">Guardar</button>
                <a class="boton boton-claro" href="${ctx}/personal/productos">Cancelar</a>
            </div>
        </form>
    </div>
</div>
<%@ include file="../comun/pie.jspf" %>
