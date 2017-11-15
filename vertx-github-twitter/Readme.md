## Build minimal docker images with customized jdk9


### Code Build instructions:

1. Before building application, create inside github-twitter-service module, next to pom.xml file called twitter-creds.properties and populate it with your credentials for twitter api.
File should look like this:
```
consumer.key=YOURKEYHERE
consumer.secret=YOURSECRETHERE
```
You will need consumer key and consumer secret, more info on obtaining these here: https://apps.twitter.com
2. Once twitter-creds.properties is created, you can run mvn clean install
3. Run the build:
```
mvn clean install
```

### Build your own base jdk-9 alpine image for first stage builds:
1. Download alpine linux jdk9 from here: http://jdk.java.net/9/ea
2. Create Dockerfile like this:
```
FROM alpine:3.6
ADD jdk-9-ea+181_linux-x64-musl_bin.tar /opt/
```
2. Place the downloaded jdk9 into jdk-9-docker folder, next to Dockerfile

3. Build docker container with:
 ```
 docker build --rm --no-cache -t myown-jdk9:alpine .
 ```
4. In your first stage builds, you can now use FROM myown-jdk9:alpine as packager


### Running instructions
1. After building container, run it with: 
```
#Run container and forward 8080 into container
docker run -d -p 8080:8080 github-twitter-search:1.0.1-SNAPSHOT
#Search for akka projects
curl -v localhost:8080/search/akka
```

