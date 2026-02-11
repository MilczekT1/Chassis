# Chassis Observability

Chassis provides built-in observability with OpenTelemetry, sending metrics, logs, and traces to OTLP-compatible
backends like Grafana Cloud.

## Zero Configuration for Local Development

**Just run your application** - observability works out-of-the-box with sensible defaults:

- ✅ Metrics export to `http://localhost:4318/v1/metrics` every 60 seconds
- ✅ Traces export to `http://localhost:4318/v1/traces` in real-time
- ✅ Logs export to `http://localhost:4318/v1/logs` in batches
- ✅ Service namespace defaults to your `spring.application.name`
- ✅ Deployment environment defaults to `local`

## Configuration for Production (Grafana Cloud)

The application uses the **Grafana OpenTelemetry Java Agent** which reads configuration from `OTEL_*` environment
variables.

### Required Environment Variables

Set these 3 environment variables:

```bash
export OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-eu-north-0.grafana.net/otlp"
export OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic <base64-encoded-credentials>"
export OTEL_RESOURCE_ATTRIBUTES="service.namespace=my-app,deployment.environment=my-environment"
```

Get your credentials from: **Grafana Cloud** → **Connections** → **Add new connection** → **Hosted OTLP**

## Running Locally

### IntelliJ Run Configuration

1. Open Run Configuration: `ChassisTestApp`
2. Add environment variables (only if connecting to Grafana Cloud):
    - `OTEL_EXPORTER_OTLP_ENDPOINT` = `https://otlp-gateway-prod-{region}.grafana.net/otlp`
    - `OTEL_EXPORTER_OTLP_HEADERS` = `Authorization=Basic <base64-credentials>`
    - `OTEL_RESOURCE_ATTRIBUTES` = `service.namespace=my-app,deployment.environment=production`
3. Ensure VM parameters include: `-Dotel.service.name=chassis-test-app`
4. Run the application

### Run with Grafana Cloud

```bash
docker run -p 8080:8080 \
  -e OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-eu-north-0.grafana.net/otlp" \
  -e OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic $(echo -n 'username:password' | base64)" \
  -e OTEL_RESOURCE_ATTRIBUTES="service.namespace=my-app,deployment.environment=production" \
  chassis-test-app:latest
```

## Configuration Reference

### Environment Variables

The OpenTelemetry Java Agent reads these standard `OTEL_*` environment variables:

| Variable                      | Default                 | Description                                                       |
|-------------------------------|-------------------------|-------------------------------------------------------------------|
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4318` | Base OTLP endpoint URL                                            |
| `OTEL_EXPORTER_OTLP_HEADERS`  | _(none)_                | HTTP headers for authentication (e.g., `Authorization=Basic ...`) |
| `OTEL_RESOURCE_ATTRIBUTES`    | _(none)_                | Resource attributes as comma-separated key=value pairs            |

### System Properties

| Property              | Required | Description                                        |
|-----------------------|----------|----------------------------------------------------|
| `-Dotel.service.name` | Yes      | Service name identifier (e.g., `chassis-test-app`) |

### application.yaml Configuration (Optional)

The `chassis.observability` configuration in `application.yaml` is for Chassis framework features (like trace header
exposure, path filtering), not for OTLP export configuration:

```yaml
chassis:
  observability:
    enabled: true  # Enable/disable Chassis observability features
    traces:
      exposeHeaders: true  # Add Trace-Id/Span-Id to response headers
      ignoredPaths:  # Don't trace these paths
        - /actuator/**
    common-tags:
      chassis: true  # Add custom tags to metrics
```

## Export Frequency

- **Metrics:** Every 60 seconds (configurable via `step`)
- **Traces:** Real-time or batched (5s delay or 512 spans)
- **Logs:** Batched (every few seconds with activity)
