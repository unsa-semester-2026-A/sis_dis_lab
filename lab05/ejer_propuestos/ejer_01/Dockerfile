FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY src/ .

# Compilar todos los archivos .java
RUN javac rmi/*.java

# Comando por defecto (será sobrescrito en docker-compose)
CMD ["java", "rmi.Server"]
