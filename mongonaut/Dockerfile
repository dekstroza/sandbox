FROM oracle/graalvm-ce:1.0.0-rc14 as graalvm
ARG JAR_FILE
ADD /target/${JAR_FILE} /home/app/mongonaut.jar
WORKDIR /home/app
RUN native-image --no-server --static --enable-http --enable-https --class-path /home/app/mongonaut.jar
#FROM scratch
FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/mongonaut .
ENTRYPOINT ["./mongonaut"]
