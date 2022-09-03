FROM openjdk:17.0.2-jdk
HEALTHCHECK --start-period=15s --interval=5s --timeout=5s --retries=15 CMD curl -f http://localhost:8080/actuator/health
