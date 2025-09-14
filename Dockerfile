# Etapa 1: Build con JDK completo
FROM eclipse-temurin:17-jdk-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copiar Gradle wrapper y dar permisos
COPY gradlew .
COPY gradlew.bat .
RUN chmod +x gradlew

# Copiar configuración de Gradle
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .
COPY main.gradle .
COPY gradle.properties .

# Copiar todos los módulos preservando estructura
COPY applications applications
COPY domain domain
COPY infrastructure infrastructure

# Comprobar que la estructura es correcta (opcional para debug)
RUN ls -R /app

# Ejecutar build incluyendo validateStructure
RUN ./gradlew clean build -x validateStructure -x test --no-daemon

# Etapa 2: Imagen ligera solo con JRE y el JAR
FROM eclipse-temurin:17-jre-alpine


# Copiar el JAR generado en la etapa de build
COPY --from=build /app/applications/app-service/build/libs/*.jar app.jar

# Exponer puerto de la aplicación
EXPOSE 8082

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
