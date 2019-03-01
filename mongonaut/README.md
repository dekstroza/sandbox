# Example of mongo backed http endpoint compiled to native binary using Graal

## Requirements
1. sdkman
2. oracle jdk 1.8 with graal
3. maven (or use provided mvnw)
3. docker
4. helm
5. kubernetes

## Build instructions

1. Build project using ./mvwn clean install
2. To build native image, first run  ```java -cp target/mongonaut-0.1.jar
   io.micronaut.graal.reflect.GraalClassLoadingAnalyzer```
3. Previous step will produce reflect.json in target folder
4. Edit reflect.json and add this ``` {
  "name" : "com.sun.crypto.provider.HmacCore$HmacSHA256",
  "allDeclaredConstructors" : true
}```
5. Build native image with ```./just-native.sh```

## Testing it all works

1. Start mongodb using provided script in kubernetes folder (requires helm)
2. Execute ./mongonaut binary
3. Add some data by executing ```curl -X POST
   localhost:8080/mongonaut/conferences -d '{"id": 42,"name": "GraalCon"}' -H
   'Content-Type:application/json'```
4. Find all data in the database with ```curl
   http://localhost:8080/mongonaut/conferences```

## Comparing results

Original jar is also runnable, it can be ran with java 1.8 with followng
command (after building project) ```java -jar target/mongonaut-0.1.jar```



