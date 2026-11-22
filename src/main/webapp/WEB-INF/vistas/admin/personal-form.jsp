<c:set var="titulo" value="Registrar personal"/>
<%@ include file="../comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Administración</span>
    <h1>Registrar personal</h1>
    <p class="tenue">La cuenta se crea con rol Personal.</p>
</div>
<div class="marco">
    <div class="tarjeta">
        <c:if test="${not empty errores}">
            <div class="aviso aviso-error" role="alert">No se pudo registrar la cuenta:
                <ul><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
            </div>
        </c:if>
        <form class="formulario" method="post" action="${ctx}/admin/personal/nuevo" novalidate>
            <div class="fila">
                <div class="campo">
                    <label for="nombre">Nombre</label>
                    <input class="entrada" id="nombre" name="nombre" maxlength="60" value="<c:out value='${personal.nombre}'/>" required>
                </div>
                <div class="campo">
                    <label for="apellido">Apellido</label>
                    <input class="entrada" id="apellido" name="apellido" maxlength="60" value="<c:out value='${personal.apellido}'/>" required>
                </div>
            </div>
            <div class="campo">
                <label for="correo">Correo institucional</label>
                <input class="entrada" id="correo" name="correo" type="email" placeholder="nombre.apellido@epn.edu.ec" value="<c:out value='${personal.correo}'/>" required>
            </div>
            <div class="fila">
                <div class="campo">
                    <label for="contrasena">Contraseña</label>
                    <input class="entrada" id="contrasena" name="contrasena" type="password" autocomplete="new-password" required>
                    <small>Mínimo 8 caracteres.</small>
                </div>
                <div class="campo">
                    <label for="confirmacion">Confirmar contraseña</label>
                    <input class="entrada" id="confirmacion" name="confirmacion" type="password" autocomplete="new-password" required>
                </div>
            </div>
            <div class="acciones">
                <button class="boton" type="submit">Guardar</button>
                <a class="boton boton-claro" href="${ctx}/admin/personal">Cancelar</a>
            </div>
        </form>
    </div>
</div>
<%@ include file="../comun/pie.jspf" %>
