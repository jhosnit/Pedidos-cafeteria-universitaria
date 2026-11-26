# Manual de instalación

**Release:** 1.0  
**Versión del documento:** 1.0  

Este manual explica cómo dejar funcionando el sistema de pedidos de la cafetería en un computador con Windows, macOS o Linux. Toda la aplicación (servidor web y base de datos) corre en contenedores, así que no hace falta instalar Java, Maven, Tomcat ni PostgreSQL.

## 1. Requisitos

| Requisito | Versión mínima | Para qué |
|---|---|---|
| Docker Desktop (incluye Docker Compose v2) | 4.30 o superior | Construir y ejecutar los contenedores |
| Git | 2.40 o superior | Descargar el código |
| Navegador | Chrome, Edge o Firefox actualizados | Usar la aplicación |
| Memoria libre | 2 GB | Tomcat y PostgreSQL |
| Puertos libres | 8080 y 5432 | Aplicación web y base de datos |

En Windows, Docker Desktop necesita WSL 2 activado; el propio instalador lo ofrece.

## 2. Instalación paso a paso

1. **Abrir Docker Desktop** y esperar a que diga *Engine running*.

2. **Descargar el código** de la rama `main` (versión estable):

   ```bash
   git clone https://github.com/jhosnit/Pedidos-cafeteria-universitaria.git
   cd Pedidos-cafeteria-universitaria
   ```

3. **Construir y levantar** la aplicación:

   ```bash
   docker compose up --build
   ```

   La primera vez tarda unos minutos porque descarga las imágenes de Maven, Tomcat y PostgreSQL y compila el proyecto. Está lista cuando en la consola aparece una línea parecida a `Server startup in [...] milliseconds`.

4. **Abrir** <http://localhost:8080> en el navegador. Debe aparecer la pantalla de inicio de sesión.

Para dejarla corriendo en segundo plano se puede usar `docker compose up --build -d`.

## 3. Cuentas de demostración

La primera vez que se crea la base se ejecutan los scripts de `db/init/`: el esquema y los datos de demostración (tres cuentas y nueve productos, uno de ellos no disponible).

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin.cafeteria@epn.edu.ec | Admin2026! |
| Personal de cafetería | carlos.mena@epn.edu.ec | Personal2026! |
| Cliente | ana.torres@epn.edu.ec | Cliente2026! |

Los clientes nuevos se registran solos desde «Crea una aquí» en el login. Las cuentas de personal las crea el administrador.

## 4. Configuración

Los valores se definen en `docker-compose.yml`:

| Variable | Servicio | Valor | Descripción |
|---|---|---|---|
| `POSTGRES_DB` | db | `cafeteria` | Nombre de la base |
| `POSTGRES_USER` | db | `cafeteria` | Usuario de la base |
| `POSTGRES_PASSWORD` | db | (ver archivo) | Clave del usuario; cambiarla si el sistema se usa fuera del ambiente de pruebas |
| `DB_URL` | web | `jdbc:postgresql://db:5432/cafeteria` | Conexión desde la aplicación |
| `DB_USER`, `DB_PASSWORD` | web | iguales a los de `db` | Credenciales que usa la aplicación |
| `TZ` | ambos | `America/Guayaquil` | Zona horaria para la fecha de los pedidos |

Si se cambia la clave, hay que cambiarla en los dos servicios y recrear la base (sección 5).

## 5. Operación

| Acción | Comando |
|---|---|
| Detener sin perder datos | `docker compose down` |
| Volver a levantar | `docker compose up` |
| Ver los registros del servidor | `docker compose logs -f web` |
| Empezar con la base limpia (borra todos los pedidos y cuentas nuevas) | `docker compose down -v` y luego `docker compose up --build` |
| Actualizar a una versión nueva del código | `git pull` y luego `docker compose up --build` |
| Respaldar la base | `docker compose exec db pg_dump -U cafeteria cafeteria > respaldo.sql` |
| Restaurar un respaldo | `docker compose exec -T db psql -U cafeteria cafeteria < respaldo.sql` |

Los datos se guardan en el volumen `dbdata`, por eso sobreviven a `docker compose down` y a reinicios del equipo.

## 6. Ejecutar las pruebas (opcional)

```bash
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-25 mvn test
```

En PowerShell `${PWD}` funciona igual. Al final debe mostrarse `BUILD SUCCESS`.

## 7. Problemas frecuentes

| Problema | Causa probable | Solución |
|---|---|---|
| `port is already allocated` en 8080 o 5432 | Otro programa usa el puerto (otro Tomcat o un PostgreSQL local) | Cerrar ese programa o cambiar el puerto de la izquierda en `docker-compose.yml`, por ejemplo `"8081:8080"` |
| `Cannot connect to the Docker daemon` | Docker Desktop no está abierto | Abrir Docker Desktop y esperar a que el motor esté en ejecución |
| La página muestra «Algo salió mal» al iniciar | La base todavía se está creando | Esperar unos segundos y recargar; revisar `docker compose logs db` |
| Las cuentas de demostración no funcionan | El volumen se creó con una versión anterior de los scripts | `docker compose down -v` y volver a levantar |
| Las tildes se ven mal | Archivos guardados con otra codificación | Todos los archivos del proyecto deben estar en UTF-8 |
