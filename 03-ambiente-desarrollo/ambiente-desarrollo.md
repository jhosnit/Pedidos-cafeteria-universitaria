# Ambiente de desarrollo

**Release:** 1.0  
**Versión del documento:** 1.0  

## 1. Herramientas seleccionadas

| Categoría | Herramienta | Versión | Justificación técnica |
|---|---|---|---|
| **Lenguaje** | Java (Eclipse Temurin) | 25 | Versión estándar del proyecto base (`maven.compiler.release=25`). |
| **Construcción** | Apache Maven | 3.9 | Gestión de dependencias, empaquetado del archivo `.war` y ejecución de pruebas. |
| **Servidor web** | Apache Tomcat | 10.1 | Servidor de aplicaciones compatible con Jakarta Servlet 6.0 y JSP. |
| **Base de datos** | PostgreSQL | 17 | Motor relacional con soporte nativo de transacciones ACID y restricciones de unicidad (RNF-05, RNF-06). |
| **Acceso a datos** | JDBC (`org.postgresql:postgresql`) | 42.7.5 | Acceso directo a datos mediante JDBC sin sobrecarga de ORM. |
| **Seguridad** | BCrypt (`at.favre.lib:bcrypt`) | 0.10.2 | Cifrado seguro de contraseñas con sal (*hash*) en cumplimiento de RNF-02. |
| **Pruebas** | JUnit Jupiter | 5.10.2 | Pruebas unitarias de las reglas de negocio en modelo y servicio. |
| **Documentación técnica** | Javadoc (incluido en el JDK) con `maven-javadoc-plugin` | 3.12.0 | Genera la documentación técnica del código fuente en HTML a partir de los comentarios Javadoc escritos en clases y métodos. |
| **Análisis estático** | SonarQube Community Build (imagen `sonarqube:community`) con SonarScanner for Maven (`sonar-maven-plugin`) | Community Build 26.x; plugin 5.7.0.6970 | Detecta errores, vulnerabilidades y malas prácticas en el código sin ejecutarlo, y genera los resultados para el reporte de análisis estático. La versión 26.3 en adelante soporta Java 25. |
| **Contenedores** | Docker y Docker Compose | Docker Desktop | Despliegue estandarizado de la aplicación y la base de datos en contenedores aislados (RNF-07). |
| **Control de versiones** | Git + GitHub | — | Trazabilidad del proyecto mediante ramas, Issues y Pull Requests. |
| **IDE recomendado** | Visual Studio Code | — | Editor común del equipo de desarrollo. |

## 2. Requisitos previos de instalación

Para ejecutar y desarrollar el proyecto de forma local se requiere:

* **Docker Desktop** (con Docker Compose v2 habilitado y en ejecución).
* **Git** (para clonar y gestionar el repositorio).

## 3. Comandos de desarrollo y ejecución

| Acción | Comando | Descripción |
|---|---|---|
| **Levantar todo el entorno** | `docker compose up --build` | Compila el código, levanta PostgreSQL 17 y despliega la aplicación en Tomcat 10.1. |
| **Acceder a la aplicación web** | [http://localhost:8080](http://localhost:8080) | Dirección local de acceso a la interfaz web. |
| **Reiniciar con base limpia** | `docker compose down -v && docker compose up --build` | Elimina el volumen `dbdata` y vuelve a ejecutar los scripts de inicialización de `db/init/`. |
| **Detener los servicios** | `docker compose down` | Detiene los contenedores preservando los datos almacenados en el volumen. |
| **Ejecutar pruebas unitarias** | `mvn test` | Corre la suite de pruebas automatizadas con JUnit Jupiter. |
| **Empaquetar el artefacto (.war)** | `mvn clean package` | Genera el archivo empaquetado listo para producción en `target/`. |

## 4. Variables de entorno y configuración

La conexión entre el contenedor de la aplicación web y la base de datos PostgreSQL se configura a través de variables de entorno definidas en `docker-compose.yml`:

| Variable | Valor predeterminado | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://db:5432/cafeteria` | Cadena de conexión JDBC hacia el contenedor `db`. |
| `DB_USER` | `cafeteria` | Usuario con permisos sobre la base de datos. |
| `DB_PASSWORD` | `cafeteria_pass` | Clave de acceso para el entorno de desarrollo y pruebas. |
