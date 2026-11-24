# Pedidos cafetería universitaria

Aplicación web para que estudiantes y docentes consulten el menú de la cafetería, hagan pedidos anticipados y los retiren con un código, y para que el personal atienda esos pedidos hasta la entrega.

Proyecto 1B de Construcción y Evolución de Software · Release 1.0 (27 de noviembre de 2026).

## Funcionalidades

- **Cliente:** registro de cuenta, consulta del menú, selección de productos y cantidades, revisión del total, confirmación con código de retiro (formato `AB-01`) y consulta del estado de sus pedidos.
- **Personal de cafetería:** panel de pedidos con filtro por estado, cambio de estado (Pendiente → En preparación → Listo para retirar), entrega con el código del cliente y gestión de productos (registro, edición y disponibilidad).
- **Administrador:** registro de cuentas del personal.

## Tecnologías

Java 25, Jakarta Servlet 6 + JSP/JSTL sobre Tomcat 10.1, PostgreSQL 17 con JDBC, BCrypt para contraseñas, JUnit 5 para pruebas y Docker Compose para levantar todo.

## Cómo ejecutarlo

Requisitos: Docker Desktop (con Docker Compose) y Git.

```bash
git clone https://github.com/jhosnit/Pedidos-cafeteria-universitaria.git
cd Pedidos-cafeteria-universitaria
docker compose up --build
```

Luego abrir <http://localhost:8080>.

Para empezar de cero (borra la base de datos y vuelve a cargar los datos iniciales):

```bash
docker compose down -v
docker compose up --build
```

### Cuentas de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin.cafeteria@epn.edu.ec | Admin2026! |
| Personal | carlos.mena@epn.edu.ec | Personal2026! |
| Cliente | ana.torres@epn.edu.ec | Cliente2026! |

## Pruebas

Con Maven instalado:

```bash
mvn test
```

Sin Maven, usando Docker:

```bash
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-25 mvn test
```

## Estructura

```
src/main/java/ec/edu/epn/cafeteria/
  modelo/       entidades del dominio (Pedido, Producto, Seleccion...)
  dao/          acceso a PostgreSQL con JDBC
  servicio/     reglas de negocio y validaciones
  controlador/  servlets y filtro de acceso por rol
src/main/webapp/
  assets/       hoja de estilos (sistema de diseño)
  WEB-INF/vistas/  páginas JSP por rol
src/test/java/  pruebas unitarias con JUnit 5
db/init/        esquema y datos iniciales de la base
```

## Documentación

Requisitos, diseño, ambiente de desarrollo, documentación técnica, análisis estático, pruebas y manuales están en la rama [`documentacion`](https://github.com/jhosnit/Pedidos-cafeteria-universitaria/tree/documentacion).

## Equipo

Equipo 2: Sebastián Guerrero, Jhostin Cadena, Melany, Franciss.
