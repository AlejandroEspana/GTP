# Etapa 1: Compilación con Maven
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Descarga de dependencias previas para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compilación del código fuente
COPY src ./src
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen final de ejecución ligera
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear directorio para persistencia de archivos subidos
RUN mkdir -p /app/uploads

# Copiar el artefacto ejecutable generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
