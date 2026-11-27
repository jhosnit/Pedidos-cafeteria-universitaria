<c:set var="titulo" value="Crear cuenta"/>
<%@ include file="comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Nueva cuenta</span>
    <h1>Crea tu cuenta</h1>
    <p class="tenue">La necesitas para hacer pedidos y ver en qué estado están.</p>
</div>
<div class="marco">
    <div class="tarjeta">
        <c:if test="${not empty errores}">
            <div class="aviso aviso-error" role="alert">No pudimos crear la cuenta:
                <ul><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
            </div>
        </c:if>
        <form class="formulario" method="post" action="${ctx}/registro" novalidate>
            <div class="fila">
                <div class="campo">
                    <label for="nombre">Nombre</label>
                    <input class="entrada" id="nombre" name="nombre" maxlength="60" value="<c:out value='${cliente.nombre}'/>" required>
                </div>
                <div class="campo">
                    <label for="apellido">Apellido</label>
                    <input class="entrada" id="apellido" name="apellido" maxlength="60" value="<c:out value='${cliente.apellido}'/>" required>
                </div>
            </div>
            <div class="fila">
                <div class="campo">
                    <label for="cedulaRuc">Cédula o RUC</label>
                    <input class="entrada" id="cedulaRuc" name="cedulaRuc" inputmode="numeric" maxlength="13" value="<c:out value='${cliente.cedulaRuc}'/>" required>
                </div>
                <div class="campo">
                    <label for="celular">Celular</label>
                    <input class="entrada" id="celular" name="celular" inputmode="numeric" maxlength="10" placeholder="0991234567" value="<c:out value='${cliente.celular}'/>" required>
                </div>
            </div>
            <div class="campo">
                <label for="correo">Correo institucional</label>
                <input class="entrada" id="correo" name="correo" type="email" placeholder="nombre.apellido@epn.edu.ec" value="<c:out value='${cliente.correo}'/>" required>
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
                <button class="boton" type="submit">Crear cuenta</button>
                <a class="boton boton-claro" href="${ctx}/login">Volver</a>
            </div>
        </form>
    </div>
</div>
<%@ include file="comun/pie.jspf" %>
