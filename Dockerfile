FROM eclipse-temurin:21.0.5_11-jre
HEALTHCHECK --start-period=15s --interval=5s --timeout=5s --retries=15 CMD curl -f http://localhost:8080/actuator/health
ARG ARTIFACT=chassis-test-app-*.jar
ADD /chassis-test-app/target/$ARTIFACT app.jar
ENTRYPOINT ["java", "-jar", \
    "-Djava.security.egd=file:/dev/./urandom ", "app.jar", \
    "--spring.profiles.active=default" \
]
