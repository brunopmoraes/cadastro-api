# Usar uma imagem base do OpenJDK
FROM openjdk:21-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o arquivo .jar da sua aplicação para o contêiner
COPY target/cadastro-api-1.0-SNAPSHOT.jar app.jar

# Expor a porta em que a aplicação irá rodar
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]