package io.dekstroza;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.jaegertracing.internal.metrics.Counter;
import io.jaegertracing.internal.metrics.Gauge;
import io.jaegertracing.spi.MetricsFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@TargetClass(className = "io.jaegertracing.internal.metrics.Metrics")
public final class Target_Metrics {
    @Alias
    public Counter traceStartedSampled;

    @Alias
    public Counter traceStartedNotSampled;

    @Alias
    public Counter tracesJoinedSampled;

    @Alias
    public Counter tracesJoinedNotSampled;

    @Alias
    public Counter spansStartedSampled;

    @Alias
    public Counter spansStartedNotSampled;

    @Alias
    public Counter spansFinished;

    @Alias
    public Counter decodingErrors;

    @Alias
    public Counter reporterSuccess;

    @Alias
    public Counter reporterFailure;

    @Alias
    public Counter reporterDropped;

    @Alias
    public Gauge reporterQueueLength;

    @Alias
    public Counter samplerRetrieved;

    @Alias
    public Counter samplerQueryFailure;

    @Alias
    public Counter samplerUpdated;

    @Alias
    public Counter samplerParsingFailure;

    @Alias
    public Counter baggageUpdateSuccess;

    @Alias
    public Counter baggageUpdateFailure;

    @Alias
    public Counter baggageTruncate;

    @Alias
    public Counter baggageRestrictionsUpdateSuccess;

    @Alias
    public Counter baggageRestrictionsUpdateFailure;

    @Substitute
    private void createMetrics(MetricsFactory factory, String metricsPrefix) {
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("state", "started");
        tags.put("sampled", "y");
        traceStartedSampled = factory.createCounter("traces", tags);

        tags = new HashMap<String, String>();
        tags.put("state", "started");
        tags.put("sampled", "n");
        traceStartedNotSampled = factory.createCounter("traces", tags);

        tags = new HashMap<String, String>();
        tags.put("state", "joined");
        tags.put("sampled", "y");
        tracesJoinedSampled = factory.createCounter("traces", tags);

        tags = new HashMap<String, String>();
        tags.put("state", "joined");
        tags.put("sampled", "n");
        tracesJoinedNotSampled = factory.createCounter("traces", tags);

        tags = new HashMap<String, String>();
        tags.put("sampled", "y");
        spansStartedSampled = factory.createCounter("started_spans", tags);

        tags = new HashMap<String, String>();
        tags.put("sampled", "n");
        spansStartedNotSampled = factory.createCounter("started_spans", tags);

        spansFinished = factory.createCounter("finished_spans", Collections.emptyMap());

        decodingErrors = factory.createCounter("span_context_decoding_errors", Collections.emptyMap());

        tags = new HashMap<String, String>();
        tags.put("result", "ok");
        reporterSuccess = factory.createCounter("reporter_spans", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "err");
        reporterFailure = factory.createCounter("reporter_spans", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "dropped");
        reporterDropped = factory.createCounter("reporter_spans", tags);

        reporterQueueLength = factory.createGauge("reporter_queue_length", Collections.emptyMap());

        tags = new HashMap<String, String>();
        tags.put("result", "ok");
        samplerRetrieved = factory.createCounter("sampler_queries", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "err");
        samplerQueryFailure = factory.createCounter("sampler_queries", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "ok");
        samplerUpdated = factory.createCounter("sampler_updates", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "err");
        samplerParsingFailure = factory.createCounter("sampler_updates", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "ok");
        baggageUpdateSuccess = factory.createCounter("baggage_updates", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "err");
        baggageUpdateFailure = factory.createCounter("baggage_updates", tags);

        baggageTruncate = factory.createCounter("baggage_truncations", Collections.emptyMap());

        tags = new HashMap<String, String>();
        tags.put("result", "ok");
        baggageRestrictionsUpdateSuccess = factory.createCounter("baggage_restrictions_updates", tags);

        tags = new HashMap<String, String>();
        tags.put("result", "err");
        baggageRestrictionsUpdateFailure = factory.createCounter("baggage_restrictions_updates", tags);
    }
}
