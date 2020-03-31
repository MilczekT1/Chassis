FROM openjdk:11.0.3-jdk-stretch
HEALTHCHECK --start-period=15s --interval=5s --timeout=5s --retries=15 CMD curl -f http://localhost:8080/actuator/health
ADD budget-ssc.p12 /
