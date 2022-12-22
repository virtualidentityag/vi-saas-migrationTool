FROM adoptopenjdk/openjdk11
VOLUME ["/tmp","/log"]
ARG JAR_FILE
COPY MigrationToolService.jar app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]