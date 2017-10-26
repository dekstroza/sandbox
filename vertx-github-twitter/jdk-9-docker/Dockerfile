FROM alpine:3.6 as packager
ADD jdk-9-ea+181_linux-x64-musl_bin.tar /opt/


RUN /opt/jdk-9/bin/jlink \
	--module-path /opt/jdk-9/jmods \
	--verbose \
	--add-modules java.base,java.compiler,java.logging,java.naming,java.sql,java.xml,jdk.unsupported \
	--compress 2 \
	--no-header-files \
	--output /opt/jdk-9-minimal

FROM alpine:3.6
COPY --from=packager /opt/jdk-9-minimal /opt/jdk-9-minimal
COPY github-twitter-service-1.0.1-SNAPSHOT-fat.jar /opt/

ENV JAVA_HOME=/opt/jdk-9-minimal
ENV PATH="$PATH:$JAVA_HOME/bin"

EXPOSE 8080
CMD java -jar /opt/github-twitter-service-1.0.1-SNAPSHOT-fat.jar
