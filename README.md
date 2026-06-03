* [Release process](#release-process)
* [Cross cutting concerns](#cross-cutting-concerns)
    * [Exception handling with problem details response](#exception-handling)
  * [Testing](#testing)
      * [@FixedClock](#fixedClock)
    * [LogbackVerifierExtension](#logbackVerifierExtension)
    * [ClearCollections](#clearCollections)
  * [Logging](#logging)
  * [Observability](#observability)
* [Changelog:](#changelog)
    * [13.04.2025](#13042025)
        * [Dropped openapi parent support](#dropped-openapi-parent-support)
        * [Dependencies update](#dependencies-update)
        * [CI:](#ci)
    * [09.11.2024](#09112024)
        * [Major refactoring of modules](#major-refactoring-of-modules)
        * [Changed test execution and reporting](#changed-test-execution-and-reporting)
      * [CI:](#ci-1)
    * [28.01.2024:](#28012024)
    * [18.01.2024:](#18012024)
    * [08.06.2023:](#08062023)
    * [10.04.2023:](#10042023)
    * [08.04.2023:](#08042023)
    * [6.07.2022:](#6072022)
    * [15.12.2021:](#15122021)
    * [25.07.2021:](#25072021)
    * [31.07.2020:](#31072020)

### Release process

1. Create branch release/x.y.z
2. Update versions to x.y.z
3. Save SHA of the commit .
4. Merge to master
5. Tag commit with saved SHA
6. Push to origin to trigger release workflow.

All steps are covered in prepare-release.sh and tag-release.sh

- sh prepare-release.sh 0.4.2 0.4.3
- merge release/0.4.2 to master
- sh tag-release.sh 0.4.2

### Cross cutting concerns

#### Exception handling

> [!IMPORTANT]
> Required property: \
> spring.mvc.problemdetails.enabled: false

Generic exceptions are provided. If they are thrown, application should return response compatible with
ProblemDetail [RFC 7807 standard](https://datatracker.ietf.org/doc/html/rfc7807).

Throw or extend those RuntimeExceptions:

| Exception                    | Response status |
|------------------------------|-----------------|
| BadRequestException          | 400             |
| ResourceCreationException    | 400             |
| ResourceNotFoundException    | 404             |
| ResourceConflictException    | 409             |
| InternalServerErrorException | 500             |

##### Problem details

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Test message",
  "instance": "/exceptions/bad-request",
  "timestamp": "2024-12-03T10:31:09.840944Z"
}
```

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Constraint Violation",
  "instance": "/exceptions/validation/method-argument-not-valid",
  "violations": [
    {
      "field": "age",
      "message": "must be greater than or equal to 18"
    }
  ],
  "timestamp": "2024-12-03T12:55:48.119636Z"
}
```

#### Testing

###### FixedClock

You can use @FixedClock annotation to mock clock in tests. Example usage:

```java

@FixedClock
@TestToolsIntegrationTest
class FixedClockIT {

    @Autowired
    Clock clock;

    @Test
    void givenClassLevelAnnotation_whenUseClock_thenReturnMockedDateTime() {
        // Given
        final var now = Instant.now(clock);
        Assertions.assertThat(now).isEqualTo(Instant.parse(FixedClock.DEFAULT_LOCAL_INSTANT));
    }

    @FixedClock("2027-01-10T10:00:00Z")
    @Test
    void givenMethodLevelAnnotation_whenUseClock_thenOverrideMockedDateTime() {
        // Given
        final var now = Instant.now(clock);
        Assertions.assertThat(now).isEqualTo(Instant.parse("2027-01-10T10:00:00Z"));
    }
}
```

###### LogbackVerifierExtension

Extend your test class with @ExtendWith(LogbackVerifierExtension.class), to enable support for customized log checks.
Refer to LogVerifier class.

```java

@ExtendWith(LogbackVerifierExtension.class)
class Test {
    @Test
    void test(LogVerifier logVerifier) {
        whatever();
        logVerifier.containsInfo("desired log");
    }
}
```

###### ClearCollections

```java

@ClearCollections("accounts")
class TestIT {
    @Test
    void test() {
        // ...
    }
}
```

Should produce following log message after each test
"Clearing up [accounts] collections. Removed X entries"

```java

@ClearCollections(value = "accounts", drop = true)
class TestIT {
    @Test
    void test() {
        // ...
    }
}
```

Should produce following log message after each test
`Dropped collection accounts`

###### MetricsVerifierExtension

```java

@ExtendWith(MetricsVerifierExtension.class)
class MetricsIT {
    @Test
    void shouldVerifyMetricValue(MetricsVerifier metricsVerifier) {
        meterRegistry.counter("dummy.metric").increment();
        meterRegistry.counter("dummy.metric").increment();

        metricsVerifier.assertMetric("dummy.metric")
                .hasValueGreaterThan(1.0)
                .hasValueGreaterThanOrEqualTo(2.0)
                .hasValue(2.0);
    }
}
```

**TraceVerifier** - Validate distributed tracing

```java

@ExtendWith(TraceVerifierExtension.class)
class TracingIT {
    @Test
    void testTracing(TraceVerifier verifier) {
        String traceId = verifier.getCurrentTraceId();
        verifier.assertTraceIdValid(traceId);

        // Verify response has trace headers
        ResponseEntity<?> response = restTemplate.getForEntity("/api", String.class);
        verifier.assertResponseHasTraceHeaders(response);

        // Verify trace propagation
        String parentTraceId = "aaaabbbbccccdddd1111222233334444";
        String childTraceId = response.getHeaders().getFirst("Trace-Id");
        verifier.assertTracePropagated(parentTraceId, childTraceId);
    }
}
```

#### Logging

Application automatically logs information around requests.

for instance:

```
Incoming: method: GET, uri: /api/logs, queryString: ?queryParam=testParam, remoteIp: 127.0.0.1, userAgent: null
[...]
Request completed. Status: 200, duration: 2ms.
```

It also automatically logs collection size if that's the response body.

```
Collection returned on endpoint: /api/logs/withCollection, size: 2, status: 200
```

#### Observability

- Automatic common tags on all metrics
- Trace response headers (Trace-Id, Span-Id) + W3C propagation
- Resource attributes (service.name, namespace, environment)

**Configuration:** `chassis.observability.*`

**Full documentation:** [chassis-tools/docs/OBSERVABILITY.md](chassis-tools/docs/OBSERVABILITY.md)

### Changelog:

##### 03.06.2026

* Added `spring-boot-configuration-processor` to the `maven-compiler-plugin` `annotationProcessorPaths` in both the
  root `chassis` pom and `chassis-parent`. Once `annotationProcessorPaths` is declared, Maven discovers processors
  only from that list, so the Spring Boot processor was being suppressed and no `spring-configuration-metadata.json`
  was generated for `@ConfigurationProperties` classes (e.g. `chassis.observability.*`). Metadata is now produced.
* Upgraded Spring Boot 4.0.3 -> 4.0.6 (root pom, `chassis-bom`, `chassis-parent` property and
  `spring-boot-starter-parent`); kept Spring Cloud 2025.1.1 (latest patch on the SB-4.0-compatible line).
* Aligned versions to the Spring Boot 4.0.6 BOM by removing redundant `chassis-bom` pins it already manages at the
  same version (Lombok, junit:junit, assertj-core, testcontainers-mongodb, h2) and dropping the local `lombok.version`
  property in `chassis-parent`.
* JUnit now follows the SB-managed junit-bom 6.0.3 (removed the `chassis-tools-test` junit-bom 5.14.4 override) and
  archunit-junit5 is unified at 1.4.1 (removed the divergent 1.4.0 pin). Kept intentionally-newer pins (mockito-core
  5.21.0, maven-surefire/failsafe 3.5.6, git-commit-id 9.2.0) to avoid downgrades.

##### 02.06.2026

* Migrated to Java 25 (Spring Boot 4.0.3, Spring Cloud 2025.1.1). CI runners and the `Dockerfile` base image
  (`eclipse-temurin:21.0.5_11-jre` -> `eclipse-temurin:25.0.3_9-jre`) updated to JDK 25.
* Bumped dependencies and plugins for JDK 25 compatibility: spring-cloud-contract-maven-plugin 4.3.1 -> 5.0.2,
  maven-surefire/maven-surefire-report/maven-failsafe 3.5.4 -> 3.5.6, maven-release-plugin 3.1.1 -> 3.3.1,
  git-commit-id-maven-plugin 9.0.2 -> 9.2.0, maven-jxr-plugin 3.3.2 -> 3.6.0, Lombok 1.18.42 -> 1.18.46,
  Guava 33.5.0-jre -> 33.6.0-jre, junit-bom 5.12.2 -> 5.14.4, embedded-mongo 4.11.0 -> 4.22.0,
  testcontainers-mongodb 2.0.3 -> 2.0.5.
* Declared Lombok on the maven-compiler-plugin `annotationProcessorPaths` in both the root `chassis` pom and
  `chassis-parent`. JDK 23+ no longer runs annotation processors discovered only from the classpath, so without this
  `@Slf4j` / `@RequiredArgsConstructor` produced no `log` field or constructor.

##### 04.05.2026

* Extracted observability into new `chassis-starter-observability` module with minimal dependencies (actuator +
  micrometer + opentelemetry). Removed unused `DistributedTracingProperties`.

##### 01.05.2026

* Added `ErrorHandlingDemoController` and slice IT in `chassis-test-app` showcasing every failure mode handled by
  `chassis-starter-errorhandling`.

##### 30.04.2026

* Extracted errorhandling into new `chassis-starter-errorhandling` module with minimal dependencies (web + validation
  only).

##### 28.02.2026

* Added observability test utilities in `chassis-tools-test`:
    * Added `MetricsVerifierExtension`
    * Added `TraceVerifierExtension`

* Automatic export of metric/logs/traces with agent
    * **Metrics**: Automatic common tags and resource attributes
    * **Tracing**: Response header exposure (Trace-Id, Span-Id), W3C propagation, sampling controls
    * **Auto Instrumentation**: Spring MVC, RestTemplate, WebClient (via OpenTelemetry Java Agent)

##### 23.02.2026

* Springboot 3.5.10 -> 4.0.3
* Spring Cloud 2025.0.1 -> 2025.1.1
* Undertow -> Tomcat

##### 27.01.2026

* Springboot 3.4.4 -> 3.5.10

##### 30.10.2025

* Added support for logging around requests
* Dropped support for previous logging configuration under `budget.chassis.http-logging.*`.

##### 28.10.2025

Added @ClearCollections test annotation and docker-compose support for test app only.

##### 18.10.2025

Added @FixedClock and LogbackVerifierExtension to test fixtures.

##### 13.04.2025

###### Dropped openapi parent support

* It's not necessary for now, maybe it will come back in the future.

###### Dependencies update

* SpringBoot 3.4.4
* Java 21

###### CI

* updated to java 21 and mvn wrapper to 3.9.9
* Dockerized test app

##### 03.12.2024

* Reimplemented first cross cutting concern [Exception handling with problem detail response](#exception-handling)

##### 09.11.2024

###### Major refactoring of modules

* chassis-dependencies -> all compile dependencies for microservice
* chassis-bom -> bill of materials
* renamed module chassis-test-tools -> chassis-tools-test
* chassis-test-app -> module with application for tests.
* chassis-tools -> single module with cross cutting concern functionalities

###### Changed test execution and reporting

Unit tests and integration tests are executed separately.
Integration tests need to end with *IT suffix.

###### CI:

Removed external microservices verification from workflow

##### 28.01.2024:

- Dependency management for lombok in bom.
- Jacoco verification during verify stage instead of test
- added @ControllerTest annotation to use in tests

##### 18.01.2024:

- Springboot 3.1.0 -> 3.2.1
- junit-5 5.9.3 -> 5.10.1
- updated mockito and assertj
- spring-cloud-dependencies 2022.0.3 -> 2023.0.0
- cucumber 7.6.0 -> 7.15.0
- Decommission password-management support in CI/CD
##### 08.06.2023:
- Springboot 3.0.4 -> 3.1.0
- junit-5 5.9.2 -> 5.9.3
- spring-cloud-dependencies 2022.0.1 -> 2022.0.3
##### 10.04.2023:
- Dependency management for test containers.
- Added missing auto configurations
- Removed dummy RestTools.java
##### 08.04.2023:
- Springboot 2.7.8 -> 3.0.4
- Added @ChassisApplication annotation. Should replace @SpringBootApplication
- Renamed chassis-settings module to chassis-bom (breaking change)
- Renamed Maven properties: chassis.dependencies.springboot.version -> chassis.springboot.version, chassis.dependencies.springcloudtrain.version -> chassis.springcloudtrain.version, chassis.dependencies.springcloud.version -> chassis.springcloud.version, chassis.dependencies.openapi.version -> chassis.openapi.version (breaking change)
- Dropped distributed tracing support.
- Upgrade of various dependencies
- Replaced embedded mongo dependency (as in spring migration guide)
```xml
<dependency>
  <groupId>de.flapdoodle.embed</groupId>
  <artifactId>de.flapdoodle.embed.mongo</artifactId>
  <version>3.5.1</version>
</dependency>
 ```
->
```xml
<dependency>
  <groupId>de.flapdoodle.embed</groupId>
  <artifactId>de.flapdoodle.embed.mongo.spring30x</artifactId>
  <version>4.6.2</version>
</dependency>
 ```
##### 6.07.2022:
- Springboot 2.4.9 -> 2.7.1 (one by one).
- Many dependencies upgrade.
- New parent for API modules.

##### 15.12.2021:
 - Extended actuator info endpoint. Example:

```json
{
    "git": {
        "branch": "develop",
        "commit": {
            "id": "ac9e2d7",
           "time": "2021-09-16T13:00:18Z"
        }
     },
    "build": {
        "springbootVersion": "2.4.9",
        "version": "0.7.0-SNAPSHOT",
        "artifact": "budget-management",
        "name": "budget-management",
        "time": "2021-12-15T18:57:44.209Z",
        "chassisVersion": "develop",
        "group": "pl.konradboniecki.budget"
    }
}
```
##### 25.07.2021:
 - Springboot 2.4.9
##### 31.07.2020:
 - Springboot 2.3.2.RELEASE
