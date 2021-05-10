package graphql.kickstart.spring.web.boot.metrics;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.tracing.TracingInstrumentation;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/** @author Bruno Rodrigues */
public class MetricsInstrumentation extends TracingInstrumentation {

  private static final String QUERY_TIME_METRIC_NAME = "graphql.timer.query";
  private static final String RESOLVER_TIME_METRIC_NAME = "graphql.timer.resolver";
  private static final String OPERATION_NAME_TAG = "operationName";
  private static final String OPERATION = "operation";
  private static final String UNKNOWN_OPERATION_NAME = "unknown";
  private static final String PARENT = "parent";
  private static final String FIELD = "field";
  private static final String TRACING = "tracing";
  private static final String DURATION = "duration";
  private static final String EXECUTION = "execution";
  private static final String VALIDATION = "validation";
  private static final String PARSING = "parsing";
  private static final String RESOLVERS = "resolvers";
  private static final String TIMER_DESCRIPTION =
      "Timer that records the time to fetch the data by Operation Name";
  private final MeterRegistry meterRegistry;
  private final boolean tracingEnabled;

  public MetricsInstrumentation(MeterRegistry meterRegistry, boolean tracingEnabled) {
    this.meterRegistry = meterRegistry;
    this.tracingEnabled = tracingEnabled;
  }

  @Override
  public CompletableFuture<ExecutionResult> instrumentExecutionResult(
      ExecutionResult executionResult, InstrumentationExecutionParameters parameters) {

    if (executionResult.getExtensions() != null
        && executionResult.getExtensions().containsKey(TRACING)) {

      Map<String, Object> tracingData =
          (Map<String, Object>) executionResult.getExtensions().get(TRACING);
      Timer executionTimer = buildQueryTimer(parameters.getOperation(), EXECUTION);
      executionTimer.record((long) tracingData.get(DURATION), TimeUnit.NANOSECONDS);

      // These next 2 ifs might not run if the document is cached on the document provider
      if (tracingData.containsKey(VALIDATION)
          && ((Map<String, Object>) tracingData.get(VALIDATION)).containsKey(DURATION)) {
        Timer validationTimer = buildQueryTimer(parameters.getOperation(), VALIDATION);
        validationTimer.record(
            (long) ((Map<String, Object>) tracingData.get(VALIDATION)).get(DURATION),
            TimeUnit.NANOSECONDS);
      }
      if (tracingData.containsKey(PARSING)
          && ((Map<String, Object>) tracingData.get(PARSING)).containsKey(DURATION)) {
        Timer parsingTimer = buildQueryTimer(parameters.getOperation(), PARSING);
        parsingTimer.record(
            (long) ((Map<String, Object>) tracingData.get(PARSING)).get(DURATION),
            TimeUnit.NANOSECONDS);
      }

      if (((Map<String, String>) tracingData.get(EXECUTION)).containsKey(RESOLVERS)) {

        ((List<Map<String, Object>>)
                ((Map<String, Object>) tracingData.get(EXECUTION)).get(RESOLVERS))
            .forEach(
                field -> {
                  Timer fieldTimer =
                      buildFieldTimer(
                          parameters.getOperation(),
                          RESOLVERS,
                          (String) field.get("parentType"),
                          (String) field.get("fieldName"));
                  fieldTimer.record((long) field.get(DURATION), TimeUnit.NANOSECONDS);
                });
      }

      if (!tracingEnabled) {
        executionResult.getExtensions().remove(TRACING);
      }
    }

    return CompletableFuture.completedFuture(executionResult);
  }

  private Timer buildQueryTimer(String operationName, String operation) {
    return Timer.builder(QUERY_TIME_METRIC_NAME)
        .description(TIMER_DESCRIPTION)
        .tag(OPERATION_NAME_TAG, operationName != null ? operationName : UNKNOWN_OPERATION_NAME)
        .tag(OPERATION, operation)
        .register(meterRegistry);
  }

  private Timer buildFieldTimer(
      String operationName, String operation, String parent, String field) {
    return Timer.builder(RESOLVER_TIME_METRIC_NAME)
        .description(TIMER_DESCRIPTION)
        .tag(OPERATION_NAME_TAG, operationName != null ? operationName : UNKNOWN_OPERATION_NAME)
        .tag(PARENT, parent)
        .tag(FIELD, field)
        .tag(OPERATION, operation)
        .register(meterRegistry);
  }
}
