FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/alliBE-0.0.1-SNAPSHOT.jar backend.jar

ENV TZ=Asia/Seoul
ENV SERVER_PORT=8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Xmx1024m", \
           "-Dserver.port=${SERVER_PORT}", "backend.jar"]

EXPOSE ${SERVER_PORT}