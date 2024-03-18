FROM eclipse-temurin:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
# Converte o nome do repositório para minúsculas
ARG REPOSITORIO=HelpDesk
RUN REPOSITORIO_MINUSCULO=$(echo "$REPOSITORIO" | tr '[:upper:]' '[:lower:]')
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]