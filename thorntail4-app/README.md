# Thorntail 4 Example

Basic Thorntail 4 example with Cassandra and Hibernate OGM

## Requirements

1. OpenJDK 1.8 (until cassandra-unit is available for JDK 11)
2. Maven 3.5.3 or greater
3. Docker (if building container)

## Build instructions

To include docker container build:
```
./mvnw clean install -Ddockerfile.skip=false
```

or to skip docker container
```
./mvnw clean install
```
