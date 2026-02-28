FROM eclipse-temurin:21.0.5_11-jre
HEALTHCHECK --start-period=15s --interval=5s --timeout=5s --retries=15 CMD curl -f http://localhost:8080/actuator/health
ARG ARTIFACT=chassis-test-app-*.jar
ADD /chassis-test-app/target/$ARTIFACT app.jar
ADD /chassis-test-app/agent/grafana-opentelemetry-java.jar /agent/grafana-opentelemetry-java.jar

# Default values (for local development with OTLP collector):
# - OTEL_EXPORTER_OTLP_ENDPOINT: http://localhost:4318
#
# For production (Grafana Cloud), set these environment variables:
#   docker run -e OTEL_EXPORTER_OTLP_ENDPOINT=https://otlp-gateway-prod-{region}.grafana.net/otlp \
#              -e OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic <base64(username:password)>" \
#              -e OTEL_RESOURCE_ATTRIBUTES="service.namespace=my-app,deployment.environment=production" \
#              your-image:tag

ENV OTEL_RESOURCE_ATTRIBUTES="service.namespace=chassis-test-app,deployment.environment=docker"

ENTRYPOINT ["java", \
    "-javaagent:/agent/grafana-opentelemetry-java.jar", \
    "-Dotel.service.name=chassis-test-app", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar" \
]
