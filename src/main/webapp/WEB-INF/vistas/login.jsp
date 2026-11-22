<c:set var="titulo" value="Iniciar sesión"/>
<%@ include file="comun/cabecera.jspf" %>
<div>
    <span class="etiqueta-seccion">Cafetería EPN</span>
    <h1>Pide antes del receso.<br><span class="tenue">Retira sin hacer fila.</span></h1>
</div>
<div class="marco">
    <div class="tarjeta">
        <c:if test="${not empty error}">
            <p class="aviso aviso-error" role="alert"><c:out value="${error}"/></p>
        </c:if>
        <form class="formulario" method="post" action="${ctx}/login" novalidate>
            <div class="campo">
                <label for="correo">Correo institucional</label>
                <input class="entrada" id="correo" name="correo" type="email" autocomplete="username"
                       placeholder="nombre.apellido@epn.edu.ec" value="<c:out value='${correo}'/>" required>
            </div>
            <div class="campo">
                <label for="contrasena">Contraseña</label>
                <input class="entrada" id="contrasena" name="contrasena" type="password" autocomplete="current-password" required>
            </div>
            <button class="boton boton-flecha" type="submit">Ingresar
                <span class="icono" aria-hidden="true"><svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.6"><path d="M4 12L12 4M6 4h6v6"/></svg></span>
            </button>
        </form>
    </div>
</div>
<p class="tenue" style="text-align:center;margin:0">¿Aún no tienes cuenta? <a href="${ctx}/registro">Crea una aquí</a></p>
<%@ include file="comun/pie.jspf" %>
