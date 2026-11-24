# Etapa 1: compilar el .war con Maven (las pruebas se corren aparte con mvn test)
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# Etapa 2: desplegar en Tomcat como aplicacion raiz
FROM tomcat:10.1-jdk25-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/cafeteria.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
