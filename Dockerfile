# Usa una imagen de base de OpenJDK con JDK 17
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR generado por Gradle
COPY ./build/libs/cemexsd.jar /app/cemexsd.jar

# Expone el puerto 8080 (ajusta si tu app usa otro puerto)
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "/app/cemexsd.jar"]
./gradlew build
