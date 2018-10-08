# alpine jdk-11 from my repo
FROM dekstroza/alpine-11-jdk as jvm-packager
# create new custom jdk with only these modules, to reduce size
RUN jlink \
    --module-path /jdk-11/jmods \
    --verbose \
    --add-modules java.base,java.logging,java.xml,java.naming,java.desktop,java.management,java.datatransfer,java.sql,jdk.unsupported \
    --compress 2 \
    --no-header-files \
    --output /jdk-11-minimal
# Second stage, build my container
FROM alpine:3.8
MAINTAINER Dejan Kitic <kdejan@gmail.com>
RUN addgroup -g 998 appuser && \
    adduser -D -u 998 -G appuser appuser
ENTRYPOINT ["./run.sh"]
ARG DIR
ARG JAR
ENV LANG C.UTF-8
ENV JAVA_HOME="/jdk-11-minimal"
ENV PATH=$PATH:${JAVA_HOME}/bin
COPY --from=jvm-packager /jdk-11-minimal /jdk-11-minimal
USER appuser
ADD target/${DIR} /${artifactId}
WORKDIR /${artifactId}/bin
ADD target/${JAR} /${artifactId}/app/
