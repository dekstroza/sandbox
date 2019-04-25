package io.dekstroza;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.jaegertracing.internal.metrics.NoopMetricsFactory;
import io.jaegertracing.spi.MetricsFactory;

@TargetClass(className = "io.jaegertracing.Configuration")
public final class Target_Configuration {
    @Substitute
    private MetricsFactory loadMetricsFactory() {
        System.out.println("--> Creating NoopMetricsFactory <--");
        return new NoopMetricsFactory();
    }
}
