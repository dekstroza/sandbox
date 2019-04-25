package io.dekstroza;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "io.jaegertracing.internal.JaegerTracer")
public final class Target_JaegerTracer {
    @Substitute
    private static String loadVersion() {
        return "Graal-1.0.0.rc15";
    }
}
