FROM oracle/graalvm-ce as graalvm
ARG JAR_FILE
ARG BUILD_DIR
ADD ${BUILD_DIR}/${JAR_FILE} /home/app/mongonaut.jar
WORKDIR /home/app
RUN gu install native-image && \
    native-image --no-server \
    --initialize-at-run-time="io.micronaut.configuration.mongo.reactive.test.AbstractMongoProcessFactory, \
	com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor, \
	io.micronaut.tracing.brave.BraveTracerFactory, \
	io.micronaut.tracing.brave.instrument.http.HttpTracingFactory, \
	io.micronaut.tracing.brave.log.Slf4jCurrentTraceContextFactory, \
	io.micronaut.tracing.brave.sender.HttpClientSenderFactory, \
	io.micronaut.tracing.instrument.rxjava.RxJava1TracingInstrumentation" \
	--initialize-at-build-time=io.micrometer.core,io.micrometer.prometheus,io.micrometer.shaded.org.pcollections \
    --class-path /home/app/mongonaut.jar
FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/mongonaut .
COPY --from=graalvm /opt/graalvm-ce-19.0.0/jre/lib/amd64/libsunec.so .
ENTRYPOINT ["./mongonaut"]

