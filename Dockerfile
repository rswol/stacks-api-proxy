FROM openjdk:17

COPY stacks-api-proxy-1.0-SNAPSHOT.jar /opt/stacks-api-proxy/

WORKDIR /opt/stacks-api-proxy/
CMD ["java", "-jar", "stacks-api-proxy-1.0-SNAPSHOT.jar", "server", "/opt/stacks-api-proxy/config/docker.yml"]
