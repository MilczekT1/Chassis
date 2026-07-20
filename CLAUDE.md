# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Environment

- **Maven wrapper**: Always use `./mvnw` (never `mvn`)

## Build Commands

```bash
# Build all modules (skip tests)
./mvnw clean install -DskipTests

# Run unit tests only (excludes *IT.java)
./mvnw test

# Run unit + integration tests
./mvnw verify

# Run a single test class
./mvnw test -pl chassis-tools -Dtest=MyTestClass

# Run a single integration test class
./mvnw verify -pl chassis-tools -Dit.test=MyTestIT

# Build specific module only
./mvnw install -pl chassis-tools -am
```

## Test Conventions

- **Unit tests**: Any class not ending in `IT` — run by maven-surefire-plugin
- **Integration tests**: Classes ending in `*IT` — run by maven-failsafe-plugin
- Coverage reports are merged from both into `target/coverage-reports/merged-test-coverage-report/`
- JaCoCo enforces 75% line/branch coverage (configurable per module via `jacoco.lineCoverage.minimum`,
  `jacoco.branches.minimum`, `jacoco.classes.maxMissed`)

## Module Architecture

This is a Spring Boot chassis library published to GitHub Packages. It provides reusable cross-cutting concerns as
Spring Boot auto-configurations for microservices.

| Module                 | Purpose                                                                                                                               |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `chassis-bom`          | Bill of Materials — dependency version management                                                                                     |
| `chassis-parent`       | Parent POM inheriting from `spring-boot-starter-parent`; defines build plugins (JaCoCo, Surefire, Failsafe)                           |
| `chassis-tools`        | Core library — Spring Boot auto-configured cross-cutting concerns (exception handling, logging, observability, security)              |
| `chassis-tools-test`   | Test utilities — `@FixedClock`, `LogbackVerifierExtension`, `@ClearCollections`, `MetricsVerifierExtension`, `TraceVerifierExtension` |
| `chassis-dependencies` | Aggregator JAR — single dependency for microservices to include all chassis modules                                                   |
| `chassis-test-app`     | Spring Boot app used for integration/regression testing of chassis features                                                           |

**Dependency hierarchy**: microservices add `chassis-dependencies` (which pulls in `chassis-tools`) and set
`chassis-parent` as their parent POM.

## Auto-Configurations in chassis-tools

Registered in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

- `ChassisExceptionHandlingAutoConfiguration` — Global `@RestControllerAdvice` mapping chassis exceptions to RFC 7807
  ProblemDetail responses. Requires `spring.mvc.problemdetails.enabled: false` in consuming apps.
- `ChassisLoggingAutoConfiguration` — Interceptor that logs incoming requests and response status/duration; logs
  collection sizes on list endpoints
- `ChassisSecurityAutoConfiguration` — Security configuration
- `ChassisObservabilityAutoConfiguration` — Micrometer/OpenTelemetry setup; configured via `chassis.observability.*`

## Exception Hierarchy

Throw or extend these from `chassis-tools`:

| Exception                      | HTTP Status |
|--------------------------------|-------------|
| `BadRequestException`          | 400         |
| `ResourceCreationException`    | 400         |
| `ResourceNotFoundException`    | 404         |
| `ResourceConflictException`    | 409         |
| `InternalServerErrorException` | 500         |

## Test Utilities (chassis-tools-test)

- `@FixedClock` — annotation (class or method level) to freeze `Clock` bean; default instant:
  `FixedClock.DEFAULT_LOCAL_INSTANT`
- `@ExtendWith(LogbackVerifierExtension.class)` + `LogVerifier` param — assert log output in tests
- `@ClearCollections("collectionName")` — clears MongoDB collection after each test; `drop = true` drops instead of
  clears
- `@ExtendWith(MetricsVerifierExtension.class)` + `MetricsVerifier` param — assert Micrometer metric values
- `@ExtendWith(TraceVerifierExtension.class)` + `TraceVerifier` param — validate distributed trace IDs and response
  headers
- `@ControllerTest` — slice test annotation for controller layer tests

In `chassis-test-app`, integration tests use `@TestAppIntegrationTest` (a composed annotation activating profile
`itest`, all three verifier extensions, Testcontainers, and MockMvc).

## Observability

Configured via `chassis.observability.*` in `application.yaml`. OTLP export uses the OpenTelemetry Java Agent with
standard `OTEL_*` environment variables. See `chassis-test-app/OBSERVABILITY.md` for full configuration reference.

## Changelog Rule

When completing any meaningful change (new feature, dependency update, bug fix, refactor), add a dated entry to the
`### Changelog:` section in `README.md`. Format:

```
##### DD.MM.YYYY

* Short description of what changed
```

Keep entries concise — one bullet per distinct change. Add the entry at the top of the changelog (most recent first).

## Release Process

Releases run in CI via the **Release** GitHub Actions workflow (`workflow_dispatch`) — there
are no local scripts. Only allowlisted users (hardcoded `RELEASE_ALLOWED_ACTORS` in
`release.yml`) may trigger it. Trigger **Actions → Release → Run workflow** and provide
`releaseVersion` (e.g. `0.5.1`) and `nextVersion` (e.g. `0.5.2`, no `-SNAPSHOT`). The workflow
creates a `release/v<version>` branch, sets and commits + tags the release version (crediting
the triggerer as co-author), publishes all modules to GitHub Packages, bumps to
`<nextVersion>-SNAPSHOT`, creates the GitHub Release, opens a PR to `master`, and enables
auto-merge (which lands once the required build check passes). The release PR is created via a
GitHub App (secrets `RELEASE_APP_ID` / `RELEASE_APP_PRIVATE_KEY`) so it triggers CI.

`publish-java.yml` ("Publish artifact (manual re-publish)") is a manual-only escape hatch for
re-publishing an already-released version. Published to GitHub Packages
(`maven.pkg.github.com/milczekt1/Chassis`); requires GitHub credentials in Maven settings.
