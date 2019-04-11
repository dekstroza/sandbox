FROM oracle/graalvm-ce:1.0.0-rc15 as graalvm
ADD /build/libs/mongonaut-1.0.0-SNAPSHOT.jar /home/app/mongonaut.jar
WORKDIR /home/app
RUN native-image --no-server \
    --delay-class-initialization-to-runtime=io.micronaut.configuration.mongo.reactive.test.AbstractMongoProcessFactory,com.mongodb.UnixServerAddress,com.mongodb.internal.connection.SnappyCompressor \
    --class-path /home/app/mongonaut.jar
FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/mongonaut .
COPY --from=graalvm /opt/graalvm-ce-1.0.0-rc15/jre/lib/amd64/libsunec.so .
ENTRYPOINT ["./mongonaut"]

